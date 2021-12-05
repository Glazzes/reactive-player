@file:Suppress("DEPRECATION")
package com.rxplayer.rxplayer.exception

import org.springframework.boot.autoconfigure.web.WebProperties
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler
import org.springframework.boot.web.error.ErrorAttributeOptions
import org.springframework.boot.web.reactive.error.ErrorAttributes
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.http.codec.ServerCodecConfigurer
import org.springframework.web.reactive.function.server.*

@Configuration
class GlobalErrorWebExceptionHandler(
    errorAttributes: ErrorAttributes?,
    resources: WebProperties.Resources?,
    applicationContext: ApplicationContext?,
    configurer: ServerCodecConfigurer
) : AbstractErrorWebExceptionHandler(errorAttributes, resources, applicationContext) {
    init {
        super.setMessageReaders(configurer.readers)
        super.setMessageWriters(configurer.writers)
    }

    private val defaultAttributes = ErrorAttributeOptions.of(
            ErrorAttributeOptions.Include.BINDING_ERRORS,
            ErrorAttributeOptions.Include.MESSAGE)

    override fun getRoutingFunction(errorAttributes: ErrorAttributes?): RouterFunction<ServerResponse> = coRouter {
        DELETE("/song/*") {handleNotFoundException(it)}
        GET("/user/*") { handleNotFoundException(it) }
        POST("/user") { handleResourceAlreadyExistsException(it) }
    }

    private suspend fun handleNotFoundException(serverRequest: ServerRequest): ServerResponse{
        val errors = getErrorAttributes(serverRequest, defaultAttributes)
        errors["status"] = HttpStatus.CONFLICT.value()
        mutableListOf("error", "requestId").forEach { errors.remove(it) }

        return ServerResponse.status(HttpStatus.NOT_FOUND)
            .bodyValueAndAwait(errors)
    }

    private suspend fun handleResourceAlreadyExistsException(serverRequest: ServerRequest): ServerResponse {
        val errors = getErrorAttributes(serverRequest, defaultAttributes)
        errors["status"] = HttpStatus.CONFLICT.value()
        mutableListOf("error", "requestId").forEach { errors.remove(it) }

        return ServerResponse.status(HttpStatus.CONFLICT)
            .bodyValueAndAwait(errors)
    }
}