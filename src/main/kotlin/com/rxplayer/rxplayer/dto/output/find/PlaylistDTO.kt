package com.rxplayer.rxplayer.dto.output.find

import java.time.LocalDate

class PlaylistDTO private constructor(
    val id: String? = null,
    val name: String? = null,
    val createdBy: UserDTO? = null,
    val createdAt: LocalDate? = null
){
    companion object {
        fun builder() = Builder()
    }

    class Builder(
        private var id: String? = null,
        private var name: String? = null,
        private var createdBy: UserDTO? = null,
        private var createdAt: LocalDate? = null
    ){
        fun id(id: String?) = apply { this.id = id }
        fun name(name: String) = apply { this.name = name }
        fun createdBy(createdBy: UserDTO?) = apply { this.createdBy = createdBy }
        fun createdAt(createdAt: LocalDate?) = apply { this.createdAt = createdAt }
        fun build() = PlaylistDTO(id, name, createdBy, createdAt)
    }
}