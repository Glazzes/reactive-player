package com.rxplayer.rxplayer.util

import com.rxplayer.rxplayer.entities.User
import com.rxplayer.rxplayer.security.SecurityUserAdapter
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.awaitPrincipal

@Component
class AuthUtil {

    companion object{
        suspend fun getAuthenticatedUserFromRequest(serverRequest: ServerRequest): User {
            val authToken = serverRequest.awaitPrincipal() as? UsernamePasswordAuthenticationToken?
                ?: throw IllegalStateException("User is not authenticated")

           return (authToken.principal as SecurityUserAdapter).user
        }

        suspend fun canPerformAction(compareWith: User?): Boolean {
            val user = ReactiveSecurityContextHolder.getContext()
                .map { (it.authentication.principal as SecurityUserAdapter).user }
                .awaitSingleOrNull() ?: return false

            if(user.email != compareWith?.email){
                return false
            }

            return true
        }
    }

}