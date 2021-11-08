package com.rxplayer.rxplayer.service

import com.rxplayer.rxplayer.dto.input.SongRequest
import com.rxplayer.rxplayer.dto.output.CreatedSongDTO
import com.rxplayer.rxplayer.dto.output.FindSongDTO
import com.rxplayer.rxplayer.entities.EntityMetadata
import com.rxplayer.rxplayer.entities.Song
import com.rxplayer.rxplayer.exception.NotFoundException
import com.rxplayer.rxplayer.repositories.SongRepository
import kotlinx.coroutines.reactive.asFlow
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyAndAwait
import org.springframework.web.reactive.function.server.buildAndAwait
import reactor.core.publisher.Mono
import reactor.core.publisher.SynchronousSink

@Component
class SongService(private val songRepository: SongRepository){

    suspend fun save(serverRequest: ServerRequest): ServerResponse {
        val songRequest = serverRequest.bodyToMono(SongRequest::class.java)
            .map { Song(title = it.title, metadata = EntityMetadata()) }
            .flatMap { songRepository.save(it) }
            .map { CreatedSongDTO(it.id, it.title) }

        return ServerResponse.status(HttpStatus.CREATED)
            .bodyAndAwait(songRequest.asFlow())
    }

    fun findById(serverRequest: ServerRequest): Mono<ServerResponse> {
        return songRepository.findById(serverRequest.pathVariable("id"))
            .map { FindSongDTO(it.id, it.title) }
            .flatMap { ServerResponse.ok().bodyValue(it) }
            .switchIfEmpty(ServerResponse.notFound().build())
    }

    suspend fun deleteById(serverRequest: ServerRequest): ServerResponse {
        val songId = serverRequest.pathVariable("id")
        val request = songRepository.existsById(songId)
            .flatMap {
                if(!it) {
                    return@flatMap Mono.error(NotFoundException("Song with id $songId does not exists"))
                }

                songRepository.deleteById(songId)
            }

        return ServerResponse.status(HttpStatus.NO_CONTENT)
            .bodyAndAwait(request.asFlow())
    }

}