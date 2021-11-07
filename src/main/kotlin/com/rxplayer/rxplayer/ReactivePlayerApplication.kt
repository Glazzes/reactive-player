package com.rxplayer.rxplayer

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.mongodb.config.EnableReactiveMongoAuditing

@SpringBootApplication
@EnableReactiveMongoAuditing
class ReactivePlayerApplication

fun main(args: Array<String>) {
	runApplication<ReactivePlayerApplication>(*args)
}
