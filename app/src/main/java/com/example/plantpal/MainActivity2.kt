package com.example.plantpal

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity2 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main2)

        // Set up RecyclerView
        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = PlantAdapter(getPlantList())

        // Handle BottomNavigationView clicks
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottomNav.selectedItemId = R.id.g
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(intent)
                    finish()
                    true
                }
                R.id.search -> {
                    startActivity(Intent(this, MainActivity5::class.java))
                    true
                }
                R.id.g -> true
                else -> false
            }
        }
    }

    private fun getPlantList(): List<Plant> {
        return listOf(
            Plant("Plant Care", "Connect with fellow parents and share tips"),
            Plant("Flowers", "Flower caring community"),
            Plant("Snake Plant", "Snake plant care tips"),
            Plant("Basil Co.", "Everything basil")
        )
    }
}
