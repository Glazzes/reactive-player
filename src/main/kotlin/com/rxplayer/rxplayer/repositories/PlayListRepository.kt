package com.rxplayer.rxplayer.repositories

import com.rxplayer.rxplayer.entities.PlayList
import org.springframework.data.mongodb.repository.ReactiveMongoRepository

interface PlayListRepository : ReactiveMongoRepository<PlayList, String>