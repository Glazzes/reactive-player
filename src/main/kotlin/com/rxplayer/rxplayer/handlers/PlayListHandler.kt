package com.rxplayer.rxplayer.handlers

import com.rxplayer.rxplayer.dto.input.PlaylistActionToSong
import com.rxplayer.rxplayer.dto.input.SongRequest
import com.rxplayer.rxplayer.dto.output.find.PlaylistDTO
import com.rxplayer.rxplayer.dto.output.find.SongDTO
import com.rxplayer.rxplayer.dto.output.find.UserDTO
import com.rxplayer.rxplayer.entities.EntityMetadata
import com.rxplayer.rxplayer.entities.PlayList
import com.rxplayer.rxplayer.exception.InvalidOperationException
import com.rxplayer.rxplayer.exception.NotFoundException
import com.rxplayer.rxplayer.repositories.PlaylistRepository
import com.rxplayer.rxplayer.repositories.SongRepository
import com.rxplayer.rxplayer.util.AuthUtil
import kotlinx.coroutines.flow.map
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.codec.multipart.FilePart
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.*
import java.nio.file.Paths
import java.util.*

@Component
class PlayListHandler(
    private val songRepository: SongRepository,
    private val playlistRepository: PlaylistRepository
){
    @Value("\${rx-player.cover-folder}")
    private lateinit var coverFolder: String

    suspend fun save(serverRequest: ServerRequest): ServerResponse {
        val newPlaylistRequest = serverRequest.awaitBody<SongRequest>()
        val playList = PlayList(name = newPlaylistRequest.title, metadata = EntityMetadata())
        return ServerResponse.status(HttpStatus.CREATED)
            .bodyValueAndAwait(playlistRepository.save(playList))
    }

    suspend fun findById(serverRequest: ServerRequest): ServerResponse {
        val id = serverRequest.pathVariable("id")
        val foundPlaylist = playlistRepository.findById(id)

        return foundPlaylist?.let {
            val createdBy = it.metadata.createdBy ?: throw NotFoundException("User not found.")

            val userDTO = UserDTO.builder()
                .id(createdBy.id)
                .username(createdBy.username)
                .email(createdBy.email)
                .profilePicture(createdBy.profilePicture)
                .build()

            val playlistDTO = PlaylistDTO.builder()
                .id(foundPlaylist.id)
                .name(foundPlaylist.name)
                .createdBy(userDTO)
                .createdAt(foundPlaylist.metadata.createdAt)
                .build()

            ServerResponse.ok().bodyValueAndAwait(playlistDTO)
        } ?: ServerResponse.notFound().buildAndAwait()
    }

    suspend fun setCover(serverRequest: ServerRequest): ServerResponse {
        val id = serverRequest.pathVariable("id")
        val playlist = playlistRepository.findById(id) ?:
            return ServerResponse.status(HttpStatus.NOT_FOUND).buildAndAwait()

        val createdBy = playlist.metadata.createdBy
        if(!AuthUtil.canPerformAction(createdBy)){
            throw InvalidOperationException("Can not set cover on a playlist you do not own")
        }

        val partData = serverRequest.awaitMultipartData().toSingleValueMap()
        val file = partData["file"]!! as FilePart

        val extension = file.filename().split(".").last()
        val fileId = UUID.randomUUID().toString()
        file.transferTo(Paths.get("${coverFolder}${fileId}.${extension}"))
            .subscribe()

        playlist.apply {
            cover = "http://localhost:8080/files/cover/${fileId}.${extension}"
        }

        playlistRepository.save(playlist)
        return ServerResponse.status(HttpStatus.NO_CONTENT)
            .buildAndAwait()
    }

    suspend fun findSongs(serverRequest: ServerRequest): ServerResponse {
        val id = serverRequest.pathVariable("id")
        val exists = playlistRepository.existsById(id)
        if(!exists) {
            return ServerResponse.notFound().buildAndAwait()
        }

        val songs = songRepository.findAllByContainedInPlaylistsContains(id)
            .map {
                val createdBy = it.metadata.createdBy ?: throw IllegalStateException("Song creator user must not be null")
                val userDTO = UserDTO.builder()
                    .id(createdBy.id)
                    .username(createdBy.username)
                    .email(createdBy.email)
                    .profilePicture(createdBy.profilePicture)
                    .build()

                SongDTO.builder()
                    .id(it.id)
                    .title(it.title)
                    .cover(it.cover)
                    .createdBy(userDTO)
                    .createdAt(it.metadata.createdAt)
                    .build()
            }

        return ServerResponse.ok().bodyAndAwait(songs)
    }

    suspend fun addSong(serverRequest: ServerRequest): ServerResponse {
        val (playlistId, songId) = serverRequest.awaitBody<PlaylistActionToSong>()
        val playlist = playlistRepository.findById(playlistId) ?:
            throw NotFoundException("Can not add song to a playlist that does not exists $playlistId")

        val song = songRepository.findById(songId) ?:
            throw NotFoundException("Can not add song $songId because it does not exists")

        song.containedInPlaylists.add(playlistId)
        playlist.songs.add(song)

        songRepository.save(song)
        val playlistWithNewSong = playlistRepository.save(playlist)
        return ServerResponse.status(HttpStatus.NO_CONTENT)
            .bodyValueAndAwait(playlistWithNewSong)
    }

    suspend fun deleteSong(serverRequest: ServerRequest): ServerResponse {
        val (playlistId, songId) = serverRequest.awaitBody<PlaylistActionToSong>()
        val playlist = playlistRepository.findById(playlistId) ?:
            throw NotFoundException("Can not delete song from a playlist that does not exists $playlistId")

        val song = songRepository.findById(songId) ?:
            throw NotFoundException("Can not delete song $songId from playlist $playlistId because the song does not exists")

        song.containedInPlaylists.remove(playlistId)
        playlist.songs.remove(song)

        songRepository.save(song)
        playlistRepository.save(playlist)
        return ServerResponse.status(HttpStatus.NO_CONTENT).buildAndAwait()
    }

    suspend fun delete(serverRequest: ServerRequest): ServerResponse{
        val playlistId = serverRequest.pathVariable("id")
        val playlist = playlistRepository.findById(playlistId) ?:
            throw NotFoundException("Can not delete playlist $playlistId, because it does not exists")

        val createdBy = playlist.metadata.createdBy
        if(!AuthUtil.canPerformAction(createdBy)){
            throw InvalidOperationException("You can not delete a resource that you do not own")
        }

        playlistRepository.delete(playlist)
        return ServerResponse.status(HttpStatus.NO_CONTENT).buildAndAwait()
    }
}