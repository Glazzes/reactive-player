package com.rxplayer.rxplayer.exception

import javax.naming.AuthenticationException

class InvalidJwtException(message: String?) : AuthenticationException(message)