package com.rxplayer.rxplayer.dto.output.created

import com.rxplayer.rxplayer.dto.output.find.UserDTO
import java.time.LocalDate

data class CreatedAlbumDTO(
    var id: String?,
    var title: String,
    var createdBy: UserDTO,
    var createdAt: LocalDate?
)