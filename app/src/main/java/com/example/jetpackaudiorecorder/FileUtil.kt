package com.example.jetpackaudiorecorder

import java.io.File

object FileUtil {
    fun getFileName(rootPath: File, rawName: String): String {
        val count = rootPath.listFiles()?.size ?: 0

        return "$rawName-$count.mp3"
    }

    fun updateFileList(rootPath: File, rawName: String): List<File>{
        return rootPath.listFiles()?.filter { it.name.startsWith(rawName) } ?: listOf()
    }
}