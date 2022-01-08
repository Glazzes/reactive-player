package com.rxplayer.rxplayer.entities

import org.springframework.data.annotation.CreatedBy
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.mongodb.core.mapping.DBRef
import java.time.LocalDate

data class EntityMetadata(
    @CreatedDate var createdAt: LocalDate? = null,
    @CreatedBy var createdBy: User? = null
)