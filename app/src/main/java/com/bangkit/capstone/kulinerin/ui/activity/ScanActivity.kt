package com.bangkit.capstone.kulinerin.ui.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
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
import com.bangkit.capstone.kulinerin.data.preference.SessionPreferences
import com.bangkit.capstone.kulinerin.data.preference.sessionDataStore
import com.bangkit.capstone.kulinerin.data.response.ScanFoodResponse
import com.bangkit.capstone.kulinerin.databinding.ActivityScanBinding
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale

class ScanActivity : AppCompatActivity() {

    private lateinit var binding: ActivityScanBinding
    private var imageCapture: ImageCapture? = null
    private lateinit var sessionPreferences: SessionPreferences

    private val photoPickerLauncher = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        Log.d("ScanActivity", "Photo picker result: ${uri?.toString() ?: "No media selected"}")
        if (uri != null) {
            sendImageToResult(uri)
        } else {
            Log.d("ScanActivity", "No media selected")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("ScanActivity", "onCreate called")
        binding = ActivityScanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionPreferences = SessionPreferences.getInstance(dataStore = this.sessionDataStore)

        if (permissionGranted()) {
            Log.d("ScanActivity", "Camera permission granted")
            startCamera()
        } else {
            Log.d("ScanActivity", "Camera permission not granted, requesting permission")
            requestCameraPermission()
        }

        binding.apply {
            cvCaptureButton.setOnClickListener {
                Log.d("ScanActivity", "Capture button clicked")
                if (permissionGranted()) {
                    capturePhoto()
                } else {
                    requestCameraPermission()
                }
            }
            gvGalleryButton.setOnClickListener {
                Log.d("ScanActivity", "Gallery button clicked")
                openGalleryWithPhotoPicker()
            }
        }
    }

    private fun openGalleryWithPhotoPicker() {
        Log.d("ScanActivity", "Opening gallery with Photo Picker")
        photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private fun startCamera() {
        Log.d("ScanActivity", "Starting camera")
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also {
                it.surfaceProvider = binding.pvScan.surfaceProvider
            }

            imageCapture = ImageCapture.Builder().build()

            try {
                Log.d("ScanActivity", "Binding camera use cases")
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this,
                    CameraSelector.DEFAULT_BACK_CAMERA,
                    preview,
                    imageCapture
                )
            } catch (exc: Exception) {
                Log.e("ScanActivity", "Failed to bind camera use cases", exc)
                Toast.makeText(this@ScanActivity, "Failed to start camera", Toast.LENGTH_SHORT).show()
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun capturePhoto() {
        Log.d("ScanActivity", "Capturing photo")
        val imageCapture = imageCapture ?: run {
            Log.d("ScanActivity", "ImageCapture is null")
            return
        }

        val photoFile = File(
            externalCacheDir,
            SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(System.currentTimeMillis()) + ".jpeg"
        )
        Log.d("ScanActivity", "Photo file created: ${photoFile.absolutePath}")

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val savedUri = Uri.fromFile(photoFile)
                    Log.d("ScanActivity", "Photo saved: $savedUri")
                    sendImageToResult(savedUri)
                }

                override fun onError(exception: ImageCaptureException) {
                    Log.e("ScanActivity", "Error capturing photo", exception)
                    Toast.makeText(this@ScanActivity, "Failed to capture photo", Toast.LENGTH_SHORT).show()
                }
            }
        )
    }

    private fun sendImageToResult(uri: Uri) {
        Log.d("ScanActivity", "Sending image to result: $uri")
        val file = uriToFile(uri) ?: run {
            Log.e("ScanActivity", "Failed to convert URI to File")
            Toast.makeText(this, "Failed to process the image", Toast.LENGTH_SHORT).show()
            return
        }
        Log.d("ScanActivity", "Converted file path: ${file.absolutePath}")
        uploadImage(file, uri)
    }

    private fun uriToFile(uri: Uri): File? {
        return try {
            val contentResolver = this.contentResolver
            val tempFile = File.createTempFile("temp_image", ".jpeg", cacheDir)

            contentResolver.openInputStream(uri)?.use { inputStream ->
                tempFile.outputStream().use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
            tempFile
        } catch (e: Exception) {
            Log.e("ScanActivity", "Error converting URI to File", e)
            null
        }
    }

    private fun uploadImage(file: File, uri: Uri) {
        Log.d("ScanActivity", "Uploading image: ${file.absolutePath}")
        val token = runBlocking {
            sessionPreferences.getToken().first().also {
                Log.d("ScanActivity", "Token retrieved: $it")
            }
        }

        val requestBody = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val part = MultipartBody.Part.createFormData("file", file.name, requestBody)

        val apiService = ApiConfig.getApiService()
        val call = apiService.uploadImage("Bearer $token", part)
        call.enqueue(object : Callback<ScanFoodResponse> {
            override fun onResponse(call: Call<ScanFoodResponse>, response: Response<ScanFoodResponse>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    Log.d("ScanActivity", "Response successful: $responseBody")
                    if (responseBody != null) {
                        val foodName = responseBody.queryResult[0].foodName
                        val foodDescription = responseBody.queryResult[0].description
                        navigateToResultActivity(uri, foodName, foodDescription)
                    } else {
                        Log.d("ScanActivity", "Empty response body")
                        Toast.makeText(this@ScanActivity, "Error: ${response.message()}", Toast.LENGTH_LONG).show()
                    }
                } else {
                    val responseBody = response.body()
                    Log.d("ScanActivity", "Response error: ${response.code()} - ${response.message()} - ${responseBody?.queryResult?.get(0)}")
                }
            }

            override fun onFailure(call: Call<ScanFoodResponse>, t: Throwable) {
                Log.e("ScanActivity", "Upload failed", t)
                Toast.makeText(this@ScanActivity, "Error: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun navigateToResultActivity(uri: Uri, foodName: String, foodDescription: String) {
        Log.d("ScanActivity", "Navigating to result activity")
        val intent = Intent(this, ResultActivity::class.java).apply {
            putExtra("EXTRA_URI", uri)
            putExtra("EXTRA_FOOD_NAME", foodName)
            putExtra("EXTRA_FOOD_DESCRIPTION", foodDescription)
        }
        startActivity(intent)
        finish()
    }

    private fun permissionGranted(): Boolean {
        val granted = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        Log.d("ScanActivity", "Permission granted: $granted")
        return granted
    }

    private fun requestCameraPermission() {
        Log.d("ScanActivity", "Requesting camera permission")
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
            Toast.makeText(this, "Camera permission is required to use the camera", Toast.LENGTH_SHORT).show()
        }
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.CAMERA),
            REQUEST_CODE_CAMERA_PERMISSION
        )
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Log.d("ScanActivity", "Permissions result: requestCode=$requestCode, grantResults=${grantResults.joinToString()}")
        if (requestCode == REQUEST_CODE_CAMERA_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCamera()
            } else {
                Toast.makeText(
                    this,
                    "Camera permission is required to use the camera",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    companion object {
        private const val REQUEST_CODE_CAMERA_PERMISSION = 10
    }
}
