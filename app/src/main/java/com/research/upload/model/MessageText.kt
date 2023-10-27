package com.research.upload.model

import java.io.File

data class Message<out T>(
    val data: T,
    val sender: User,
    val createdAt: Long,
) {
    companion object {
        fun dummyTextsFrom(sender: String): List<Message<Text>> {
            return listOf(
                generateRandomMessage(sender),
                generateRandomMessage(sender),
                generateRandomMessage(sender),
            )
        }

        private fun generateRandomMessage(sender: String): Message<Text> {
            val listOfMessage =
                listOf("Hello World", "Message Dummy", "Hello Lorem", "Ipsum Color Si Amet")
            return Message(
                data = Text(listOfMessage.random()),
                sender = User(sender),
                createdAt = System.currentTimeMillis(),
            )
        }
    }
}

data class Text(
    val text: String,
)

data class Document(
    val file: File,
    val path: String,
    val size: String,
    val name: String,
)

data class Image(
    val image: File,
    val path: String,
    val size: String,
    val name: String,
)

data class User(
    var nickname: String,
    var profileUrl: String = "",
)
