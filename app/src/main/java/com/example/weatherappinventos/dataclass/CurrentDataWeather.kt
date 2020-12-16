package com.example.weatherappinventos.dataclass

data class CurrentDataWeather(
    val coord: Coord,
    val weather: List<Weather>,
    val base: String,
    val main: Main,
    val visibility: Int,
    val wind: Wind,
    val clouds: Clouds,
    val dt: Long,
    val sys: CurrentSys,
    val timezone: Int,
    val id: Int,
    val name: String,
    val cod: Int
)

data class Main(
    val temp: Double,
    val feels_like: Double,
    val temp_min: Double,
    val temp_max: Double,
    val pressure: Int,
    val sea_level: Int,
    val grnd_level: Int,
    val humidity: Int,
    val temp_kf: Double
)

data class Weather(
    val id: Int,
    val main: String,
    val description: String,
    val icon: String
)

data class Clouds(
    val all: Int
)

data class Coord(
    val lon: Double,
    val lat: Double
)

data class CurrentSys(
    val type: Int,
    val id: Int,
    val country: String,
    val sunrise: Int,
    val sunset: Int,
    val pod: String
)

data class Wind(
    val speed: Double,
    val deg: Int
)

data class MainItem(
    val name: String,
    val temp: String
)