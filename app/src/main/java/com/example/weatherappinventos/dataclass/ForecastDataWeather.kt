package com.example.weatherappinventos.dataclass

data class ForecastDataWeather(
    val cod: Int,
    val message : Int,
    val cnt : Int,
    val list : List<ForecastDaysList>,
    val city : ForecastCity
)

data class ForecastDaysList (
    val dt : Long,
    val main : Main,
    val weather : List<Weather>,
    val clouds : Clouds,
    val wind : Wind,
    val visibility : Int,
    val pop : Double,
    val sys : ForecastSys,
    val dt_txt : String
)

data class ForecastSys (
    val pod : String
)

data class ForecastCity (
    val id : Int,
    val name : String,
    val coord : Coord,
    val country : String,
    val population : Int,
    val timezone : Int,
    val sunrise : Int,
    val sunset : Int
)
