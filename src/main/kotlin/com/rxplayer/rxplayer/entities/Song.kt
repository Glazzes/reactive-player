package com.rxplayer.rxplayer.entities

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "songs")
class Song(
    @Id
    var id: String? = null,
    var title: String,
    var cover: String,
    val containedInPlaylists: MutableSet<String> = HashSet(),
    val metadata: EntityMetadata
)