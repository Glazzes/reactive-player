package com.rxplayer.rxplayer.exception

import org.springframework.security.core.AuthenticationException

class InvalidCredentialsException(message: String): AuthenticationException(message)