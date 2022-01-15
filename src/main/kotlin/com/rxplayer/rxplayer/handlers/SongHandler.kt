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
import org.springframework.http.HttpStatus
import org.springframework.http.codec.multipart.FilePart
import org.springframework.http.codec.multipart.FormFieldPart
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.*
import java.nio.file.Paths
import java.util.*

@Component
class SongHandler( private val songRepository: SongRepository ){
    private val musicFolder: String = "/home/glaze/rx-player/music/"

    suspend fun save(serverRequest: ServerRequest): ServerResponse {
        val fileId = UUID.randomUUID().toString()
        val filePath = "${musicFolder}${fileId}.mp3"

        val partData = serverRequest.awaitMultipartData().toSingleValueMap()
        val file = partData["file"]!! as FilePart
        val songName = (partData["title"]!! as FormFieldPart).value()

        file.transferTo(Paths.get(filePath))
            .subscribe { println("File transferred to $filePath") }

        val newSong = songRepository.save(Song(title = songName, metadata = EntityMetadata()))
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

            FindSongDTO(song.id, song.title, createdBy, song.metadata.createdAt)
            ServerResponse.ok().bodyValueAndAwait(song)
        } ?: ServerResponse.notFound().buildAndAwait()
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