package com.config

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.Database
import io.github.cdimascio.dotenv.dotenv

class DatabaseFactory {

    private val dotenv = dotenv()

    private val config = HikariConfig().apply {
        jdbcUrl = "jdbc:postgresql://localhost:5432/${dotenv["DB_NAME"]}"
        username = dotenv["DB_USER"]
        password = dotenv["DB_PASSWORD"]
        driverClassName = "org.postgresql.Driver"
        maximumPoolSize = 10
        isAutoCommit = false
        transactionIsolation = "TRANSACTION_SERIALIZABLE"
        validate()
    }

    private val dataSource = HikariDataSource(config)

    val database = Database.connect(datasource = dataSource)
}