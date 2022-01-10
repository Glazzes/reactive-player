package com.rxplayer.rxplayer.dto.output.created

import com.rxplayer.rxplayer.dto.output.find.FindUserDTO
import java.time.LocalDate

data class CreatedAlbumDTO(
    var id: String?,
    var title: String,
    var createdBy: FindUserDTO,
    var createdAt: LocalDate?
)