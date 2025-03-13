package com.example.plantpal

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.coroutines.*
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.channels.FileChannel

class MainActivity : AppCompatActivity() {
    private lateinit var imageView: ImageView
    private lateinit var resultTextView: TextView
    private lateinit var captureButton: Button
    private lateinit var tflite: Interpreter
    private val labels = arrayOf("Healthy", "Diseased", "Other")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        imageView = findViewById(R.id.imageView)
        resultTextView = findViewById(R.id.resultTextView)
        captureButton = findViewById(R.id.captureButton)

        tflite = Interpreter(loadModelFile("plant_model.tflite"))

        captureButton.setOnClickListener {
            checkCameraPermissionAndOpenCamera()
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) openCamera()
            else resultTextView.text = "Camera permission denied."
        }

    private fun checkCameraPermissionAndOpenCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            openCamera()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private val cameraLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val imageBitmap = result.data?.extras?.get("data") as Bitmap
                imageView.setImageBitmap(imageBitmap)
                resultTextView.text = "Processing image..."
                processImageAsync(imageBitmap)
            }
        }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraLauncher.launch(intent)
    }

    private fun processImageAsync(bitmap: Bitmap) {
        CoroutineScope(Dispatchers.IO).launch {
            val result = processImage(bitmap)
            withContext(Dispatchers.Main) {
                resultTextView.text = result
            }
        }
    }

    private fun processImage(bitmap: Bitmap): String {
        val resizedBitmap = Bitmap.createScaledBitmap(bitmap, 224, 224, true)
        val byteBuffer = convertBitmapToByteBuffer(resizedBitmap)

        val output = Array(1) { FloatArray(labels.size) }
        tflite.run(byteBuffer, output)

        val predictedIndex = output[0].indices.maxByOrNull { output[0][it] } ?: -1
        return if (predictedIndex in labels.indices) {
            "Prediction: ${labels[predictedIndex]}"
        } else {
            "Prediction: Unknown"
        }
    }

    private fun convertBitmapToByteBuffer(bitmap: Bitmap): ByteBuffer {
        val byteBuffer = ByteBuffer.allocateDirect(4 * 224 * 224 * 3).apply {
            order(ByteOrder.nativeOrder())
        }

        val intValues = IntArray(224 * 224)
        bitmap.getPixels(intValues, 0, 224, 0, 0, 224, 224)

        intValues.forEach { pixel ->
            byteBuffer.putFloat(((pixel shr 16 and 0xFF) - 127.5f) / 127.5f)
            byteBuffer.putFloat(((pixel shr 8 and 0xFF) - 127.5f) / 127.5f)
            byteBuffer.putFloat(((pixel and 0xFF) - 127.5f) / 127.5f)
        }

        return byteBuffer
    }

    private fun loadModelFile(modelName: String): ByteBuffer {
        val fileDescriptor = assets.openFd(modelName)
        FileInputStream(fileDescriptor.fileDescriptor).use { inputStream ->
            val fileChannel = inputStream.channel
            return fileChannel.map(FileChannel.MapMode.READ_ONLY, fileDescriptor.startOffset, fileDescriptor.declaredLength)
        }
    }
}
