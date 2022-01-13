package com.rxplayer.rxplayer.entities

import com.rxplayer.rxplayer.configuration.AuthenticationProvider
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "users")
class User(
    @Id
    var id: String? = null,
    var username: String,
    var nickName: String,
    var email: String,
    var password: String?,
    var profilePicture: String = "",
    var authenticationProvider: AuthenticationProvider = AuthenticationProvider.RX_PLAYER
)