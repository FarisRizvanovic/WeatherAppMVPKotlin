package com.example.kotlinweatherappmvp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.kotlinweatherappmvp.R
import com.example.kotlinweatherappmvp.model.Daily
import java.text.SimpleDateFormat
import java.util.*

class WeatherRecViewAdapter(
    private val dailyItems: List<Daily>,
    private val context: Context
) : RecyclerView.Adapter<WeatherRecViewAdapter.ViewHolder>() {

    private val days =
        arrayListOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtWeatherForecastDescription: TextView =
            itemView.findViewById(R.id.txtWeatherForecastDescription)
        val txtWeatherTemperature: TextView = itemView.findViewById(R.id.txtWeatherTemperature)
        val txtDay: TextView = itemView.findViewById(R.id.txtDay)
        val imgWeatherForecastIcon: ImageView = itemView.findViewById(R.id.imgWeatherForecastIcon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_weather, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val description = dailyItems[position].weather[0].description.replaceFirstChar {
            it.uppercase()
        }

        val temperature = dailyItems[position].temp.day.toInt().toString() + "°C"

        val iconLink =
            "http://openweathermap.org/img/wn/${dailyItems[position].weather[0].icon}@2x.png"

        Glide.with(context)
            .load(iconLink)
            .into(holder.imgWeatherForecastIcon)

        holder.txtWeatherTemperature.text = temperature
        holder.txtWeatherForecastDescription.text = description
        holder.txtDay.text = getDay(position)
    }

    private fun getDay(position: Int): String {
        val calendar = Calendar.getInstance()
        val date = calendar.time

        val dayToday = SimpleDateFormat("EEEE", Locale.ENGLISH).format(date.time)
        var pos = 0

        for (i in days.indices) {
            if (days[i] == dayToday) {
                pos = i
                break
            }
        }
        pos += position + 1
        if (pos >= 7) {
            pos -= 7
        }
        return days[pos]
    }

    override fun getItemCount(): Int {
        return dailyItems.size - 1
    }
}