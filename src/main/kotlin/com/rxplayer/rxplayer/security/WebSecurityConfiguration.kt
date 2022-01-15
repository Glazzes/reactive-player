package com.rxplayer.rxplayer.security

import com.rxplayer.rxplayer.security.jwt.JwtAuthenticationManager
import com.rxplayer.rxplayer.security.jwt.JwtServerConverter
import com.rxplayer.rxplayer.security.oauth2.Oauth2SuccessHandler
import com.rxplayer.rxplayer.security.util.CustomEntryPoint
import org.springframework.context.annotation.Bean
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.SecurityWebFiltersOrder
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.authentication.AuthenticationWebFilter
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository
import org.springframework.web.cors.CorsConfiguration

@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
class WebSecurityConfiguration(
    private val oauth2SuccessHandler: Oauth2SuccessHandler,
    private val jwtServerConverter: JwtServerConverter,
    private val jwtAuthenticationManager: JwtAuthenticationManager,
){

    @Bean
    fun springSecurityFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
        val authenticationFilter = AuthenticationWebFilter(jwtAuthenticationManager)
        authenticationFilter.setServerAuthenticationConverter(jwtServerConverter)

        http
            .authorizeExchange {
                it.pathMatchers(HttpMethod.GET,
                    "/home/**", "/oauth2/**", "/playlist/{id}",
                    "/song/{id}", "/user", "/playlist/{id}", "/song/{id}"
                    ).permitAll()
                    .pathMatchers(
                        HttpMethod.POST, "/login", "/logout"
                    ).permitAll()
                    .pathMatchers("/oauth2/**").permitAll()
                    .anyExchange().authenticated()
            }
            .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
            .cors {
                it.configurationSource {
                    val configuration = CorsConfiguration()
                    configuration.maxAge = 3600
                    configuration.allowCredentials = true
                    configuration.allowedMethods = listOf("POST", "GET", "PATCH", "PUT", "DELETE", "OPTIONS")
                    configuration.allowedOrigins = listOf("*")

                    configuration
                }
            }
            .csrf { it.disable() }
            .httpBasic { it.disable() }
            .formLogin { it.disable() }
            .logout { it.disable() }
            .exceptionHandling {
                it.authenticationEntryPoint(CustomEntryPoint())
            }
            .oauth2Login {
                it.authenticationSuccessHandler(oauth2SuccessHandler)
                it.securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
            }

        http.addFilterAt(authenticationFilter, SecurityWebFiltersOrder.AUTHENTICATION)

        return http.build()
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

}