package com

import com.config.DatabaseFactory
import com.data.UserDataSource

import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {

    val databaseFactory = DatabaseFactory()
    val userDataSource = UserDataSource(databaseFactory.database)

    configureSerialization()
    configureDatabases()
    configureFrameworks()
    configureHTTP()
    configureRouting()
}
