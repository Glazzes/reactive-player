package com.rxplayer.rxplayer.handlers

import com.rxplayer.rxplayer.dto.input.SongRequest
import com.rxplayer.rxplayer.dto.output.CreatedSongDTO
import com.rxplayer.rxplayer.dto.output.FindSongDTO
import com.rxplayer.rxplayer.entities.EntityMetadata
import com.rxplayer.rxplayer.entities.Song
import com.rxplayer.rxplayer.exception.NotFoundException
import com.rxplayer.rxplayer.repositories.SongRepository
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyAndAwait
import org.springframework.web.reactive.function.server.bodyValueAndAwait
import reactor.core.publisher.Mono

@Component
class SongHandler(private val songRepository: SongRepository){

    suspend fun save(serverRequest: ServerRequest): ServerResponse {
        val songRequest = serverRequest.bodyToMono(SongRequest::class.java)
            .map { Song(title = it.title, metadata = EntityMetadata()) }
            .flatMap { songRepository.save(it) }
            .map { CreatedSongDTO(it.id, it.title) }

        return ServerResponse.status(HttpStatus.CREATED)
            .bodyValueAndAwait(songRequest.awaitSingle())
    }

    /*
    Something replacing switchIfEmpty() with switchIfEmpty{  } causes the compiler to not know
    what the fuck is going so i am
     */
    suspend fun findById(serverRequest: ServerRequest): ServerResponse {
        val songId = serverRequest.pathVariable("id")
        val song = songRepository.findById(songId)
            .map { FindSongDTO(it.id, it.title) }
            .switchIfEmpty(
                Mono.error(NotFoundException("Song with id $songId was not found."))
            )

        return ServerResponse.ok()
            .bodyValueAndAwait(song.awaitSingle())
    }

    suspend fun deleteById(serverRequest: ServerRequest): ServerResponse {
        val songId = serverRequest.pathVariable("id")
        val request = songRepository.existsById(songId)
            .handle <Boolean>{ exists, sink ->
                if(exists) sink.error(NotFoundException("Song with id $songId does not exists"))
                sink.next(exists)
            }
            .flatMap { songRepository.deleteById(songId) }

        return ServerResponse.status(HttpStatus.NO_CONTENT)
            .bodyValueAndAwait(request.awaitSingle())
    }

}