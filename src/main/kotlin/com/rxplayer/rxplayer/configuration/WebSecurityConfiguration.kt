package com.rxplayer.rxplayer.configuration

import com.rxplayer.rxplayer.configuration.oauth2.Oauth2SuccessHandler
import org.springframework.context.annotation.Bean
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.web.cors.CorsConfiguration

@EnableWebFluxSecurity
class WebSecurityConfiguration(
    private val oauth2SuccessHandler: Oauth2SuccessHandler
){

    @Bean
    fun securityFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
        http.authorizeExchange {
            it.pathMatchers("/login").permitAll()
                .pathMatchers("/user/**").permitAll()
                .pathMatchers(HttpMethod.GET, "/playlist/{id}").permitAll()
                .pathMatchers(HttpMethod.GET, "/song/{id}").permitAll()
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
            .httpBasic()
            .and()
            .oauth2Login {
                it.authenticationSuccessHandler(oauth2SuccessHandler)
            }

        return http.build()
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder{
        return BCryptPasswordEncoder()
    }

}