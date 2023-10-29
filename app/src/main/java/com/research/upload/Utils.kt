package com.research.upload

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.text.SimpleDateFormat
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

fun Uri.documentToFile(): File {
    return File(this.path ?: "")
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
