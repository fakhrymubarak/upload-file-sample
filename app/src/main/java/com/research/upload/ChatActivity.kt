package com.research.upload

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.DocumentsContract
import android.text.format.Formatter
import android.util.Log
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.research.upload.adapter.ItemMessageAdapter
import com.research.upload.databinding.ActivityChatBinding
import com.research.upload.model.Document
import com.research.upload.model.Image
import com.research.upload.model.Message
import com.research.upload.model.Text


class ChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatBinding
    private lateinit var adapter: ItemMessageAdapter

    private val pickMedia =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri == null) {
                Log.d("PhotoPicker", "No media selected")
                return@registerForActivityResult
            }

            Log.d("PhotoPicker", "Selected URI: $uri")
            uploadImages(uri)
        }

    private val pickDocumentLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val uri = result.data?.data
            if (uri == null) {
                Log.d("DocumentPicker", "No document selected")
                return@registerForActivityResult
            }
            Log.d("DocumentPicker", "Selected URI: $uri")
            uploadDocument(uri)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
        initEvent()
        inflateDummyMessage()
    }

    private fun initView() {
        adapter = ItemMessageAdapter()
        binding.rvChat.adapter = adapter
    }

    private fun initEvent() {
        adapter.onRemoveMessage = { showAlertDelete(it) }

        with(binding) {
            btnUploadImages.setOnClickListener {
                pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }
            btnUploadFiles.setOnClickListener { pickDocument() }
            btnSendChat.setOnClickListener { sendMessage() }
        }
    }

    private fun pickDocument() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/*"
            val mimetypes = arrayOf(
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                "application/msword",
                "application/pdf",
            )
            intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                putExtra(DocumentsContract.EXTRA_INITIAL_URI, "")
            }
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false)
        }
        pickDocumentLauncher.launch(intent)
    }

    private fun uploadImages(uri: Uri) {
        val file = uri.bitmapToFile(this)
        val sizeStr = Formatter.formatFileSize(this, file.length())

        val image = Image(file, file.toUri(), sizeStr, file.name)
        val messageImage = Message(image)

        // viewModel.uploadImages(image)
        addDataToAdapter(messageImage)
    }

    private fun uploadDocument(uri: Uri) {
        val file = uri.documentToFile(this)
        if (file == null) {
            Toast.makeText(this, "File Not Found!", Toast.LENGTH_LONG).show()
            return
        }

        val sizeStr = Formatter.formatFileSize(this, file.length())
        val document = Document(file, file.path, sizeStr, file.name)

        val messageDocument = Message(document)

        // viewModel.uploadFiles(messageDocument)
        addDataToAdapter(messageDocument)
    }

    private fun sendMessage() {
        val messageValue = binding.etChat.text.toString()
        val message = Message(data = Text(messageValue))

        // viewModel.sendMessage(message)
        addDataToAdapter(message)
        binding.etChat.setText("")
    }

    private fun addDataToAdapter(message: Message<*>) {
        adapter.addData(message, scrollTo = { index ->
            binding.rvChat.scrollToPosition(index)
        })
    }

    private fun showAlertDelete(data: Message<*>) {
        MaterialAlertDialogBuilder(this)
            .setTitle("Delete item")
            .setCancelable(false)
            .setMessage("Sure want to delete item?")
            .setPositiveButton("Yes") { _, _ ->
                //viewModel.deleteImage(data)
                adapter.removeData(data)
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.cancel()
            }.show()
    }

    private fun inflateDummyMessage() {
        val listSentMessage = Message.dummyTextsFrom("Me")
        val listReceivedMessage = Message.dummyTextsFrom("Voldemort")

        listSentMessage.forEach { message ->
            addDataToAdapter(message)
        }

        listReceivedMessage.forEach { message ->
            addDataToAdapter(message)
        }
    }
}