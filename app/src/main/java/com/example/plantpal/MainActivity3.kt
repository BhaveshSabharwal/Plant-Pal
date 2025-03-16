package com.example.plantpal

import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.widget.TextView
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class MainActivity3 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main3)

        val myPlantsText = findViewById<TextView>(R.id.myPlantsText)
        val homeIcon = findViewById<ImageView>(R.id.homeIcon)
        val icCluster = findViewById<ImageView>(R.id.topLeftIcon) // Left icon (Plants)
        val profileIcon = findViewById<ImageView>(R.id.profileIcon) // Right icon (Profile)

        // Set underline for "My Plants"
        myPlantsText.paintFlags = myPlantsText.paintFlags or Paint.UNDERLINE_TEXT_FLAG

        // Navigate to My Plants screen (Uncomment if needed)
        // myPlantsText.setOnClickListener {
        //     val intent = Intent(this, MyPlantsActivity::class.java)
        //     startActivity(intent)
        // }

        // Navigate back to Home
        homeIcon.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        // Click event for top left icon
        icCluster.setOnClickListener {
            val intent = Intent(this, MainActivity4::class.java)
            startActivity(intent)
        }

        // Click event for top right profile icon (Add Profile Activity if needed)
        profileIcon.setOnClickListener {
            val intent = Intent(this, MainActivity3::class.java) // Replace with actual activity
            startActivity(intent)
        }
    }
}
