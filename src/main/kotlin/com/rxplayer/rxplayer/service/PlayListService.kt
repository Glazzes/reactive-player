package com.rxplayer.rxplayer.service

import com.rxplayer.rxplayer.dto.input.PlayListRequest
import com.rxplayer.rxplayer.dto.output.CreatedPlayListDTO
import com.rxplayer.rxplayer.entities.EntityMetadata
import com.rxplayer.rxplayer.entities.PlayList
import com.rxplayer.rxplayer.repositories.PlayListRepository
import kotlinx.coroutines.reactive.asFlow
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyAndAwait

@Service
class PlayListService(private val playListRepository: PlayListRepository) {

    suspend fun save(serverRequest: ServerRequest): ServerResponse {
        val reactive = serverRequest.bodyToMono(PlayListRequest::class.java)
            .map { PlayList(name = it.title, metadata = EntityMetadata()) }
            .flatMap { playListRepository.save(it) }
            .map { CreatedPlayListDTO(it.id, it.name) }

        return ServerResponse.status(HttpStatus.CREATED)
            .bodyAndAwait(reactive.asFlow())
    }

}