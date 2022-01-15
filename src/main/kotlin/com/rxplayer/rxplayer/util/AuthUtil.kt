package com.rxplayer.rxplayer.util

import com.rxplayer.rxplayer.entities.User
import com.rxplayer.rxplayer.security.SecurityUserAdapter
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.stereotype.Component

@Component
class AuthUtil {

    companion object{
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