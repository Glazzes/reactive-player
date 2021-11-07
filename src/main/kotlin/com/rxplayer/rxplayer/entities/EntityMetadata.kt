package com.rxplayer.rxplayer.entities

import org.springframework.data.annotation.CreatedBy
import org.springframework.data.annotation.CreatedDate
import java.time.LocalDate

data class EntityMetadata(
    @CreatedDate var createdAt: LocalDate? = null,
    @CreatedBy var createdBy: String? = null
)