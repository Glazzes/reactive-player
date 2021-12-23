package com.rxplayer.rxplayer.handlers

import com.rxplayer.rxplayer.dto.input.PlaylistActionToSong
import com.rxplayer.rxplayer.dto.input.PlayListRequest
import com.rxplayer.rxplayer.dto.output.CreatedPlayListDTO
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

    suspend fun addSong(serverRequest: ServerRequest): ServerResponse {
        val (playlistId, songId) = serverRequest.awaitBody<PlaylistActionToSong>()

        val reactive = playListRepository.findById(playlistId)
            .switchIfEmpty { Mono.error(NotFoundException("Could not find playlist with id $playlistId")) }
            .flatMap { playlist ->
                val save = songRepository.findById(songId)
                    .switchIfEmpty{ Mono.error(NotFoundException("Could not find song with id $songId")) }
                    .flatMap { song ->
                        playlist.songs.add(song)
                        playListRepository.save(playlist)
                    }

                Mono.`when`(save)
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
                    .switchIfEmpty{ Mono.error(NotFoundException("Could not find song with id $songId")) }
                    .flatMap { song ->
                        playlist.songs.remove(song)
                        playListRepository.save(playlist)
                    }

                Mono.`when`(save)
            }
            .map { "Song $songId removed successfully to playlist $playlistId" }

        return ServerResponse.status(HttpStatus.NO_CONTENT)
            .bodyValueAndAwait(reactive.awaitSingle())
    }
}