package com.rxplayer.rxplayer.repositories

import com.rxplayer.rxplayer.entities.Song
import kotlinx.coroutines.flow.Flow
import org.springframework.data.repository.kotlin.CoroutineSortingRepository

interface SongRepository : CoroutineSortingRepository<Song, String> {
    suspend fun findAllByContainedInPlaylistsContains(playlistId: String) : Flow<Song>
}