package com.research.upload.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import coil.load
import com.research.upload.databinding.ItemChatMeBinding
import com.research.upload.databinding.ItemChatMeDocBinding
import com.research.upload.databinding.ItemChatMeImagesBinding
import com.research.upload.databinding.ItemChatOthersBinding
import com.research.upload.model.Document
import com.research.upload.model.Image
import com.research.upload.model.Message
import com.research.upload.model.Text
import com.research.upload.toHHmm

class ItemMessageAdapter : RecyclerView.Adapter<ItemMessageAdapter.MessageViewHolder>() {
    private val listData = ArrayList<Message<*>>()

    var onRemoveMessage: ((Message<*>) -> Unit)? = null

    fun addData(message: Message<*>, scrollTo: ((Int) -> Unit)? = {}) {
        val previousContentSize = this.listData.size
        listData.add(message)
        notifyItemRangeInserted(previousContentSize, listData.size)
        scrollTo?.invoke(previousContentSize)
    }

    fun removeData(message: Message<*>) {
        val index = listData.indexOf(message)
        this.listData.remove(message)
        notifyItemRangeRemoved(index, 1)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        return when (viewType) {
            VIEW_TYPE_MESSAGE_SENT -> SentMessageViewHolder(
                ItemChatMeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )

            VIEW_TYPE_IMAGE_SENT -> SentImagesViewHolder(
                ItemChatMeImagesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )

            VIEW_TYPE_DOCUMENT_SENT -> SentDocsViewHolder(
                ItemChatMeDocBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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
        abstract fun bind(data: Message<*>)
    }

    inner class SentMessageViewHolder(private val binding: ItemChatMeBinding) :
        MessageViewHolder(binding) {

        override fun bind(data: Message<*>) {
            if (data.data !is Text) return

            with(binding) {
                textGchatDateMe.visibility = View.GONE
                textGchatMessageMe.text = data.data.text
                textGchatTimestampMe.text = data.createdAt.toHHmm()
                cardGchatMessageMe.setOnLongClickListener {
                    onRemoveMessage?.invoke(data)
                    true
                }
            }
        }
    }

    inner class SentImagesViewHolder(private val binding: ItemChatMeImagesBinding) :
        MessageViewHolder(binding) {

        override fun bind(data: Message<*>) {
            if (data.data !is Image) return

            with(binding) {
                textGchatDateMe.visibility = View.GONE
                ivUploadedImage.load(data.data.uri)
                textGchatTimestampMe.text = data.createdAt.toHHmm()
                btnDeleteImage.setOnClickListener {
                    onRemoveMessage?.invoke(data)
                }
            }
        }
    }

    inner class SentDocsViewHolder(private val binding: ItemChatMeDocBinding) :
        MessageViewHolder(binding) {

        override fun bind(data: Message<*>) {
            if (data.data !is Document) return

            with(binding) {
                textGchatDateMe.visibility = View.GONE
                tvFileName.text = data.data.name
                tvFileSize.text = data.data.size
                textGchatTimestampMe.text = data.createdAt.toHHmm()
                btnDeleteImage.setOnClickListener {
                    onRemoveMessage?.invoke(data)
                }
            }
        }
    }

    inner class ReceivedMessageViewHolder(private val binding: ItemChatOthersBinding) :
        MessageViewHolder(binding) {

        override fun bind(data: Message<*>) {
            if (data.data !is Text) return

            with(binding) {
                textGchatDateOther.visibility = View.GONE
                textGchatUserOther.text = data.sender.nickname
                textGchatMessageOther.text = data.data.text
                textGchatTimestampOther.text = data.createdAt.toHHmm()
            }
        }
    }


    companion object {
        private const val VIEW_TYPE_MESSAGE_SENT = 101
        private const val VIEW_TYPE_DOCUMENT_SENT = 102
        private const val VIEW_TYPE_IMAGE_SENT = 103

        private const val VIEW_TYPE_MESSAGE_RECEIVED = 201
    }
}