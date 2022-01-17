package com.rxplayer.rxplayer.dto.output.find

import java.time.LocalDate

class SongDTO private constructor(
    val id: String? = null,
    val title: String? = null,
    val cover: String? = null,
    val createdBy: UserDTO? = null,
    val cratedAt: LocalDate? = null,
){
    companion object {
        fun builder() = Builder()
    }

    class Builder(
        private var id: String? = null,
        private var title: String? = null,
        private var cover: String? = null,
        private var createdBy: UserDTO? = null,
        private var createdAt: LocalDate? = null,
    ){
        fun id(id: String?) = apply { this.id = id }
        fun title(title: String) = apply { this.title = title }
        fun cover(cover: String) = apply { this.cover = cover }
        fun createdBy(createdBy: UserDTO) = apply { this.createdBy = createdBy }
        fun createdAt(cratedAt: LocalDate?) = apply { this.createdAt = cratedAt }
        fun build() = SongDTO(id, title, cover, createdBy, createdAt)
    }
}