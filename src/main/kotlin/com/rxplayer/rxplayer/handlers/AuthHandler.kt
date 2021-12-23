package com.rxplayer.rxplayer.handlers

import com.rxplayer.rxplayer.configuration.SecurityUserAdapter
import com.rxplayer.rxplayer.dto.output.FindUserDTO
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyValueAndAwait

@Component
class AuthHandler {

    suspend fun getAuthenticatedUser(serverRequest: ServerRequest): ServerResponse {
        val authenticatedUser = ReactiveSecurityContextHolder.getContext()
            .map { (it.authentication.principal as SecurityUserAdapter).user }
            .map { FindUserDTO(it.id, it.nickName, it.email, it.profilePicture) }

        return ServerResponse.ok()
            .bodyValueAndAwait(authenticatedUser.awaitSingle())
    }

}