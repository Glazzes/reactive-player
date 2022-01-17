package com.rxplayer.rxplayer.dto.output.find

class UserDTO private constructor(
    val id: String? = null,
    val username: String? = null,
    val email: String? = null,
    val profilePicture: String? = null
){

    companion object{
        fun builder(): Builder {
            return Builder()
        }
    }

    class Builder(
        private var id: String? = null,
        private var username: String? = null,
        private var email: String? = null,
        private var profilePicture: String? = null
    ){
        fun id(id: String?) = apply { this.id = id }
        fun username(username: String) = apply { this.username = username }
        fun email(email: String) = apply { this.email = email }
        fun profilePicture(profilePicture: String) = apply { this.profilePicture = profilePicture }
        fun build() = UserDTO(id, username, email, profilePicture)
    }

}