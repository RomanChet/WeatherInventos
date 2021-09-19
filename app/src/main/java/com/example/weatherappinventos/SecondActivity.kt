package com.example.weatherappinventos

import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.whenCreated
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherappinventos.apiprocessing.WeatherApiClient
import com.example.weatherappinventos.database.WeatherDatabase
import com.example.weatherappinventos.dataclass.ForecastDataWeather
import com.example.weatherappinventos.dataclass.ForecastHourItems
import com.example.weatherappinventos.recyclerview.SecondAdapter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_second.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.util.*
import kotlin.collections.ArrayList

@Suppress("DEPRECATION")
class SecondActivity : AppCompatActivity() {

    private lateinit var apiClient: WeatherApiClient
    private var counter = true
    private val db = WeatherDatabase
    private val cityName = db.getAll(this).name

    private var hourItems: MutableList<ForecastHourItems> = ArrayList()

    private val coroutineJob = Job()
    private val mainCoroutine = CoroutineScope(Main + coroutineJob)

    init {
        mainCoroutine.launch {
            whenCreated {
                processCurrentApi()
                processForecastApi()
                rv_second.layoutManager =
                    LinearLayoutManager(this@SecondActivity, LinearLayoutManager.HORIZONTAL, false)
                rv_second.adapter = SecondAdapter(hourItems)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)
        initListeners()
        apiClient = WeatherApiClient()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        //db.insertFun(this, cityNameSecond.text.toString(), cityTempSecond.text.toString())
    }

    override fun onStart() {
        super.onStart()

        val returnedName = db.getAll(this).name
        val returnedTemp = db.getAll(this).temp

        cityNameSecond.text = returnedName
        cityTempSecond.text = returnedTemp
    }

    override fun onPause() {
        super.onPause()
        mainCoroutine.cancel()
    }


    private fun initListeners() {
        firstDescrImage.setOnClickListener {
            onClickDay(DAY_ONE)
        }
        secondDescrImage.setOnClickListener {
            onClickDay(DAY_TWO)
        }
        thirdDescrImage.setOnClickListener {
            onClickDay(DAY_THREE)
        }
    }

    private fun noDataInfo(value: Boolean) {
        if (counter) {
            Handler().postDelayed({
                progressBarSecond.visibility = View.INVISIBLE
            }, 1000)
            if (value) {
                val toast = Toast.makeText(
                    baseContext,
                    "Ошибка интернет-соединения! Попробуйте обновить страницу!",
                    Toast.LENGTH_SHORT
                )
                toast.show()
            } else {
                val toast = Toast.makeText(
                    baseContext,
                    "Ошибка загрузки! Попробуйте обновить страницу!",
                    Toast.LENGTH_SHORT
                )
                toast.show()
            }
        }
        counter = false
    }

    // локальная проверка интернет-соединеия
    private fun checkNetwork(): Boolean {
        val cm = baseContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetworkInfo
        return activeNetwork != null && activeNetwork.isConnected
    }

    private fun checkTransmissionErrors() {
        if (checkNetwork()) {
            noDataInfo(false)
        } else {
            noDataInfo(true)
        }
    }

    private suspend fun processCurrentApi() {
        try {
            val response = apiClient.weatherForecast(cityName)
            val weather: ForecastDataWeather = response
            presentData(weather)
        } catch (e: HttpException) {
            toHandleHttpErrors(e.code())
        } catch (e: Exception) {
            checkTransmissionErrors()
        }
    }

    private suspend fun processForecastApi() {
        try {
            val response = apiClient.weatherForecast(cityName)
            val weatherSec: ForecastDataWeather = response
            weatherSec.let {
                showWeekDays(it)
                addHourItems(it)
                setIcons(it)
                showForecastData(it)
                progressBarSecond.visibility = View.INVISIBLE
            }
        } catch (e: HttpException) {
            toHandleHttpErrors(e.code())
        } catch (e: Exception) {
            checkTransmissionErrors()
        }
    }

    private fun addHourItems(data: ForecastDataWeather) {
        hourItems.clear()
        val hours = listOf(0, 2, 4, 6, 8, 10, 12, 14, 16, 18, 20, 22)
        val currentDay = when (DAY_SELECTED) {
            DAY_ONE -> 0
            DAY_TWO -> 1
            DAY_THREE -> 2
            else -> 0
        }

        hours.forEach { hour ->
            hourItems.add(
                ForecastHourItems(
                    data.forecast.forecastday[currentDay].hour[hour].time.substringAfter(" "),
                    analyzeWeatherConditionIcon(data.forecast.forecastday[currentDay].hour[hour].condition.code),
                    data.forecast.forecastday[currentDay].hour[hour].temp_c.toString() + " °C"
                )
            )
        }
    }

    private fun toHandleHttpErrors(code: Int) {
        if (code == 401) {
            Handler().postDelayed({
                progressBarMain.visibility = View.INVISIBLE
            }, 1000)
            val toast = Toast.makeText(
                baseContext,
                "Не корректный API ключ",
                Toast.LENGTH_SHORT
            )
            toast.show()
        }
        if (code == 404) city_name.text = ""
    }

    private fun reduceIcons(viewIcon: ImageView, nameIcon: Int) {
        viewIcon.setImageResource(analyzeWeatherConditionIcon(nameIcon))
    }

    private fun onClickDay(selectedDay: String) {
        when (selectedDay) {
            DAY_ONE -> {
                DAY_SELECTED = DAY_ONE
            }
            DAY_TWO -> {
                DAY_SELECTED = DAY_TWO
            }
            DAY_THREE -> {
                DAY_SELECTED = DAY_THREE
            }
        }
        mainCoroutine.launch {
            processCurrentApi()
            processForecastApi()
            rv_second.adapter = SecondAdapter(hourItems)
        }
    }

    private fun setIcons(main: ForecastDataWeather) {
        reduceIcons(firstDescrImage, main.forecast.forecastday[0].day.condition.code)
        reduceIcons(secondDescrImage, main.forecast.forecastday[1].day.condition.code)
        reduceIcons(thirdDescrImage, main.forecast.forecastday[2].day.condition.code)
    }

    // возвращает значение аргумента в зависимости от имени иконки
    private fun analyzeWeatherConditionIcon(iconCode: Int): Int {
        return when (iconCode) {
            1000 -> R.drawable.sunny
            1003 -> R.drawable.sunny_intervals
            1006 -> R.drawable.white_cloud
            1009 -> R.drawable.white_cloud
            1030 -> R.drawable.black_low_cloud
            1063 -> R.drawable.heavy_rain_showers
            1066 -> R.drawable.light_snow_showers
            1069 -> R.drawable.sleet_showers
            1072 -> R.drawable.fog
            1087 -> R.drawable.thundery_showers
            1114 -> R.drawable.cloudy_with_heavy_snow
            1117 -> R.drawable.cloudy_with_heavy_snow
            1135 -> R.drawable.fog
            1147 -> R.drawable.fog
            1150 -> R.drawable.cloudy_with_heavy_rain
            1153 -> R.drawable.cloudy_with_heavy_rain
            1168 -> R.drawable.fog
            1171 -> R.drawable.cloudy_with_heavy_rain
            1180 -> R.drawable.heavy_rain_showers
            1183 -> R.drawable.cloudy_with_light_rain
            1186 -> R.drawable.light_rain_showers
            1189 -> R.drawable.cloudy_with_light_rain
            1192 -> R.drawable.heavy_rain_showers
            1195 -> R.drawable.cloudy_with_heavy_rain
            1198 -> R.drawable.cloudy_with_heavy_rain
            1201 -> R.drawable.cloudy_with_heavy_rain
            1204 -> R.drawable.cloudy_with_sleet
            1207 -> R.drawable.cloudy_with_sleet
            1210 -> R.drawable.light_snow_showers
            1213 -> R.drawable.cloudy_with_light_snow
            1216 -> R.drawable.light_snow_showers
            1219 -> R.drawable.cloudy_with_light_snow
            1222 -> R.drawable.heavy_snow_showers
            1225 -> R.drawable.cloudy_with_heavy_snow
            1237 -> R.drawable.cloudy_with_light_snow
            1240 -> R.drawable.light_rain_showers
            1243 -> R.drawable.heavy_rain_showers
            1246 -> R.drawable.heavy_rain_showers
            1249 -> R.drawable.sleet_showers
            1252 -> R.drawable.sleet_showers
            1255 -> R.drawable.light_snow_showers
            1258 -> R.drawable.heavy_snow_showers
            1261 -> R.drawable.light_snow_showers
            1264 -> R.drawable.heavy_snow_showers
            1273 -> R.drawable.thundery_showers
            1276 -> R.drawable.thunderstorms
            1279 -> R.drawable.thundery_showers
            1282 -> R.drawable.thunderstorms
            else -> R.drawable.sunny_intervals
        }
    }

    // перевод и установка представляемых данных текущей температуры
    private fun presentData(data: ForecastDataWeather) {
        cityNameSecond.text = data.location.name
        when (DAY_SELECTED) {
            DAY_ONE -> {
                reduceIcons(mainDescrImage, data.forecast.forecastday[0].day.condition.code)
                min_maxTemp.text = "За сутки: min  ${
                    String.format(
                        "%.1f",
                        data.forecast.forecastday[0].day.mintemp_c
                    )
                } °C    max  ${
                    String.format(
                        "%.1f",
                        data.forecast.forecastday[0].day.maxtemp_c
                    )
                } °C"

                cityTempSecond.text = "${String.format("%.1f", data.current.temp_c)} °C"
                cityDescrSecond.text = data.current.condition.text
                weekDate.text = getSelectedDayDate(data, DAY_ONE)
            }
            DAY_TWO -> {
                reduceIcons(mainDescrImage, data.forecast.forecastday[1].day.condition.code)
                min_maxTemp.text = "За сутки: min  ${
                    String.format(
                        "%.1f",
                        data.forecast.forecastday[1].day.mintemp_c
                    )
                } °C    max  ${
                    String.format(
                        "%.1f",
                        data.forecast.forecastday[1].day.maxtemp_c
                    )
                } °C"

                cityTempSecond.text =
                    "${String.format("%.1f", data.forecast.forecastday[1].day.avgtemp_c)} °C"
                cityDescrSecond.text = data.forecast.forecastday[1].day.condition.text
                weekDate.text = getSelectedDayDate(data, DAY_TWO)
            }
            DAY_THREE -> {
                reduceIcons(mainDescrImage, data.forecast.forecastday[2].day.condition.code)
                min_maxTemp.text = "За сутки: min  ${
                    String.format(
                        "%.1f",
                        data.forecast.forecastday[2].day.mintemp_c
                    )
                } °C    max  ${
                    String.format(
                        "%.1f",
                        data.forecast.forecastday[2].day.maxtemp_c
                    )
                } °C"

                cityTempSecond.text =
                    "${String.format("%.1f", data.forecast.forecastday[2].day.avgtemp_c)} °C"
                cityDescrSecond.text = data.forecast.forecastday[2].day.condition.text
                weekDate.text = getSelectedDayDate(data, DAY_THREE)
            }
        }

    }

    // извлекатель дня недели из UNIX даты
    private fun weekDayValue(unixTime: Long): List<String> {
        val toUsualDate = Date(unixTime * 1000).toString()
        return toUsualDate.split(" ")
    }

    // переводчик дней недели
    private fun translateWeekDays(weekDay: String): String {
        return when (weekDay) {
            "Mon" -> "Понедельник"
            "Tue" -> "Вторник"
            "Wed" -> "Среда"
            "Thu" -> "Четверг"
            "Fri" -> "Пятница"
            "Sat" -> "Суббота"
            "Sun" -> "Воскресенье"
            else -> ""
        }
    }

    private fun translateWeekDays(dt: Long): String {
        return translateWeekDays(weekDayValue(dt)[0])
    }

    private fun getSelectedDayDate(data: ForecastDataWeather, selectedDay: String): String {
        val constPrDt = when (selectedDay) {
            DAY_ONE -> weekDayValue(data.forecast.forecastday[0].date_epoch.toLong())
            DAY_TWO -> weekDayValue(data.forecast.forecastday[1].date_epoch.toLong())
            DAY_THREE -> weekDayValue(data.forecast.forecastday[2].date_epoch.toLong())
            else -> weekDayValue(data.forecast.forecastday[0].date_epoch.toLong())
        }
        var st = constPrDt[0]
        var monthName = constPrDt[1]
        val numberDay = constPrDt[2]

        monthName = when (monthName) {
            "Jan" -> "Января"
            "Feb" -> "Февраля"
            "Mar" -> "Марта"
            "Apr" -> "Апреля"
            "May" -> "Мая"
            "Jun" -> "Июня"
            "Jul" -> "Июля"
            "Aug" -> "Августа"
            "Sep" -> "Сентября"
            "Oct" -> "Октября"
            "Nov" -> "Ноября"
            "Dec" -> "Декабря"
            else -> ""
        }

        st = when (st) {
            "Mon" -> "Понедельник"
            "Tue" -> "Вторник"
            "Wed" -> "Среда"
            "Thu" -> "Четверг"
            "Fri" -> "Пятница"
            "Sat" -> "Суббота"
            "Sun" -> "Воскресенье"
            else -> ""
        }
        return "${st},                  $numberDay $monthName"
    }

    // извлечение и перевод дней недели из приходящей в UNIX формате даты
    private fun showWeekDays(main: ForecastDataWeather) {
        date_day_one.text =
            "${translateWeekDays(main.forecast.forecastday[0].date_epoch.toLong())}:"
        date_day_two.text =
            "${translateWeekDays(main.forecast.forecastday[1].date_epoch.toLong())}:"
        date_day_three.text =
            "${translateWeekDays(main.forecast.forecastday[2].date_epoch.toLong())}:"
    }

    // представление данных, прогнозируеммой погоды
    private fun showForecastData(main: ForecastDataWeather) {
        // Макс темп за день
        temp_max_one.text =
            "Max t:\n${String.format("%.1f", main.forecast.forecastday[0].day.maxtemp_c)} °C"
        temp_max_two.text =
            "Max t:\n${String.format("%.1f", main.forecast.forecastday[1].day.maxtemp_c)} °C"
        temp_max_three.text =
            "Max t:\n${String.format("%.1f", main.forecast.forecastday[2].day.maxtemp_c)} °C"

        // Мин темп за день
        temp_min_one.text =
            "Min t:\n${String.format("%.1f", main.forecast.forecastday[0].day.mintemp_c)} °C"
        temp_min_two.text =
            "Min t:\n${String.format("%.1f", main.forecast.forecastday[1].day.mintemp_c)} °C"
        temp_min_three.text =
            "Min t:\n${String.format("%.1f", main.forecast.forecastday[2].day.mintemp_c)} °C"

        // Средняя влажность за день
        hum_day_one.text = "Влажность:\n${main.forecast.forecastday[0].day.avghumidity} %"
        hum_day_two.text = "Влажность:\n${main.forecast.forecastday[1].day.avghumidity} %"
        hum_day_three.text = "Влажность:\n${main.forecast.forecastday[2].day.avghumidity} %"

        // Макс скор ветра км ч
        wind_speed_one.text = "Ветер, Vmax:\n${main.forecast.forecastday[0].day.maxwind_kph} км/ч"
        wind_speed_two.text = "Ветер, Vmax:\n${main.forecast.forecastday[1].day.maxwind_kph} км/ч"
        wind_speed_three.text = "Ветер, Vmax:\n${main.forecast.forecastday[2].day.maxwind_kph} км/ч"

        // Время восхода солнца
        sunrise_time_one.text =
            "Восход:\n${main.forecast.forecastday[0].astro.sunrise.substringBefore(' ')}"
        sunrise_time_two.text =
            "Восход:\n${main.forecast.forecastday[1].astro.sunrise.substringBefore(' ')}"
        sunrise_time_three.text =
            "Восход:\n${main.forecast.forecastday[2].astro.sunrise.substringBefore(' ')}"

        // Время заката
        sunset_time_one.text =
            "Закат:\n${main.forecast.forecastday[0].astro.sunset.substringBefore(' ')}"
        sunset_time_two.text =
            "Закат:\n${main.forecast.forecastday[1].astro.sunset.substringBefore(' ')}"
        sunset_time_three.text =
            "Закат:\n${main.forecast.forecastday[2].astro.sunset.substringBefore(' ')}"

        // погодные условния (прогноз)
        descr_day_one.text = main.forecast.forecastday[0].day.condition.text
        descr_day_two.text = main.forecast.forecastday[1].day.condition.text
        descr_day_three.text = main.forecast.forecastday[2].day.condition.text
    }

    companion object {
        private var DAY_SELECTED = "First Day"
        private const val DAY_ONE = "First Day"
        private const val DAY_TWO = "Second Day"
        private const val DAY_THREE = "Third Day"
    }
}
