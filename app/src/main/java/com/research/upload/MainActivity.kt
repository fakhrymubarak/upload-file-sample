package com.research.upload

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.appcompat.app.AppCompatActivity
import coil.load
import com.research.upload.databinding.ActivityMainBinding
import java.io.File


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding


    /**
     * Compatible to Android 11 (API Level 30) or later.
     * Need to add some code in manifest to enable backported installation through Google Play Service.
     * @see <a href="https://developer.android.com/training/data-storage/shared/photopicker#device-availability.">Device Compatibility</a>
     *
     * */
    private val pickMedia =
        registerForActivityResult(PickVisualMedia()) { uri ->
            if (uri == null) {
                Log.d("PhotoPicker", "No media selected")
                return@registerForActivityResult
            }

            Log.d("PhotoPicker", "Selected URI: $uri")
            uploadImageToServer(uri)
        }

    private fun uploadImageToServer(uri: Uri) {
        val imageFiles = File(uri.path ?: "")
        Log.d("PhotoPicker", "Selected URI: $imageFiles")

        // viewModel.uploadImage(imagePath)

        binding.ivUploadedImage.load(uri)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initEvent()
    }

    private fun initEvent() {
        with(binding) {
            btnUploadImages.setOnClickListener {
                pickImage()
            }

            // remove image when image view clicked
            ivUploadedImage.setOnClickListener {
                ivUploadedImage.setImageResource(0)
            }
        }
    }


    private fun pickImage() {
        // Launch the photo picker and let the user choose only images.
        pickMedia.launch(PickVisualMediaRequest(PickVisualMedia.ImageOnly))
    }
}