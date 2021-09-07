package com.example.weatherappinventos.dataclass

import android.widget.ImageView

data class ForecastDataWeather(
    val location: LocationForecast,
    val current: CurrentForecast,
    val forecast: Forecast
)

data class Forecast(
    val forecastday: List<Forecastday>
)

data class Forecastday(
    val date: String,
    val date_epoch: Int,
    val day: Day,
    val astro: Astro,
    val hour: List<Hour>
)

data class Day(
    val maxtemp_c: Double,
    val maxtemp_f: Double,
    val mintemp_c: Double,
    val mintemp_f: Double,
    val avgtemp_c: Double,
    val avgtemp_f: Double,
    val maxwind_mph: Double,
    val maxwind_kph: Double,
    val totalprecip_mm: Double,
    val totalprecip_in: Double,
    val avgvis_km: Double,
    val avgvis_miles: Double,
    val avghumidity: Int,
    val daily_will_it_rain: Double,
    val daily_chance_of_rain: Double,
    val daily_will_it_snow: Double,
    val daily_chance_of_snow: Double,
    val condition: ConditionForecast,
    val uv: Double
)

data class Astro(
    val sunrise: String,
    val sunset: String,
    val moonrise: String,
    val moonset: String,
    val moon_phase: String,
    val moon_illumination: Double
)

data class Hour(
    val time_epoch: Int,
    val time: String,
    val temp_c: Double,
    val temp_f: Float,
    val is_day: Int,
    val condition: ConditionForecast,
    val wind_mph: Double,
    val wind_kph: Double,
    val wind_degree: Int,
    val wind_dir: String,
    val pressure_mb: Double,
    val pressure_in: Double,
    val precip_mm: Double,
    val precip_in: Double,
    val humidity: Int,
    val cloud: Int,
    val feelslike_c: Double,
    val feelslike_f: Double,
    val windchill_c: Double,
    val windchill_f: Double,
    val heatindex_c: Double,
    val heatindex_f: Double,
    val dewpoint_c: Double,
    val dewpoint_f: Double,
    val will_it_rain: Int,
    val chance_of_rain: Int,
    val will_it_snow: Int,
    val chance_of_snow: Double,
    val vis_km: Double,
    val vis_miles: Double,
    val gust_mph: Double,
    val gust_kph: Double,
    val uv: Double
)

data class LocationForecast(
    val name: String,
    val region: String,
    val country: String,
    val lat: Double,
    val lon: Double,
    val tz_id: String,
    val localtime_epoch: Int,
    val localtime: String
)

data class CurrentForecast(
    val last_updated_epoch: Int,
    val last_updated: String,
    val temp_c: Double,
    val temp_f: Double,
    val is_day: Int,
    val condition: ConditionForecast,
    val wind_mph: Double,
    val wind_kph: Double,
    val wind_degree: Int,
    val wind_dir: String,
    val pressure_mb: Double,
    val pressure_in: Double,
    val precip_mm: Double,
    val precip_in: Double,
    val humidity: Int,
    val cloud: Int,
    val feelslike_c: Double,
    val feelslike_f: Double,
    val vis_km: Double,
    val vis_miles: Double,
    val uv: Double,
    val gust_mph: Double,
    val gust_kph: Double
)

data class ConditionForecast(
    val text: String,
    val icon: String,
    val code: Int
)

data class ForecastHourItems(
    val hour: String,
    val icon: Int,
    val hourTemp: String
)
