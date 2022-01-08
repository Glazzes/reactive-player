package com.rxplayer.rxplayer.entities

import com.rxplayer.rxplayer.configuration.SecurityUserAdapter
import org.springframework.data.domain.ReactiveAuditorAware
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class Auditor : ReactiveAuditorAware<User> {
    override fun getCurrentAuditor(): Mono<User> {
        return ReactiveSecurityContextHolder.getContext()
            .map { it.authentication.principal as SecurityUserAdapter }
            .map { it.user }
    }
}