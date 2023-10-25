package com.research.upload

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.DocumentsContract
import android.util.Log
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.appcompat.app.AppCompatActivity
import coil.load
import com.research.upload.databinding.ActivityMainBinding
import java.io.File


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
            type = "application/pdf"

            // Optionally, specify a URI for the file that should appear in the
            // system file picker when it loads.
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

    }

    private fun uploadImageToServer(uri: Uri) {
        val imageFiles = File(uri.path ?: "")
        Log.d("PhotoPicker", "Selected URI: $imageFiles")

        // viewModel.uploadImage(imagePath)
        binding.ivUploadedImage.load(uri)
    }
}