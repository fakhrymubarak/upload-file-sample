package com.research.upload

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.research.upload.adapter.ItemMessageAdapter
import com.research.upload.databinding.ActivityChatBinding
import com.research.upload.model.Message

class ChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatBinding
    private lateinit var adapter: ItemMessageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
        initEvent()
        inflateDummyMessage()
    }

    private fun initEvent() {
        with(binding) {

        }

    }

    private fun initView() {
        adapter = ItemMessageAdapter()
        binding.rvChat.adapter = adapter

    }

    private fun inflateDummyMessage() {
        val listSentMessage = Message.dummyTextsFrom("Me")
        val listReceivedMessage = Message.dummyTextsFrom("Voldemort")

        listSentMessage.forEach { message ->
            adapter.addData(message)
        }

        listReceivedMessage.forEach { message ->
            adapter.addData(message)
        }
    }
}