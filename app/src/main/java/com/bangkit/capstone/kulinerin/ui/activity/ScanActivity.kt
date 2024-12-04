package com.bangkit.capstone.kulinerin.ui.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bangkit.capstone.kulinerin.data.api.ApiConfig
import com.bangkit.capstone.kulinerin.data.response.FoodResponseEntity
import com.bangkit.capstone.kulinerin.databinding.ActivityScanBinding
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale

class ScanActivity : AppCompatActivity() {

    private lateinit var binding: ActivityScanBinding
    private var imageCapture: ImageCapture? = null
    private val photoPickerLauncher = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            sendImageToResult(uri)
        } else {
            Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (permissionGranted()) {
            startCamera()
        } else {
            requestCameraPermission()
        }

        binding.apply {
            cvCaptureButton.setOnClickListener {
                if (permissionGranted()) {
                    capturePhoto()
                } else {
                    requestCameraPermission()
                }
            }
            gvGalleryButton.setOnClickListener {
                openGalleryWithPhotoPicker()
            }
        }
    }

    private fun openGalleryWithPhotoPicker() {
        photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(binding.pvScan.surfaceProvider)
            }

            imageCapture = ImageCapture.Builder().build()

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this,
                    CameraSelector.DEFAULT_BACK_CAMERA,
                    preview,
                    imageCapture
                )
            } catch (exc: Exception) {
                Toast.makeText(this@ScanActivity, "Failed to start camera", Toast.LENGTH_SHORT).show()
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun capturePhoto() {
        val imageCapture = imageCapture ?: return

        val photoFile = File(
            externalCacheDir,
            SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(System.currentTimeMillis()) + ".jpg"
        )

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val savedUri = Uri.fromFile(photoFile)
                    sendImageToResult(savedUri)
                }

                override fun onError(exception: ImageCaptureException) {
                    Toast.makeText(this@ScanActivity, "Failed to capture photo", Toast.LENGTH_SHORT).show()
                }
            }
        )
    }

    private fun sendImageToResult(uri: Uri) {
        val file = File(getRealPathFromURI(uri))
        uploadImage(file, uri)
    }

    private fun getRealPathFromURI(uri: Uri): String {
        val cursor = contentResolver.query(uri, null, null, null, null)
        cursor?.moveToFirst()
        val idx = cursor?.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
        val path = idx?.let { cursor.getString(it) }
        cursor?.close()
        return path ?: ""
    }

    private fun uploadImage(file: File, uri: Uri) {
        val requestBody = file.asRequestBody("image/*".toMediaTypeOrNull())
        val part = MultipartBody.Part.createFormData("file", file.name, requestBody)

        val apiService = ApiConfig.getApiService()
        val call = apiService.uploadImage(part)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()?.string()
                    val foodResponseEntity = parseFoodResponseFromJson(responseBody)
                    val foodName = foodResponseEntity?.prediction ?: "Unknown"
                    Log.d("Response", "Response: $foodName")
                    navigateToResultActivity(uri, foodName)
                } else {
                    val errorBody = response.errorBody()?.string()
                    Toast.makeText(this@ScanActivity, "Error: $errorBody", Toast.LENGTH_LONG).show()
                    Log.d("Error", "Response: $errorBody")
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(this@ScanActivity, "Error: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun parseFoodResponseFromJson(json: String?): FoodResponseEntity? {
        return if (json != null) {
            try {
                val gson = Gson()
                gson.fromJson(json, FoodResponseEntity::class.java)
            } catch (e: Exception) {
                null
            }
        } else {
            null
        }
    }

    private fun navigateToResultActivity(uri: Uri, foodName: String) {
        val intent = Intent(this, ResultActivity::class.java).apply {
            putExtra("image_uri", uri.toString())
            putExtra("food_name", foodName)
        }
        startActivity(intent)
    }

    private fun permissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestCameraPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
            Toast.makeText(this, "Camera permission is needed to take photos.", Toast.LENGTH_LONG).show()
        }
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.CAMERA),
            REQUEST_CODE_CAMERA_PERMISSION
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_CAMERA_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCamera()
            } else {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        private const val REQUEST_CODE_CAMERA_PERMISSION = 10
    }
}
