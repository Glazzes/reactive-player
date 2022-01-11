package com.rxplayer.rxplayer.exception

import org.springframework.security.core.AuthenticationException

class InvalidAuthenticationProviderException(message: String) : AuthenticationException(message)