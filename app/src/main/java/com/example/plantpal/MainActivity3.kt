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

        // Set underline for "My Plants"
        myPlantsText.paintFlags = myPlantsText.paintFlags or Paint.UNDERLINE_TEXT_FLAG

        // Navigate to My Plants screen
//        myPlantsText.setOnClickListener {
//            val intent = Intent(this, MyPlantsActivity::class.java)
//            startActivity(intent)
//        }

        // Navigate back to Home
        homeIcon.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}
