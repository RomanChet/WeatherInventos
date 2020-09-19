package com.example.weatherappinventos.MainResView

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.weatherappinventos.R

class MainAdapter(var items: MutableList<MainItem>, val callback: Callback) : RecyclerView.Adapter<MainAdapter.MainHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        MainHolder(LayoutInflater.from(parent.context).inflate(R.layout.city_item, parent, false))

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: MainHolder, position: Int) {
        holder.bind(items[position])
    }

    inner class MainHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val cityName = itemView.findViewById<TextView>(R.id.cityName)
        private val cityTemp = itemView.findViewById<TextView>(R.id.cityTemp)

        fun bind(item: MainItem) {
            cityName.text = item.name
            cityTemp.text = item.temp
            itemView.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) callback.onItemClicked(items[adapterPosition])
            }
        }
    }

    interface Callback {
        fun onItemClicked(item: MainItem)
    }
}