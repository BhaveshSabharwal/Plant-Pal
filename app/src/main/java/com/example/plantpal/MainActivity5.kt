// MainActivity5.kt
package com.example.plantpal

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ScrollView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.*
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.channels.FileChannel

class MainActivity5 : AppCompatActivity() {
    private lateinit var imageView: ImageView
    private lateinit var resultTextView: TextView
    private lateinit var captureButton: Button
    private lateinit var icCluster: ImageView
    private lateinit var tflite: Interpreter
    private val labels = arrayOf("Aloe Vera", "Marigold", "Rose", "Snake Plant", "Sunflower", "Tulips")
    private lateinit var scrollViewData: ScrollView
    private lateinit var plantDetailsTextView: TextView

    data class Plant(
        val plantName: String,
        val scientificName: String,
        val family: String,
        val bestMonths: String,
        val commonPests: List<String>,
        val growthHeight: String,
        val soilType: String,
        val moisture: String,
        val temperature: String,
        val humidity: String,
        val healthIndicators: List<String>
    )

    private val plantData = listOf(
        Plant(
            "Aloe Vera",
            "Aloe barbadensis miller",
            "Asphodelaceae",
            "Year-round",
            listOf("Mealybugs", "Aphids"),
            "30-100 cm",
            "Sandy, well-drained",
            "Low",
            "15-25°C",
            "Low",
            listOf("Thick, fleshy green leaves", "No brown tips", "Strong upright growth", "Firm texture")
        ),
        Plant(
            "Marigold",
            "Tagetes spp.",
            "Asteraceae",
            "Spring, Summer",
            listOf("Aphids", "Spider mites"),
            "30-90 cm",
            "Well-drained, moderately fertile",
            "Moderate",
            "18-30°C",
            "Low to moderate",
            listOf("Bright green foliage", "Abundant flowers", "No wilting", "No yellowing leaves")
        ),
        Plant(
            "Rose",
            "Rosa spp.",
            "Rosaceae",
            "Spring, Summer",
            listOf("Aphids", "Japanese beetles"),
            "50-200 cm",
            "Loamy, well-drained",
            "Moderate",
            "15-25°C",
            "Moderate",
            listOf("Glossy green leaves", "Strong stems", "Fragrant blooms", "No black spots")
        ),
        Plant(
            "Snake Plant",
            "Sansevieria trifasciata",
            "Asparagaceae",
            "Year-round",
            listOf("Spider mites", "Mealybugs"),
            "30-120 cm",
            "Well-drained, sandy",
            "Low",
            "15-30°C",
            "Low to moderate",
            listOf("Upright, sturdy leaves", "No soft spots", "Vivid green patterns", "No curling or yellowing")
        ),
        Plant(
            "Sunflower",
            "Helianthus annuus",
            "Asteraceae",
            "June to September",
            listOf("Aphids", "Cutworms"),
            "150-300 cm",
            "Well-drained, nutrient-rich",
            "Moderate",
            "20-30°C",
            "Low to moderate",
            listOf("Tall, sturdy stems", "Large vibrant flowers", "Deep green leaves", "No wilting")
        ),
        Plant(
            "Tulips",
            "Tulipa spp.",
            "Liliaceae",
            "March to May",
            listOf("Aphids", "Slugs"),
            "10-70 cm",
            "Well-drained, sandy or loamy",
            "Moderate",
            "15-20°C",
            "Low to moderate",
            listOf("Glossy green leaves", "Strong stems", "Vibrant blooms", "No yellowing leaves")
        )
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main5)

        imageView = findViewById(R.id.imageView)
        resultTextView = findViewById(R.id.resultTextView)
        captureButton = findViewById(R.id.captureButton)
        icCluster = findViewById(R.id.topLeftIcon)
        plantDetailsTextView = findViewById(R.id.plantDetails)
        scrollViewData = findViewById(R.id.scrollViewData)
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)

        resultTextView.text = "Result will be displayed here"
        plantDetailsTextView.text = ""
        scrollViewData.visibility = View.GONE

        tflite = Interpreter(loadModelFile("plant_model.tflite"))

        captureButton.setOnClickListener {
            checkCameraPermissionAndOpenCamera()
        }

        icCluster.setOnClickListener {
            val intent = Intent(this, MainActivity4::class.java)
            startActivity(intent)
        }

        val profileButton = findViewById<ImageView>(R.id.topRightIcon)
        profileButton.setOnClickListener {
            val intent = Intent(this, MainActivity3::class.java)
            startActivity(intent)
        }

        bottomNav.selectedItemId = R.id.search
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.g -> {
                    val intent = Intent(this, MainActivity2::class.java)
                    startActivity(intent)
                    true
                }
                R.id.home -> {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.search -> true
                else -> false
            }
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
            val (resultIndex, confidence) = processImage(bitmap)
            withContext(Dispatchers.Main) {
                // Define a confidence threshold for the prediction
                val confidenceThreshold = 0.7f

                if (confidence >= confidenceThreshold && resultIndex in labels.indices) {
                    val plantName = labels[resultIndex]
                    val plant = plantData.find { it.plantName == plantName }

                    if (plant != null) {
                        val plantDetails = """
                        Plant Name: ${plant.plantName}
                        Scientific Name: ${plant.scientificName}
                        Family: ${plant.family}
                        Best Months to Grow: ${plant.bestMonths}
                        Common Pests: ${plant.commonPests.joinToString(", ")}
                        Growth Height: ${plant.growthHeight}
                        Soil Type: ${plant.soilType}
                        Moisture: ${plant.moisture}
                        Temperature: ${plant.temperature}
                        Humidity: ${plant.humidity}
                        Health Indicators: ${plant.healthIndicators.joinToString(", ")}
                    """.trimIndent()

                        resultTextView.text = plant.plantName
                        plantDetailsTextView.text = plantDetails
                        scrollViewData.visibility = View.VISIBLE
                    } else {
                        resultTextView.text = "Prediction: Plant data not found for $plantName"
                        scrollViewData.visibility = View.GONE
                    }
                } else {
                    // Fallback message when confidence is too low
                    resultTextView.text = "Prediction uncertain. Please try again."
                    scrollViewData.visibility = View.GONE
                }
            }
        }
    }

    private fun processImage(bitmap: Bitmap): Pair<Int, Float> {
        val resizedBitmap = Bitmap.createScaledBitmap(bitmap, 224, 224, true)
        val byteBuffer = convertBitmapToByteBuffer(resizedBitmap)

        val output = Array(1) { FloatArray(labels.size) }
        tflite.run(byteBuffer, output)

        // Get the index with the highest probability
        val resultIndex = output[0].indices.maxByOrNull { output[0][it] } ?: -1
        // Get the confidence (probability) of the predicted class
        val confidence = if (resultIndex != -1) output[0][resultIndex] else 0f

        return Pair(resultIndex, confidence)
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