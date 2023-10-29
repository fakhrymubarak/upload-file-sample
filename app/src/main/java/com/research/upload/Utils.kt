package com.research.upload

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import java.io.BufferedInputStream
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


fun createImageTempFile(context: Context): File {
    val timeStamp: String = SimpleDateFormat(
        "yyyy-MM-dd", Locale.getDefault()
    ).format(System.currentTimeMillis())

    return File.createTempFile("Veronika_Chat_$timeStamp", ".jpg", context.cacheDir)
}

fun Uri.bitmapToFile(context: Context): File {
    val contentResolver = context.contentResolver
    val tempFile = createImageTempFile(context)

    val inputStream = contentResolver.openInputStream(this) as InputStream
    val outputStream = FileOutputStream(tempFile)
    val buf = ByteArray(1024)
    var len: Int
    while (inputStream.read(buf).also { len = it } > 0) outputStream.write(buf, 0, len)
    outputStream.close()
    inputStream.close()

    return tempFile
}

fun Uri.documentToFile(context: Context): File? {
    val filename = this.fileName(context)
    val newFile = File(context.cacheDir, filename)

    return try {
        val inputStream = context.contentResolver.openInputStream(this)
        val outputStream = FileOutputStream(newFile)
        copyAndCloseInputStream(inputStream, outputStream)
        newFile
    } catch (e: FileNotFoundException) {
        e.printStackTrace()
        null
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

fun copyAndCloseInputStream(inputStream: InputStream?, outputStream: OutputStream): Int {
    var bufferedInputStream: BufferedInputStream? = null
    var totalBytes = 0
    try {
        bufferedInputStream = BufferedInputStream(inputStream)
        val buffer = ByteArray(1024)
        var bytesRead: Int
        while (bufferedInputStream.read(buffer).also { bytesRead = it } != -1) {
            outputStream.write(buffer, 0, bytesRead)
            totalBytes += bytesRead
        }
    } finally {
        bufferedInputStream?.close()
        inputStream?.close()
    }
    return totalBytes
}


fun Uri.fileName(context: Context): String {
    val contentResolver = context.contentResolver
    val cursor = contentResolver.query(
        this, null, null, null, null
    ) ?: return ""

    val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
    cursor.moveToFirst()

    val filename = cursor.getString(nameIndex)
    cursor.close()

    return filename
}

fun Long.toHHmm(): String {
    val date = Date(this)
    val simpleDateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    return simpleDateFormat.format(date)
}

