package com.example.weatherappinventos

import android.os.Bundle
import android.os.Handler
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.ImageView
import com.example.weatherappinventos.API.SecondNetworkClient
import com.example.weatherappinventos.API.WeatherNetworkClient
import com.example.weatherappinventos.DataClass.DataWeather
import kotlinx.android.synthetic.main.activity_second.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*


class SecondActivity : AppCompatActivity() {

    // переменные для обновления по свайпу вниз
    private lateinit var handler: Handler
    private lateinit var runnable: Runnable

    companion object { // для того, чтобы обращаться к методам и свойствам объекта
        // через имя содержащего его класса без явного указания имени объекта.
        // (к тотал_коунт можно обратиться за классом)
        const val TOTAL_COUNT = "total_count" // объявляем переменную-ключ для (приема)передачи данных
        // из другого активити (будет использоваться в PutExtra)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)

        // выполнение запросов на основании Интента, от первого активити
        // две функии потому, что нет такого запроса (бесплатного) в котором будут в ответе все необходимые данные
        showName()
        showNameSecond()

        // обновление активити по свайпу
        handler = Handler()
        val swipeRefresh = findViewById<SwipeRefreshLayout>(R.id.go_refresh)
        swipeRefresh.setOnRefreshListener {
            runnable = Runnable {
                showName()
                showNameSecond()
                swipeRefresh.isRefreshing = false
            }
            handler.postDelayed(
                runnable, 800.toLong()
            )
        }
        go_refresh.setColorSchemeResources(
            android.R.color.holo_blue_bright,
            android.R.color.holo_green_light,
            android.R.color.holo_orange_light,
            android.R.color.holo_red_light
        )
    }

    // тут творится волшебство(выполнение и обработка запроса к API)
    // функция извлечения значения из ТОТАЛ_КАУНТ
    private fun showName() {
        val count : String = intent.getStringExtra(TOTAL_COUNT) //извлечение значения из интент маин активити
        /////////////////////////////////////////////////////////////////////////////////////////////////////////
        val network = WeatherNetworkClient(applicationContext)
        val call = network.clientCall(count) // передаем город в качестве аргумента в функцию запроса, Call - Синхронно отправить запрос и вернуть его ответ.
        call.enqueue(object : Callback<DataWeather> { // объект для получения ответа
            override fun onFailure(call: Call<DataWeather>?, t: Throwable?) {
                t?.printStackTrace()
            }
            override fun onResponse(call: Call<DataWeather>?, response: Response<DataWeather>?) {
                if (response != null) {
                    val weather: DataWeather? = response.body()
                    weather?.main
                    weather?.let {
                        presentData(it)
                    }
                }
            }
        })
    }

    // тут творится волшебство(выполнение и обработка запроса к API)
    // функция извлечения значения из ТОТАЛ_КАУНТ
    private fun showNameSecond() {
        val countSecond : String = intent.getStringExtra(TOTAL_COUNT) //извлечение значения из интент маин активити
        /////////////////////////////////////////////////////////////////////////////////////////////////////////
        val networkSec = SecondNetworkClient(applicationContext)
        val call = networkSec.clientCall(countSecond) // передаем город в качестве аргумента в функцию запроса, Call - Синхронно отправить запрос и вернуть его ответ.
        call.enqueue(object : Callback<DataWeather> { // объект для получения ответа
            override fun onFailure(call: Call<DataWeather>?, t: Throwable?) {
                t?.printStackTrace()
            }
            override fun onResponse(call: Call<DataWeather>?, response: Response<DataWeather>?) {
                if (response != null) {
                    val weatherSec: DataWeather? = response.body()
                    weatherSec?.main
                    weatherSec?.let {
                        downPresentData(it)
                        loadIcon(it)
                        progressBar2.visibility = View.INVISIBLE
                    }
                }
            }
        })
    }

