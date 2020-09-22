package com.example.weatherappinventos.DataClass

data class DataWeather(
    val coord: Coord,
    val cnt : Int,
    val list : List<List1>,
    val weather: List<Weather>,
    val base: String,
    val main: Main,
    val message : Int,
    val visibility: Int,
    val wind: Wind,
    val clouds: Clouds,
    val dt: Long,
    val sys: Sys,
    val timezone: Int,
    val id: Int,
    val name: String,
    val cod: Int,
    val city : City
)

data class Main(
    val temp : Double,
    val feels_like : Double,
    val temp_min : Double,
    val temp_max : Double,
    val pressure : Int,
    val sea_level : Int,
    val grnd_level : Int,
    val humidity : Int,
    val temp_kf : Double
)

data class City (
    val id : Int,
    val name : String,
    val coord : Coord,
    val country : String,
    val population : Int,
    val timezone : Int,
    val sunrise : Int,
    val sunset : Int
)


data class List1 (
    val dt : Long,
    val main : Main,
    val weather : List<Weather>,
    val clouds : Clouds,
    val wind : Wind,
    val visibility : Int,
    val pop : Double,
    val sys : Sys,
    val dt_txt : String
)

data class Weather (
    val id: Int,
    val main: String,
    val description: String,
    val icon: String
)

data class Clouds(
    val all : Int
)

data class Coord(
    val lon : Double,
    val lat : Double
)

data class Sys(
    val type : Int,
    val id : Int,
    val country : String,
    val sunrise : Int,
    val sunset : Int,
    val pod : String
)

data class Wind(
    val speed : Double,
    val deg : Int
)

data class MainItem(
    val name: String,
    val temp: String
)

data class TempItem(
    val temp: String
)
