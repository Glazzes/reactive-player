package com.rxplayer.rxplayer.handlers

import com.rxplayer.rxplayer.dto.output.find.FindSongDTO
import com.rxplayer.rxplayer.dto.output.find.FindUserDTO
import com.rxplayer.rxplayer.entities.EntityMetadata
import com.rxplayer.rxplayer.entities.Song
import com.rxplayer.rxplayer.exception.NotFoundException
import com.rxplayer.rxplayer.repositories.SongRepository
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.http.HttpStatus
import org.springframework.http.codec.multipart.FilePart
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.*
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty
import java.nio.charset.StandardCharsets
import java.nio.file.Paths
import java.util.*

@Component
class SongHandler(private val songRepository: SongRepository){
    private val musicFolder: String = "/home/glaze/rx-player/music/"

    suspend fun save(serverRequest: ServerRequest): ServerResponse {
        val fileId = UUID.randomUUID().toString()
        val filePath = "${musicFolder}${fileId}.mp3"

        val partData = serverRequest.awaitMultipartData().toSingleValueMap()
        val file = partData["file"]!! as FilePart

        val songRequest = partData["title"]!!.content()
            .map{ it.toString(StandardCharsets.UTF_8) }
            .map { Song(title = it, metadata = EntityMetadata()) }
            .flatMap { songRepository.save(it) }
            .flatMap { file.transferTo(Paths.get(filePath)) }

        return ServerResponse.status(HttpStatus.CREATED)
            .bodyAndAwait(songRequest.asFlow())
    }

    /*
    Something replacing switchIfEmpty() with switchIfEmpty{  } causes the compiler to not know
    what the fuck is going so i am
     */
    suspend fun findById(serverRequest: ServerRequest): ServerResponse {
        val songId = serverRequest.pathVariable("id")
        val song = songRepository.findById(songId)
            .map {
                val metadata = it.metadata.createdBy ?: throw IllegalStateException("")
                val createdBy = FindUserDTO(
                    metadata.id,
                    metadata.nickName,
                    metadata.nickName,
                    metadata.profilePicture)

                FindSongDTO(it.id, it.title, createdBy, it.metadata.createdAt)
            }
            .switchIfEmpty { Mono.error(NotFoundException("Song with id $songId does not exists")) }

        return ServerResponse.ok()
            .bodyValueAndAwait(song.awaitSingle())
    }

    suspend fun deleteById(serverRequest: ServerRequest): ServerResponse {
        val songId = serverRequest.pathVariable("id")
        val request = songRepository.existsById(songId)
            .handle <Boolean>{ exists, sink ->
                if(exists){
                    sink.next(exists)
                }else{
                    sink.error(NotFoundException("Song with id $songId does not exists"))
                }
            }
            .flatMap { songRepository.deleteById(songId) }
            .map { "Song with id $songId has been successfully deleted." }

        return ServerResponse.status(HttpStatus.NO_CONTENT)
            .bodyAndAwait(request.asFlow())
    }
}