    // функция, отвечающая за инцилизацию иконок
    private fun loadIcon(main: DataWeather) {
        findViewById<ImageView>(R.id.mainDescrImage)
        findViewById<ImageView>(R.id.firstDescrImage)
        findViewById<ImageView>(R.id.secondDescrImage)
        findViewById<ImageView>(R.id.thirdDescrImage)
        findViewById<ImageView>(R.id.fourthDescrImage)
        findViewById<ImageView>(R.id.fifthDescrImage)

        val mainIcon = main.list[0].weather[0].icon
        val firstDayIcon = main.list[7].weather[0].icon
        val secondDayIcon = main.list[15].weather[0].icon
        val thirdDayIcon = main.list[23].weather[0].icon
        val fourthDayIcon = main.list[31].weather[0].icon
        val fifthDayIcon = main.list[39].weather[0].icon

        when (mainIcon) {
            "01d",  -> mainDescrImage.setImageResource(R.drawable.r01d)
            "01n" -> mainDescrImage.setImageResource(R.drawable.r01n)
            "02d" -> mainDescrImage.setImageResource(R.drawable.r02d)
            "02n" -> mainDescrImage.setImageResource(R.drawable.r02n)
            "03d" -> mainDescrImage.setImageResource(R.drawable.r03d)
            "03n" -> mainDescrImage.setImageResource(R.drawable.r03n)
            "04d" -> mainDescrImage.setImageResource(R.drawable.r04d)
            "04n" -> mainDescrImage.setImageResource(R.drawable.r04n)
            "09d" -> mainDescrImage.setImageResource(R.drawable.r09d)
            "09n" -> mainDescrImage.setImageResource(R.drawable.r09n)
            "10d" -> mainDescrImage.setImageResource(R.drawable.r10d)
            "10n" -> mainDescrImage.setImageResource(R.drawable.r10n)
            "11d" -> mainDescrImage.setImageResource(R.drawable.r11d)
            "11n" -> mainDescrImage.setImageResource(R.drawable.r11n)
            "13d" -> mainDescrImage.setImageResource(R.drawable.r13d)
            "13n" -> mainDescrImage.setImageResource(R.drawable.r13n)
            "50d" -> mainDescrImage.setImageResource(R.drawable.r50d)
            "50n" -> mainDescrImage.setImageResource(R.drawable.r50n)
            else -> {
                mainDescrImage.setImageResource(R.drawable.r02d)
            }
        }

        when (firstDayIcon) {
            "01d" -> firstDescrImage.setImageResource(R.drawable.r01d)
            "01n" -> firstDescrImage.setImageResource(R.drawable.r01n)
            "02d" -> firstDescrImage.setImageResource(R.drawable.r02d)
            "02n" -> firstDescrImage.setImageResource(R.drawable.r02n)
            "03d" -> firstDescrImage.setImageResource(R.drawable.r03d)
            "03n" -> firstDescrImage.setImageResource(R.drawable.r03n)
            "04d" -> firstDescrImage.setImageResource(R.drawable.r04d)
            "04n" -> firstDescrImage.setImageResource(R.drawable.r04n)
            "09d" -> firstDescrImage.setImageResource(R.drawable.r09d)
            "09n" -> firstDescrImage.setImageResource(R.drawable.r09n)
            "10d" -> firstDescrImage.setImageResource(R.drawable.r10d)
            "10n" -> firstDescrImage.setImageResource(R.drawable.r10n)
            "11d" -> firstDescrImage.setImageResource(R.drawable.r11d)
            "11n" -> firstDescrImage.setImageResource(R.drawable.r11n)
            "13d" -> firstDescrImage.setImageResource(R.drawable.r13d)
            "13n" -> firstDescrImage.setImageResource(R.drawable.r13n)
            "50d" -> firstDescrImage.setImageResource(R.drawable.r50d)
            "50n" -> firstDescrImage.setImageResource(R.drawable.r50n)
            else -> {
                firstDescrImage.setImageResource(R.drawable.r02d)
            }
        }

        when (secondDayIcon) {
            "01d" -> secondDescrImage.setImageResource(R.drawable.r01d)
            "01n" -> secondDescrImage.setImageResource(R.drawable.r01n)
            "02d" -> secondDescrImage.setImageResource(R.drawable.r02d)
            "02n" -> secondDescrImage.setImageResource(R.drawable.r02n)
            "03d" -> secondDescrImage.setImageResource(R.drawable.r03d)
            "03n" -> secondDescrImage.setImageResource(R.drawable.r03n)
            "04d" -> secondDescrImage.setImageResource(R.drawable.r04d)
            "04n" -> secondDescrImage.setImageResource(R.drawable.r04n)
            "09d" -> secondDescrImage.setImageResource(R.drawable.r09d)
            "09n" -> secondDescrImage.setImageResource(R.drawable.r09n)
            "10d" -> secondDescrImage.setImageResource(R.drawable.r10d)
            "10n" -> secondDescrImage.setImageResource(R.drawable.r10n)
            "11d" -> secondDescrImage.setImageResource(R.drawable.r11d)
            "11n" -> secondDescrImage.setImageResource(R.drawable.r11n)
            "13d" -> secondDescrImage.setImageResource(R.drawable.r13d)
            "13n" -> secondDescrImage.setImageResource(R.drawable.r13n)
            "50d" -> secondDescrImage.setImageResource(R.drawable.r50d)
            "50n" -> secondDescrImage.setImageResource(R.drawable.r50n)
            else -> {
                secondDescrImage.setImageResource(R.drawable.r02d)
            }
        }

        when (thirdDayIcon) {
            "01d" -> thirdDescrImage.setImageResource(R.drawable.r01d)
            "01n" -> thirdDescrImage.setImageResource(R.drawable.r01n)
            "02d" -> thirdDescrImage.setImageResource(R.drawable.r02d)
            "02n" -> thirdDescrImage.setImageResource(R.drawable.r02n)
            "03d" -> thirdDescrImage.setImageResource(R.drawable.r03d)
            "03n" -> thirdDescrImage.setImageResource(R.drawable.r03n)
            "04d" -> thirdDescrImage.setImageResource(R.drawable.r04d)
            "04n" -> thirdDescrImage.setImageResource(R.drawable.r04n)
            "09d" -> thirdDescrImage.setImageResource(R.drawable.r09d)
            "09n" -> thirdDescrImage.setImageResource(R.drawable.r09n)
            "10d" -> thirdDescrImage.setImageResource(R.drawable.r10d)
            "10n" -> thirdDescrImage.setImageResource(R.drawable.r10n)
            "11d" -> thirdDescrImage.setImageResource(R.drawable.r11d)
            "11n" -> thirdDescrImage.setImageResource(R.drawable.r11n)
            "13d" -> thirdDescrImage.setImageResource(R.drawable.r13d)
            "13n" -> thirdDescrImage.setImageResource(R.drawable.r13n)
            "50d" -> thirdDescrImage.setImageResource(R.drawable.r50d)
            "50n" -> thirdDescrImage.setImageResource(R.drawable.r50n)
            else -> {
                thirdDescrImage.setImageResource(R.drawable.r02d)
            }
        }

        when (fourthDayIcon) {
            "01d" -> fourthDescrImage.setImageResource(R.drawable.r01d)
            "01n" -> fourthDescrImage.setImageResource(R.drawable.r01n)
            "02d" -> fourthDescrImage.setImageResource(R.drawable.r02d)
            "02n" -> fourthDescrImage.setImageResource(R.drawable.r02n)
            "03d" -> fourthDescrImage.setImageResource(R.drawable.r03d)
            "03n" -> fourthDescrImage.setImageResource(R.drawable.r03n)
            "04d" -> fourthDescrImage.setImageResource(R.drawable.r04d)
            "04n" -> fourthDescrImage.setImageResource(R.drawable.r04n)
            "09d" -> fourthDescrImage.setImageResource(R.drawable.r09d)
            "09n" -> fourthDescrImage.setImageResource(R.drawable.r09n)
            "10d" -> fourthDescrImage.setImageResource(R.drawable.r10d)
            "10n" -> fourthDescrImage.setImageResource(R.drawable.r10n)
            "11d" -> fourthDescrImage.setImageResource(R.drawable.r11d)
            "11n" -> fourthDescrImage.setImageResource(R.drawable.r11n)
            "13d" -> fourthDescrImage.setImageResource(R.drawable.r13d)
            "13n" -> fourthDescrImage.setImageResource(R.drawable.r13n)
            "50d" -> fourthDescrImage.setImageResource(R.drawable.r50d)
            "50n" -> fourthDescrImage.setImageResource(R.drawable.r50n)
            else -> {
                fourthDescrImage.setImageResource(R.drawable.r02d)
            }
        }

        when (fifthDayIcon) {
            "01d" -> fifthDescrImage.setImageResource(R.drawable.r01d)
            "01n" -> fifthDescrImage.setImageResource(R.drawable.r01n)
            "02d" -> fifthDescrImage.setImageResource(R.drawable.r02d)
            "02n" -> fifthDescrImage.setImageResource(R.drawable.r02n)
            "03d" -> fifthDescrImage.setImageResource(R.drawable.r03d)
            "03n" -> fifthDescrImage.setImageResource(R.drawable.r03n)
            "04d" -> fifthDescrImage.setImageResource(R.drawable.r04d)
            "04n" -> fifthDescrImage.setImageResource(R.drawable.r04n)
            "09d" -> fifthDescrImage.setImageResource(R.drawable.r09d)
            "09n" -> fifthDescrImage.setImageResource(R.drawable.r09n)
            "10d" -> fifthDescrImage.setImageResource(R.drawable.r10d)
            "10n" -> fifthDescrImage.setImageResource(R.drawable.r10n)
            "11d" -> fifthDescrImage.setImageResource(R.drawable.r11d)
            "11n" -> fifthDescrImage.setImageResource(R.drawable.r11n)
            "13d" -> fifthDescrImage.setImageResource(R.drawable.r13d)
            "13n" -> fifthDescrImage.setImageResource(R.drawable.r13n)
            "50d" -> fifthDescrImage.setImageResource(R.drawable.r50d)
            "50n" -> fifthDescrImage.setImageResource(R.drawable.r50n)
            else -> {
                fifthDescrImage.setImageResource(R.drawable.r02d)
            }
        }
    }

