package com.rxplayer.rxplayer.handlers

import com.rxplayer.rxplayer.dto.input.PlaylistActionToSong
import com.rxplayer.rxplayer.dto.input.SongRequest
import com.rxplayer.rxplayer.dto.output.find.FindPlaylistDTO
import com.rxplayer.rxplayer.dto.output.find.FindSongDTO
import com.rxplayer.rxplayer.dto.output.find.FindUserDTO
import com.rxplayer.rxplayer.entities.EntityMetadata
import com.rxplayer.rxplayer.entities.PlayList
import com.rxplayer.rxplayer.exception.NotFoundException
import com.rxplayer.rxplayer.repositories.PlaylistRepository
import com.rxplayer.rxplayer.repositories.SongRepository
import kotlinx.coroutines.flow.map
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.server.*

@Service
class PlayListHandler(
    private val songRepository: SongRepository,
    private val playlistRepository: PlaylistRepository
    ){

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
            val creator = it.metadata.createdBy ?: throw NotFoundException("User not found.")
            val createdDate = it.metadata.createdAt ?: throw IllegalStateException("Null created date.")

            val userDTO = FindUserDTO(
                creator.id,
                creator.nickName,
                creator.email,
                creator.profilePicture)

            val dto = FindPlaylistDTO(it.id, it.name, userDTO, createdDate)
            ServerResponse.ok().bodyValueAndAwait(dto)
        } ?: ServerResponse.notFound().buildAndAwait()
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
                val userDto = FindUserDTO(createdBy.id, createdBy.nickName, createdBy.email, createdBy.profilePicture)
                FindSongDTO(it.id, it.title, userDto, it.metadata.createdAt)
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
        val exists = playlistRepository.existsById(playlistId)

        if(!exists){
            throw NotFoundException("Can not delete playlist $playlistId, because it does not exists")
        }

        playlistRepository.deleteById(playlistId)
        return ServerResponse.status(HttpStatus.NO_CONTENT).buildAndAwait()
    }
}