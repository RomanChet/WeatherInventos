package com.example.weatherappinventos.recyclerview

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.example.weatherappinventos.R
import com.example.weatherappinventos.dataclass.ForecastHourItems

class SecondAdapter(var hourItems: MutableList<ForecastHourItems>) :
    RecyclerView.Adapter<SecondAdapter.MainHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        MainHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.second_forecast_item, parent, false)
        )

    override fun getItemCount() = hourItems.size

    override fun onBindViewHolder(holder: MainHolder, position: Int) {
        holder.bind(hourItems[position])
    }

    inner class MainHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val hourValue = itemView.findViewById<TextView>(R.id.hourValue)
        private val icon = itemView.findViewById<ImageView>(R.id.hourIcon)
        private val hourTemp = itemView.findViewById<TextView>(R.id.hourTemp)

        fun bind(item: ForecastHourItems) {
            hourValue.text = item.hour
            icon.setImageResource(item.icon)
            hourTemp.text = item.hourTemp
        }
    }
}