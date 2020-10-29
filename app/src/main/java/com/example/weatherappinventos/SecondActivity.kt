package com.example.weatherappinventos

import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Handler
import android.view.Gravity
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import com.example.weatherappinventos.apiprocessing.WeatherApiClient
import com.example.weatherappinventos.database.*
import com.example.weatherappinventos.dataclass.CurrentDataWeather
import com.example.weatherappinventos.dataclass.ForecastDataWeather
import kotlinx.android.synthetic.main.activity_second.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

@Suppress("DEPRECATION")
class SecondActivity : AppCompatActivity() {

    private lateinit var apiClient: WeatherApiClient
    private var counter = true
    private val db = WeatherDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)

        apiClient = WeatherApiClient(this)

        processCurrentApi()
        processForecastApi()
        swipeRefreshSecond()
    }

    override fun onBackPressed() {
        super.onBackPressed()

        db.action(this, "deleteAll")
        db.action(this, "insert", cityNameSecond.text.toString(), cityTempSecond.text.toString())
    }

    override fun onStart() {
        super.onStart()

        val returnedName = db.getAll(this).name
        val returnedTemp = db.getAll(this).temp

        cityNameSecond.text = returnedName
        cityTempSecond.text = returnedTemp

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
                toast.setGravity(Gravity.CENTER, 0, 0)
                toast.show()
            } else {
                val toast = Toast.makeText(
                    baseContext,
                    "Ошибка загрузки! Попробуйте обновить страницу!",
                    Toast.LENGTH_SHORT
                )
                toast.setGravity(Gravity.CENTER, 0, 0)
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

    private fun swipeRefreshSecond() {
        val swipeRefresh: SwipeRefreshLayout = findViewById(R.id.go_refresh)
        val runnable = Runnable {
            counter = true
            processCurrentApi()
            processForecastApi()
            swipeRefresh.isRefreshing = false
        }

        swipeRefresh.setOnRefreshListener { swipeRefresh.postDelayed(runnable, 800L) }

        go_refresh.setColorSchemeResources(
            android.R.color.holo_orange_light,
            android.R.color.holo_red_light
        )
    }

    private fun processCurrentApi() {
        val count: String? = intent.getStringExtra(PLACE_NAME)
        count?.let { apiClient.currentWeather(it) }?.enqueue(object :
            Callback<CurrentDataWeather> { // асинхронный запрос, на основе описанного ранее метода
            override fun onFailure(call: Call<CurrentDataWeather>?, t: Throwable?) {
                t?.printStackTrace()
                checkTransmissionErrors()
            }

            override fun onResponse(
                call: Call<CurrentDataWeather>?,
                response: Response<CurrentDataWeather>?
            ) {
                if (response != null) {
                    val weather: CurrentDataWeather? = response.body()
                    weather?.main
                    weather?.let {
                        presentData(it)
                    }
                }
            }
        })
    }

    private fun processForecastApi() {
        val countSecond: String? = intent.getStringExtra(PLACE_NAME)
        countSecond?.let { apiClient.weatherForecast(it) }?.enqueue(object :
            Callback<ForecastDataWeather> { // асинхронный запрос, на основе описанного ранее метода
            override fun onFailure(call: Call<ForecastDataWeather>?, t: Throwable?) {
                t?.printStackTrace()
                checkTransmissionErrors()
            }

            override fun onResponse(
                call: Call<ForecastDataWeather>?,
                response: Response<ForecastDataWeather>?
            ) {
                if (response != null) {
                    val weatherSec: ForecastDataWeather? = response.body()
                    weatherSec?.list?.get(0)?.main
                    weatherSec?.let {
                        showWeekDays(it)
                        setIcons(it)
                        showForecastData(it)
                        progressBarSecond.visibility = View.INVISIBLE
                    }
                }
            }
        })
    }

    private fun reduceIcons(viewIcon: ImageView, nameIcon: String) {
        viewIcon.setImageResource(analyzeWeatherConditionIcon(nameIcon))
    }

    fun setIcons(main: ForecastDataWeather) {
        reduceIcons(mainDescrImage, main.list[0].weather[0].icon)
        reduceIcons(firstDescrImage, main.list[7].weather[0].icon)
        reduceIcons(secondDescrImage, main.list[15].weather[0].icon)
        reduceIcons(thirdDescrImage, main.list[23].weather[0].icon)
        reduceIcons(fourthDescrImage, main.list[31].weather[0].icon)
        reduceIcons(fifthDescrImage, main.list[39].weather[0].icon)
    }

    // возвращает значение аргумента в зависимости от имени иконки
    private fun analyzeWeatherConditionIcon(iconName: String): Int {
        return when (iconName) {
            "01d" -> R.drawable.r01d
            "01n" -> R.drawable.r01n
            "02d" -> R.drawable.r02d
            "02n" -> R.drawable.r02n
            "03d" -> R.drawable.r03d
            "03n" -> R.drawable.r03n
            "04d" -> R.drawable.r04d
            "04n" -> R.drawable.r04n
            "09d" -> R.drawable.r09d
            "09n" -> R.drawable.r09n
            "10d" -> R.drawable.r10d
            "10n" -> R.drawable.r10n
            "11d" -> R.drawable.r11d
            "11n" -> R.drawable.r11n
            "13d" -> R.drawable.r13d
            "13n" -> R.drawable.r13n
            "50d" -> R.drawable.r50d
            "50n" -> R.drawable.r50n
            else -> R.drawable.r02d
        }
    }

    // перевод и установка представляемых данных текущей температуры
    private fun presentData(main: CurrentDataWeather) {
        val constPrDt = weekDayValue(main.dt)
        var st = constPrDt[0]
        var mounthName = constPrDt[1]
        val numberDay = constPrDt[2]

        mounthName = when (mounthName) {
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
        with(main) {
            cityNameSecond.text = main.name
            cityTempSecond.text = "${main.main.temp} °C"
            cityDescrSecond.text = main.weather[0].description
            weekDate.text = "${st},                  ${numberDay} ${mounthName}"
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

    // извлечение и перевод дней недели из приходящей в UNIX формате даты
    fun showWeekDays(main: ForecastDataWeather) {
        nameDay1.text = "${translateWeekDays(main.list[7].dt)}:"
        nameDay2.text = "${translateWeekDays(main.list[15].dt)}:"
        nameDay3.text = "${translateWeekDays(main.list[23].dt)}:"
        nameDay4.text = "${translateWeekDays(main.list[31].dt)}:"
        nameDay5.text = "${translateWeekDays(main.list[39].dt)}:"
    }

    // представление данных, прогнозируеммой погоды
    fun showForecastData(main: ForecastDataWeather) {
        // прогноз температуры на 5 дней (1 колонка)
        dayOneTemp.text = "${main.list[7].main.temp} °C"
        dayTwoTemp.text = "${main.list[15].main.temp} °C"
        dayThreeTemp.text = "${main.list[23].main.temp} °C"
        dayFourTemp.text = "${main.list[31].main.temp} °C"
        dayFiveTemp.text = "${main.list[39].main.temp} °C"

        // прогноз ощущаемой температуры на 5 дней (2 колонка)
        dayOneLikeTemp.text = "${main.list[7].main.feels_like} °C"
        dayTwoLikeTemp.text = "${main.list[15].main.feels_like} °C"
        dayThreeLikeTemp.text = "${main.list[23].main.feels_like} °C"
        dayFourLikeTemp.text = "${main.list[31].main.feels_like} °C"
        dayFiveLikeTemp.text = "${main.list[39].main.feels_like} °C"

        // погодные условния (прогноз)
        descrText1.text = main.list[7].weather[0].description
        descrText2.text = main.list[15].weather[0].description
        descrText3.text = main.list[23].weather[0].description
        descrText4.text = main.list[31].weather[0].description
        descrText5.text = main.list[39].weather[0].description
    }

    companion object {
        const val PLACE_NAME = "place_name"
    }
}
