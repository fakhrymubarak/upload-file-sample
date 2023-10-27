package com.research.upload.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.research.upload.databinding.ItemChatMeBinding
import com.research.upload.databinding.ItemChatOthersBinding
import com.research.upload.model.Document
import com.research.upload.model.Image
import com.research.upload.model.Message
import com.research.upload.model.Text

class ItemMessageAdapter : RecyclerView.Adapter<ItemMessageAdapter.MessageViewHolder>() {
    private val listData = ArrayList<Message<Any>>()

    var onRemoveImage: ((Message<Any>) -> Unit)? = null

    fun addData(message: Message<Any>) {
        val previousContentSize = this.listData.size
        listData.add(message)
        notifyItemRangeInserted(previousContentSize, listData.size)
    }

    fun removeData(message: Message<Any>) {
        val index = listData.indexOf(message)
        this.listData.remove(message)
        notifyItemRangeRemoved(index, 1)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        return when (viewType) {
            VIEW_TYPE_MESSAGE_SENT -> SentMessageViewHolder(
                ItemChatMeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )

            VIEW_TYPE_MESSAGE_RECEIVED -> ReceivedMessageViewHolder(
                ItemChatOthersBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )

            else -> ReceivedMessageViewHolder(
                ItemChatOthersBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )
        }
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val data = listData[position]
        holder.bind(data)
    }

    override fun getItemCount() = listData.size

    override fun getItemViewType(position: Int): Int {
        val message = listData[position]
        return if (message.sender.nickname == "Me") {
            when (message.data) {
                is Image -> VIEW_TYPE_IMAGE_SENT
                is Document -> VIEW_TYPE_DOCUMENT_SENT
                is Text -> VIEW_TYPE_MESSAGE_SENT
                else -> VIEW_TYPE_MESSAGE_SENT
            }
        } else {
            VIEW_TYPE_MESSAGE_RECEIVED
        }
    }

    abstract class MessageViewHolder(binding: ViewBinding) : RecyclerView.ViewHolder(binding.root) {
        abstract fun bind(data: Message<Any>)
    }

    inner class SentMessageViewHolder(private val binding: ItemChatMeBinding) :
        MessageViewHolder(binding) {

        override fun bind(data: Message<Any>) {
            if (data.data !is Text) return

            binding.textGchatDateMe.visibility = View.GONE
            binding.textGchatMessageMe.text = data.data.text
            binding.textGchatTimestampMe.text = data.createdAt.toString()
        }
    }

    inner class ReceivedMessageViewHolder(private val binding: ItemChatOthersBinding) :
        MessageViewHolder(binding) {

        override fun bind(data: Message<Any>) {
            if (data.data !is Text) return

            binding.textGchatDateOther.visibility = View.GONE
            binding.textGchatUserOther.text = data.sender.nickname
            binding.textGchatMessageOther.text = data.data.text
            binding.textGchatTimestampOther.text = data.createdAt.toString()

        }
    }


    companion object {
        private const val VIEW_TYPE_MESSAGE_SENT = 101
        private const val VIEW_TYPE_DOCUMENT_SENT = 102
        private const val VIEW_TYPE_IMAGE_SENT = 103

        private const val VIEW_TYPE_MESSAGE_RECEIVED = 201
    }
}