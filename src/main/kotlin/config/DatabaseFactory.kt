package com.config

import com.entities.*
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.Database
import io.github.cdimascio.dotenv.dotenv
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {
    fun init() {

        val dotenv = dotenv()

        val config = HikariConfig().apply {
            jdbcUrl = "jdbc:postgresql://localhost:5432/${dotenv["DB_NAME"]}"
            username = dotenv["DB_USER"]
            password = dotenv["DB_PASSWORD"]
            driverClassName = "org.postgresql.Driver"
            maximumPoolSize = 10
            isAutoCommit = false
            transactionIsolation = "TRANSACTION_SERIALIZABLE"
            validate()
        }

        val dataSource = HikariDataSource(config)

        Database.connect(datasource = dataSource)

        transaction {
            SchemaUtils.createMissingTablesAndColumns(
                Users,
                BlacklistedTokens,
                Tasks,
                TaskAttachments
            )
        }
    }
}