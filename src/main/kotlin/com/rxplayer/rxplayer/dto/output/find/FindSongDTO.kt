package com.rxplayer.rxplayer.dto.output.find

import java.time.LocalDate

data class FindSongDTO(
    val id: String?,
    val title: String,
    val createdBy: FindUserDTO?,
    val cratedAt: LocalDate?,
)