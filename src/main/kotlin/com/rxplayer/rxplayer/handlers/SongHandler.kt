package com.rxplayer.rxplayer.handlers

import com.rxplayer.rxplayer.dto.input.SongRequest
import com.rxplayer.rxplayer.dto.output.CreatedSongDTO
import com.rxplayer.rxplayer.dto.output.FindSongDTO
import com.rxplayer.rxplayer.entities.EntityMetadata
import com.rxplayer.rxplayer.entities.Song
import com.rxplayer.rxplayer.exception.NotFoundException
import com.rxplayer.rxplayer.repositories.SongRepository
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.http.HttpStatus
import org.springframework.http.codec.multipart.FilePart
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty
import java.nio.charset.Charset
import java.nio.file.Paths

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
            .switchIfEmpty { Mono.error(NotFoundException("Song with id $songId does not exists")) }

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

    /*
    Handling multipart uploads with functional endpoints, i ended up with this solution
    as i could not find one detailing a better way to do it or even how it's done
     */
    suspend fun handleFileUpload(serverRequest: ServerRequest): ServerResponse {
        val partData = serverRequest.awaitMultipartData().toSingleValueMap()
        val filename = partData["filename"]!!.content()
            .map { Pair("filename", it.toString(Charset.defaultCharset())) }

        val filename2 = partData["filename2"]!!.content()
            .map { Pair("filename2", it.toString(Charset.defaultCharset())) }

        val file = partData["file"]!! as FilePart

        val result = Flux.concat(filename, filename2)
            .collectMap({it.first}, {it.second})
            .flatMap {
                file.transferTo(Paths.get("/home/glaze/${it["filename"]}-${it["filename2"]}.png"))
            }

        return ServerResponse.status(HttpStatus.NO_CONTENT)
            .bodyAndAwait(result.asFlow())
    }

}