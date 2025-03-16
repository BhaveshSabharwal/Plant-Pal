package com.example.plantpal

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity4 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main4)

        val icCluster = findViewById<ImageView>(R.id.topLeftIcon)

        val homeButton = findViewById<ImageView>(R.id.homeIcon)

        val permissions = findViewById<TextView>(R.id.permissions)
        val notifications = findViewById<TextView>(R.id.notifications)
        val appearance = findViewById<TextView>(R.id.appearance)
        val about = findViewById<TextView>(R.id.about)

        // Open Cluster Screen
        icCluster.setOnClickListener {
            val intent = Intent(this, MainActivity4::class.java)
            startActivity(intent)
        }
        val profileButton = findViewById<ImageView>(R.id.topRightIcon)
        profileButton.setOnClickListener {
            val intent = Intent(this, MainActivity3::class.java)
            startActivity(intent)
        }

        // Open Profile Screen
//        icProfile.setOnClickListener {
//            val intent = Intent(this, MainActivity2::class.java)
//            startActivity(intent)
//        }

        // Go Back to Home Screen
        homeButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        // Handle settings options (you can define new activities)
        permissions.setOnClickListener {
            // Start Permissions Activity (Create it if needed)
        }
        notifications.setOnClickListener {
            // Start Notifications Activity
        }
        appearance.setOnClickListener {
            // Start Appearance Activity
        }
        about.setOnClickListener {
            // Start About Activity
        }
    }
}
