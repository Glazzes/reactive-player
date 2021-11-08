package com.rxplayer.rxplayer.entities

import org.springframework.data.domain.ReactiveAuditorAware
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class Auditor : ReactiveAuditorAware<String> {
    override fun getCurrentAuditor(): Mono<String> {
        return ReactiveSecurityContextHolder.getContext()
            .map { it.authentication.name }
    }
}