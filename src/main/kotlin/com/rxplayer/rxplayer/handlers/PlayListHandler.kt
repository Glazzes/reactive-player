package com.rxplayer.rxplayer.handlers

import com.rxplayer.rxplayer.dto.input.PlaylistActionToSong
import com.rxplayer.rxplayer.dto.input.PlayListRequest
import com.rxplayer.rxplayer.dto.output.created.CreatedPlayListDTO
import com.rxplayer.rxplayer.dto.output.find.FindPlaylistDTO
import com.rxplayer.rxplayer.dto.output.find.FindSongDTO
import com.rxplayer.rxplayer.dto.output.find.FindUserDTO
import com.rxplayer.rxplayer.entities.EntityMetadata
import com.rxplayer.rxplayer.entities.PlayList
import com.rxplayer.rxplayer.exception.NotFoundException
import com.rxplayer.rxplayer.repositories.PlayListRepository
import com.rxplayer.rxplayer.repositories.SongRepository
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.server.*
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty

@Service
class PlayListHandler(
    private val playListRepository: PlayListRepository,
    private val songRepository: SongRepository
    ){

    suspend fun save(serverRequest: ServerRequest): ServerResponse {
        val reactive = serverRequest.bodyToMono(PlayListRequest::class.java)
            .map { PlayList(name = it.title, metadata = EntityMetadata()) }
            .flatMap { playListRepository.save(it) }
            .map { CreatedPlayListDTO(it.id, it.name) }

        return ServerResponse.status(HttpStatus.CREATED)
            .bodyAndAwait(reactive.asFlow())
    }

    suspend fun findById(serverRequest: ServerRequest): ServerResponse {
        val id = serverRequest.pathVariable("id")
        val playlist = playListRepository.findById(id)
            .switchIfEmpty{ Mono.error(NotFoundException("Playlist with id $id was not found")) }
            .map {
                val creator = it.metadata.createdBy ?: throw NotFoundException("User not found.")
                val createdDate = it.metadata.createdAt ?: throw IllegalStateException("Null created date.")

                val userDTO = FindUserDTO(
                    creator.id,
                    creator.nickName,
                    creator.email,
                    creator.profilePicture
                )
                FindPlaylistDTO(it.id, it.name, userDTO, createdDate)
            }

        return ServerResponse.status(HttpStatus.OK)
            .bodyValueAndAwait(playlist.awaitSingle())
    }

    suspend fun findSongs(serverRequest: ServerRequest): ServerResponse {
        val id = serverRequest.pathVariable("id")
        val playlist = playListRepository.findById(id)
            .switchIfEmpty{ Mono.error(NotFoundException("Playlist with id $id was not found")) }
            .map { it.songs }
            .map { it.map {s ->
                    val metadata = s.metadata.createdBy ?: throw IllegalStateException("")
                    val createdBy = FindUserDTO(
                        metadata.id,
                        metadata.nickName,
                        metadata.email,
                        metadata.profilePicture)
                    FindSongDTO(s.id, s.title,createdBy, s.metadata.createdAt)
            }}

        return ServerResponse.status(HttpStatus.OK)
            .bodyAndAwait(playlist.asFlow())
    }

    suspend fun addSong(serverRequest: ServerRequest): ServerResponse {
        val (playlistId, songId) = serverRequest.awaitBody<PlaylistActionToSong>()

        val reactive = playListRepository.findById(playlistId)
            .switchIfEmpty { Mono.error(NotFoundException("Could not find playlist with id $playlistId")) }
            .flatMap {
                songRepository.findById(songId)
                    .switchIfEmpty{ Mono.error(NotFoundException("Could not find song with id $songId")) }
                    .flatMap { song ->
                        it.songs.add(song)
                        playListRepository.save(it)
                    }
            }
            .map { "Song $songId saved successfully to playlist $playlistId" }

        return ServerResponse.status(HttpStatus.NO_CONTENT)
            .bodyValueAndAwait(reactive.awaitSingle())
    }

    suspend fun deleteSong(serverRequest: ServerRequest): ServerResponse {
        val (playlistId, songId) = serverRequest.awaitBody<PlaylistActionToSong>()

        val reactive = playListRepository.findById(playlistId)
            .switchIfEmpty { Mono.error(NotFoundException("Could not find playlist with id $playlistId")) }
            .flatMap { playlist ->
                val save = songRepository.findById(songId)
                    .switchIfEmpty{
                        Mono.error(NotFoundException("Could not find song with id $songId in playlist $playlistId"))
                    }
                    .flatMap { song ->
                        playlist.songs.remove(song)
                        playListRepository.save(playlist)
                    }

                Mono.`when`(save)
            }

        return ServerResponse.status(HttpStatus.NO_CONTENT)
            .bodyAndAwait(reactive.asFlow())
    }

    suspend fun delete(serverRequest: ServerRequest): ServerResponse{
        val playlistId = serverRequest.pathVariable("id")
        val reactive = playListRepository.existsById(playlistId)
            .handle<Boolean> { exists, sink ->
                if(exists){
                    sink.next(exists)
                }else{
                    sink.error(NotFoundException("Playlist with id $playlistId does not exists"))
                }
            }
            .flatMap { playListRepository.deleteById(playlistId) }

        return ServerResponse.status(HttpStatus.NO_CONTENT)
            .bodyAndAwait(reactive.asFlow())
    }
}