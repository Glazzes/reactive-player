package com.rxplayer.rxplayer.handlers

import com.rxplayer.rxplayer.dto.output.created.CreatedSongDTO
import com.rxplayer.rxplayer.dto.output.find.FindSongDTO
import com.rxplayer.rxplayer.dto.output.find.FindUserDTO
import com.rxplayer.rxplayer.entities.EntityMetadata
import com.rxplayer.rxplayer.entities.Song
import com.rxplayer.rxplayer.exception.InvalidOperationException
import com.rxplayer.rxplayer.exception.NotFoundException
import com.rxplayer.rxplayer.repositories.SongRepository
import com.rxplayer.rxplayer.util.AuthUtil
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.codec.multipart.FilePart
import org.springframework.http.codec.multipart.FormFieldPart
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.*
import java.nio.file.Paths
import java.util.*

@Component
class SongHandler( private val songRepository: SongRepository ){
    @Value("\${rx-player.music-folder}")
    private lateinit var musicFolder: String

    suspend fun save(serverRequest: ServerRequest): ServerResponse {
        val fileId = UUID.randomUUID().toString()
        val filePath = "${musicFolder}${fileId}.mp3"

        val partData = serverRequest.awaitMultipartData().toSingleValueMap()
        val file = partData["file"]!! as FilePart
        val title = (partData["title"]!! as FormFieldPart).value()

        file.transferTo(Paths.get(filePath))
            .subscribe { println("File transferred to $filePath") }

        val url = "http://localhost:8080/files/music/${fileId}"
        val newSong = songRepository.save(Song(title = title, cover = url, metadata = EntityMetadata()))
        val songDto = CreatedSongDTO(newSong.id, newSong.title)
        return ServerResponse.status(HttpStatus.CREATED)
            .bodyValueAndAwait(songDto)
    }

    suspend fun findById(serverRequest: ServerRequest): ServerResponse {
        val songId = serverRequest.pathVariable("id")
        val song = songRepository.findById(songId)

        return song?.let {
            val metadata = song.metadata.createdBy ?: throw IllegalStateException("")
            val createdBy = FindUserDTO(
                metadata.id,
                metadata.username,
                metadata.email,
                metadata.profilePicture)

            val dto = FindSongDTO(song.id, song.title, song.cover, createdBy, song.metadata.createdAt)
            ServerResponse.ok().bodyValueAndAwait(dto)
        } ?: ServerResponse.notFound().buildAndAwait()
    }

    suspend fun rename(serverRequest: ServerRequest): ServerResponse {
        val songId = serverRequest.pathVariable("id")
        val newTitle = serverRequest.queryParamOrNull("title") ?:
            throw NotFoundException("Parameter title is required")

        val song = songRepository.findById(songId) ?:
            return ServerResponse.status(HttpStatus.NOT_FOUND).buildAndAwait()

        val createdBy = song.metadata.createdBy
        if(!AuthUtil.canPerformAction(createdBy)){
            throw InvalidOperationException("You can not delete a song that do not own")
        }

        song.apply { title = newTitle }
        val saved = songRepository.save(song)
        val metadata = song.metadata.createdBy ?: throw IllegalStateException("")

        val creator = FindUserDTO(
            metadata.id,
            metadata.username,
            metadata.email,
            metadata.profilePicture)

        val dto = FindSongDTO(song.id, song.title, saved.cover, creator, song.metadata.createdAt)
        return ServerResponse.status(HttpStatus.OK)
            .bodyValueAndAwait(dto)

    }

    suspend fun delete(serverRequest: ServerRequest): ServerResponse {
        val songId = serverRequest.pathVariable("id")
        val song = songRepository.findById(songId) ?:
            throw NotFoundException("Can not delete song with id ${songId}, because it does not exits")

        val createdBy = song.metadata.createdBy
        if(!AuthUtil.canPerformAction(createdBy)){
            throw InvalidOperationException("You can not delete a song that do not own")
        }

        songRepository.delete(song)
        return ServerResponse.status(HttpStatus.NO_CONTENT).buildAndAwait()
    }
}