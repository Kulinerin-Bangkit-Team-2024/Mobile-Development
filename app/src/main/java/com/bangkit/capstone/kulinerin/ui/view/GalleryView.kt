package com.bangkit.capstone.kulinerin.ui.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.provider.MediaStore
import android.util.AttributeSet
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import com.bangkit.capstone.kulinerin.R

class GalleryView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.white)
        style = Paint.Style.FILL
    }

    private val strokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.white)
        style = Paint.Style.STROKE
        strokeWidth = 10f
    }

    private val cornerRadius = 10f
    private var placeholderBitmap: Bitmap? = null
    private val placeholderPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    init {
        if (hasPermission()) {
            loadLatestImage()
        } else {
            requestPermission()
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val rectangle = RectF(10f, 10f, width - 10f, height - 10f)
        canvas.drawRoundRect(rectangle, cornerRadius, cornerRadius, backgroundPaint)
        canvas.drawRoundRect(rectangle, cornerRadius, cornerRadius, strokePaint)

        placeholderBitmap?.let {
            val left = (width - it.width) / 2f
            val top = (height - it.height) / 2f
            canvas.drawBitmap(it, left, top, placeholderPaint)
        }
    }

    private fun hasPermission(): Boolean {
        val permission = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            android.Manifest.permission.READ_MEDIA_IMAGES
        } else {
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        }
        return PermissionChecker.checkSelfPermission(context, permission) == PermissionChecker.PERMISSION_GRANTED
    }

    private fun requestPermission() {
        val permissionLauncher = (context as? androidx.appcompat.app.AppCompatActivity)
            ?.registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                if (isGranted) {
                    loadLatestImage()
                } else {
                    loadPlaceholderImage()
                }
            }

        val permission = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            android.Manifest.permission.READ_MEDIA_IMAGES
        } else {
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        }

        permissionLauncher?.launch(permission)
    }

    private fun loadLatestImage() {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"

        val cursor = context.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            sortOrder
        )

        cursor?.use {
            if (it.moveToFirst()) {
                val columnIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                val imagePath = it.getString(columnIndex)
                placeholderBitmap = BitmapFactory.decodeFile(imagePath)

                placeholderBitmap = placeholderBitmap?.let { bitmap ->
                    Bitmap.createScaledBitmap(
                        bitmap,
                        width / 2,
                        height / 2,
                        false
                    )
                }
            } else {
                loadPlaceholderImage()
            }
        } ?: run {
            loadPlaceholderImage()
        }
    }

    private fun loadPlaceholderImage() {
        placeholderBitmap = BitmapFactory.decodeResource(
            resources,
            R.drawable.ic_place_holder
        )
        invalidate()
    }
}