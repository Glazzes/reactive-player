package com.rxplayer.rxplayer.repositories

import com.rxplayer.rxplayer.entities.Song
import org.springframework.data.mongodb.repository.ReactiveMongoRepository

interface SongRepository : ReactiveMongoRepository<Song, String>