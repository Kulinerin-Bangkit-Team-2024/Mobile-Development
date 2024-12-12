package com.bangkit.capstone.kulinerin.ui.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
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
        if (uri != null) {
            sendImageToResult(uri)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionPreferences = SessionPreferences.getInstance(dataStore = this.sessionDataStore)

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
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also {
                it.surfaceProvider = binding.pvScan.surfaceProvider
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
        val imageCapture = imageCapture ?: run {
            return
        }

        val photoFile = File(
            externalCacheDir,
            SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(System.currentTimeMillis()) + ".jpeg"
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
        binding.progressBar.visibility = View.VISIBLE
        val file = uriToFile(uri) ?: run {
            binding.progressBar.visibility = View.GONE
            Toast.makeText(this, "Failed to process the image", Toast.LENGTH_SHORT).show()
            return
        }
        uploadImage(file, uri)
    }

    private fun uriToFile(uri: Uri): File? {
        return try {
            val tempFile = File.createTempFile("temp_image", ".jpeg", cacheDir)
            contentResolver.openInputStream(uri)?.use { inputStream ->
                tempFile.outputStream().use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
            tempFile
        } catch (e: Exception) {
            null
        }
    }

    private fun uploadImage(file: File, uri: Uri) {
        binding.progressBar.visibility = View.VISIBLE

        val mimeType = contentResolver.getType(uri) ?: "image/jpeg"

        val token = runBlocking {
            sessionPreferences.getToken().first()
        }

        val requestBody = file.asRequestBody(mimeType.toMediaTypeOrNull())
        val part = MultipartBody.Part.createFormData("image", file.name, requestBody)

        val apiService = ApiConfig.getApiService()
        val call = apiService.uploadImage("Bearer $token", part)
        call.enqueue(object : Callback<ScanFoodResponse> {
            override fun onResponse(call: Call<ScanFoodResponse>, response: Response<ScanFoodResponse>) {
                binding.progressBar.visibility = View.GONE
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        val foodName = responseBody.data?.queryResult?.get(0)?.foodName
                        val placeOfOrigin = responseBody.data?.queryResult?.get(0)?.placeOfOrigin
                        val foodDescription = responseBody.data?.queryResult?.get(0)?.description
                        navigateToResultActivity(uri, foodName?: "", placeOfOrigin?: "", foodDescription?: "")
                    } else {
                        Toast.makeText(this@ScanActivity, "Low confidence result.", Toast.LENGTH_LONG).show()
                    }
                } else {
                    val errorBody = response.body()
                    if (response.code() == 500) {
                        Toast.makeText(this@ScanActivity, "${errorBody?.message}", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(this@ScanActivity, response.message(), Toast.LENGTH_LONG).show()
                    }
                }
            }

            override fun onFailure(call: Call<ScanFoodResponse>, t: Throwable) {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(this@ScanActivity, "${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun navigateToResultActivity(uri: Uri, foodName: String, placeofOrigin: String, foodDescription: String) {
        val intent = Intent(this, ResultActivity::class.java).apply {
            putExtra("EXTRA_URI", uri.toString())
            putExtra("EXTRA_FOOD_NAME", foodName)
            putExtra("EXTRA_FOOD_ORIGIN", placeofOrigin)
            putExtra("EXTRA_FOOD_DESCRIPTION", foodDescription)
        }
        startActivity(intent)
        finish()
    }

    private fun permissionGranted(): Boolean {
        val granted = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        return granted
    }

    private fun requestCameraPermission() {
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
        if (requestCode == REQUEST_CODE_CAMERA_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCamera()
            } else {
                Toast.makeText(this, "Camera permission is required to use the camera", Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        private const val REQUEST_CODE_CAMERA_PERMISSION = 10
    }
}
