package com.utils

import io.ktor.http.content.PartData
import io.ktor.http.content.streamProvider
import java.io.File
import java.util.UUID

fun PartData.FileItem.save(directoryPath: String): String {
    val extension = originalFileName?.substringAfterLast('.', "") ?: "jpg"
    val fileName = "${UUID.randomUUID()}.$extension"
    val file = File(directoryPath, fileName)
    streamProvider().use { inputStream ->
        file.outputStream().buffered().use { outputStream ->
            inputStream.copyTo(outputStream)
        }
    }
    return fileName
}