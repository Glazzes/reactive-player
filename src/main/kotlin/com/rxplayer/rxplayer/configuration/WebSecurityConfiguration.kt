package com.rxplayer.rxplayer.configuration

import org.springframework.context.annotation.Bean
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService
import org.springframework.security.core.userdetails.ReactiveUserDetailsService
import org.springframework.security.core.userdetails.User
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.web.cors.CorsConfiguration

@EnableWebFluxSecurity
class WebSecurityConfiguration {

    @Bean
    fun securityFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
        http.authorizeExchange {
            it.pathMatchers("/user/**").permitAll()
                .anyExchange().authenticated()
            }
            .cors {
                it.configurationSource {
                    val configuration = CorsConfiguration()
                    configuration.maxAge = 3600
                    configuration.allowCredentials = true
                    configuration.allowedMethods = listOf("POST", "GET", "PATCH", "PUT", "DELETE", "OPTIONS")
                    configuration.allowedOrigins = listOf("http://localhost:19006")

                    configuration
                }
            }
            .csrf { it.disable() }

        http.httpBasic()

        return http.build()
    }

    @Bean
    fun userDetailsService(): ReactiveUserDetailsService{
        val user = User.withUsername("glaze")
            .password(passwordEncoder().encode("pass"))
            .authorities("rw")
            .build()

        return MapReactiveUserDetailsService(user)
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder{
        return BCryptPasswordEncoder()
    }

}