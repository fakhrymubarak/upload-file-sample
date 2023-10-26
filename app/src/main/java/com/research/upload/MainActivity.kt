package com.research.upload

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.DocumentsContract
import android.provider.OpenableColumns
import android.text.format.Formatter.formatFileSize
import android.util.Log
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.appcompat.app.AppCompatActivity
import coil.load
import com.research.upload.databinding.ActivityMainBinding
import java.io.File
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.roundToInt


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val pickDocumentLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val uri = result.data?.data
            if (uri == null) {
                Log.d("DocumentPicker", "No document selected")
                return@registerForActivityResult
            }
            Log.d("DocumentPicker", "Selected URI: $uri")
            uploadDocumentToServer(uri)
        }

    private val pickMedia =
        registerForActivityResult(PickVisualMedia()) { uri ->
            if (uri == null) {
                Log.d("PhotoPicker", "No media selected")
                return@registerForActivityResult
            }

            Log.d("PhotoPicker", "Selected URI: $uri")
            uploadImageToServer(uri)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initEvent()
    }

    private fun initEvent() {
        with(binding) {
            btnUploadFiles.setOnClickListener {
                pickDocument()
            }

            btnUploadImages.setOnClickListener {
                pickImage()
            }

            // remove image when image view clicked
            ivUploadedImage.setOnClickListener {
                ivUploadedImage.setImageResource(0)
            }
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

    private fun pickImage() {
        // Launch the photo picker and let the user choose only images.
        pickMedia.launch(PickVisualMediaRequest(PickVisualMedia.ImageOnly))
    }


    private fun uploadDocumentToServer(uri: Uri) {
        val documentFiles = File(uri.path ?: "")
        Log.d("DocumentPicker", "Selected URI: $documentFiles")

        // viewModel.uploadImage(documentFiles)
        binding.tvFilesTitle.text = uri.metaDataDocuments.name
        binding.tvFilesSize.text = formatFileSize(this, uri.metaDataDocuments.size)
    }

    private fun uploadImageToServer(uri: Uri) {
        val imageFiles = File(uri.path ?: "")
        Log.d("PhotoPicker", "Selected URI: $imageFiles")

        // viewModel.uploadImage(imageFiles)
        binding.ivUploadedImage.load(uri)
        binding.tvFilesTitle.text = uri.metaDataDocuments.name
        binding.tvFilesSize.text = formatFileSize(this, uri.metaDataDocuments.size)
    }

    private val Uri.metaDataDocuments: FileMetadata
        get() {
            val contentResolver = contentResolver
            val cursor = contentResolver.query(
                this, null, null, null, null
            ) ?: return FileMetadata.empty()

            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
            cursor.moveToFirst()

            val filename = cursor.getString(nameIndex)
            val fileSize = cursor.getLong(sizeIndex)
            cursor.close()

            return FileMetadata(filename, fileSize)
        }
}

data class FileMetadata(
    val name: String,
    val size: Long,
) {
    companion object {
        fun empty() = FileMetadata("", 0L)
    }
}
