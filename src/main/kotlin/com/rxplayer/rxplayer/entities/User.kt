package com.rxplayer.rxplayer.entities

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "users")
class User(
    @Id
    var id: String? = null,
    var username: String,
    var nickName: String,
    var password: String,
    var email: String,
    var profilePicture: String
)