    // функция прописывающая отображение данных из датаклассов во вью
    private fun presentData(main: DataWeather) {
        val dt = main.dt
        val date = Date(dt*1000).toString()
        val str = date.split(" ")
        var st = str[0]
        var mounthName = str[1]
        val numberDay = str[2]

        when (mounthName) {
            "Jan" -> mounthName = "Января"
            "Feb" -> mounthName = "Февраля"
            "Mar" -> mounthName = "Марта"
            "Apr" -> mounthName = "Апреля"
            "May" -> mounthName = "Мая"
            "Jun" -> mounthName = "Июня"
            "Jul" -> mounthName = "Июля"
            "Aug" -> mounthName = "Августа"
            "Sep" -> mounthName = "Сентября"
            "Oct" -> mounthName = "Октября"
            "Nov" -> mounthName = "Ноября"
            "Dec" -> mounthName = "Декабря"
        }

        when (st) {
            "Mon" -> st = "Понедельник"
            "Tue" -> st = "Вторник"
            "Wed" -> st = "Среда"
            "Thu" -> st = "Четверг"
            "Fri" -> st = "Пятница"
            "Sat" -> st = "Суббота"
            "Sun" -> st = "Воскресенье"
        }
        with(main) {
            cityNameSecond.text = main.name
            cityTempSecond.text = "${main.main.temp} °C"
            cityDescrSecond.text = main.weather[0].description
            weekDate.text = "${st},                  ${numberDay} ${mounthName}"
        }
    }

