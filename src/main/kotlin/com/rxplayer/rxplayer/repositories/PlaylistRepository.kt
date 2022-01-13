package com.rxplayer.rxplayer.repositories

import com.rxplayer.rxplayer.entities.PlayList
import org.springframework.data.repository.kotlin.CoroutineSortingRepository

interface PlaylistRepository : CoroutineSortingRepository<PlayList, String>