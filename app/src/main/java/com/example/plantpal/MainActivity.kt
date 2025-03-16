package com.example.plantpal

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Navigation Icons
        val topLeftIcon = findViewById<ImageView>(R.id.topLeftIcon)
        val topRightIcon = findViewById<ImageView>(R.id.topRightIcon)
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)

        // Top Left Icon Click - Open MainActivity3
        topLeftIcon.setOnClickListener {
            startActivity(Intent(this, MainActivity3::class.java))
        }

        // Top Right Icon Click - Open MainActivity4
        topRightIcon.setOnClickListener {
            startActivity(Intent(this, MainActivity4::class.java))
        }

        // Bottom Navigation Handling
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.g -> {
                    startActivity(Intent(this, MainActivity2::class.java))
                    true
                }
                R.id.home -> true
                R.id.search -> {
                    startActivity(Intent(this, MainActivity5::class.java))
                    true
                }
                else -> false
            }
        }
    }
}