    private fun downPresentData(main: DataWeather) {
        //0 7 15 23 31 39 это 9.00 утра
        val dtDayWeek1 = main.list[7].dt
        val date1 = Date(dtDayWeek1*1000).toString()
        val str1 = date1.split(" ")
        var st1 = str1[0]
        when (st1) {
            "Mon" -> st1 = "Понедельник"
            "Tue" -> st1 = "Вторник"
            "Wed" -> st1 = "Среда"
            "Thu" -> st1 = "Четверг"
            "Fri" -> st1 = "Пятница"
            "Sat" -> st1 = "Суббота"
            "Sun" -> st1 = "Воскресенье"
        }
        //0 7 15 23 31 39 это 9.00 утра
        val dtDayWeek2 = main.list[15].dt
        val date2 = Date(dtDayWeek2*1000).toString()
        val str2 = date2.split(" ")
        var st2 = str2[0]
        when (st2) {
            "Mon" -> st2 = "Понедельник"
            "Tue" -> st2 = "Вторник"
            "Wed" -> st2 = "Среда"
            "Thu" -> st2 = "Четверг"
            "Fri" -> st2 = "Пятница"
            "Sat" -> st2 = "Суббота"
            "Sun" -> st2 = "Воскресенье"
        } //0 7 15 23 31 39 это 9.00 утра
        val dtDayWeek3 = main.list[23].dt
        val date3 = Date(dtDayWeek3*1000).toString()
        val str3 = date3.split(" ")
        var st3 = str3[0]
        when (st3) {
            "Mon" -> st3 = "Понедельник"
            "Tue" -> st3 = "Вторник"
            "Wed" -> st3 = "Среда"
            "Thu" -> st3 = "Четверг"
            "Fri" -> st3 = "Пятница"
            "Sat" -> st3 = "Суббота"
            "Sun" -> st3 = "Воскресенье"
        } //0 7 15 23 31 39 это 9.00 утра
        val dtDayWeek4 = main.list[31].dt
        val date4 = Date(dtDayWeek4*1000).toString()
        val str4 = date4.split(" ")
        var st4 = str4[0]
        when (st4) {
            "Mon" -> st4 = "Понедельник"
            "Tue" -> st4 = "Вторник"
            "Wed" -> st4 = "Среда"
            "Thu" -> st4 = "Четверг"
            "Fri" -> st4 = "Пятница"
            "Sat" -> st4 = "Суббота"
            "Sun" -> st4 = "Воскресенье"
        } //0 7 15 23 31 39 это 9.00 утра
        val dtDayWeek5 = main.list[39].dt
        val date5 = Date(dtDayWeek5*1000).toString()
        val str5 = date5.split(" ")
        var st5 = str5[0]
        when (st5) {
            "Mon" -> st5 = "Понедельник"
            "Tue" -> st5 = "Вторник"
            "Wed" -> st5 = "Среда"
            "Thu" -> st5 = "Четверг"
            "Fri" -> st5 = "Пятница"
            "Sat" -> st5 = "Суббота"
            "Sun" -> st5 = "Воскресенье"
        }
        with(main) {
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

            // 5 ближайих названий дней недели
            nameDay1.text = "${st1}:"
            nameDay2.text = "${st2}:"
            nameDay3.text = "${st3}:"
            nameDay4.text = "${st4}:"
            nameDay5.text = "${st5}:"
        }
    }
}
