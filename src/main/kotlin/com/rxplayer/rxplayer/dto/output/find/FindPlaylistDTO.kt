package com.rxplayer.rxplayer.dto.output.find

import java.time.LocalDate

data class FindPlaylistDTO(
    var id: String? = null,
    var name: String,
    var createdBy: FindUserDTO,
    var createAt: LocalDate
)