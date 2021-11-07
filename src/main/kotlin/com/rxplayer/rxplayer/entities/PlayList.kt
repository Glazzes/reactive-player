package com.rxplayer.rxplayer.entities

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "playlists")
data class PlayList(
    @Id
    var id: String? = null,
    var name: String,
    var metadata: EntityMetadata
)