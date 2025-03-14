package com.example.plantpal

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.plantpal.R

class PlantAdapter(private val plantList: List<Plant>) :
    RecyclerView.Adapter<PlantAdapter.PlantViewHolder>() {

    class PlantViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val plantName: TextView = itemView.findViewById(R.id.plantName)
        val plantDescription: TextView = itemView.findViewById(R.id.plantDescription)
        val joinButton: Button = itemView.findViewById(R.id.joinButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlantViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.plant_items, parent, false)
        return PlantViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlantViewHolder, position: Int) {
        val plant = plantList[position]
        holder.plantName.text = plant.name
        holder.plantDescription.text = plant.description
        holder.joinButton.setOnClickListener {
            // Handle join button click
        }
    }

    override fun getItemCount(): Int {
        return plantList.size
    }
}
