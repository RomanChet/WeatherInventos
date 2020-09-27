package com.example.weatherappinventos

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.ItemTouchHelper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import com.example.weatherappinventos.apiprocessing.WeatherApiClient
import com.example.weatherappinventos.dataclass.CurrentDataWeather
import com.example.weatherappinventos.dataclass.MainItem
import com.example.weatherappinventos.recyclerview.MainAdapter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    // создание массива данных,  в котором будет содержаться списов городов на главной странице
    private var items : MutableList<MainItem>? = ArrayList()

    private lateinit var handler: Handler
    private lateinit var apiClient: WeatherApiClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        apiClient = WeatherApiClient(this)

        loadData()
        swipeRefresh()
        listenerEditName()

        // клик по элементу приходит сюда, и дальше передается название города во второе активити
         val myAdapter = items?.let {
             MainAdapter(it, object : MainAdapter.Callback {
                 override fun onItemClicked(item: MainItem) {
                     val goSecondActivityIntent = Intent (
                         this@MainActivity,
                         SecondActivity::class.java
                     )
                     val counterString = item.name // преобразование объекта в строку
                     goSecondActivityIntent.putExtra(
                         SecondActivity.PLACE_NAME,
                         counterString
                     ) // добавляет значение в интент (ключ, и соответсвующее ему
                     // значение, которое потом получается при риеме ключа другим активити)
                     startActivity(goSecondActivityIntent) // запуск активити
                 }
             })
        }

        // привязываем ресайклервью к адаптеру (инцилизация)
        myRecycler.adapter = myAdapter

        itemsIterrator()

        // кнопка добавления города из поля ввода в список
        addbutton.setOnClickListener() {
           if(city_name.text.toString() == "") {
               items = items
           }
           else{
               items?.add(MainItem(city_name.text.toString(), currentTemp.text.toString()))
               cityNameText.text = null // обнуление строки ввода для удобства
               city_name.text = ""
               currentTemp.text = ""
               descr.text = ""
               myAdapter?.notifyDataSetChanged()
           }
        }

        val swipeHandler = object : SwipeToDelete(this) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val adapter = myAdapter
                if (adapter != null) {
                    adapter.removeAt(viewHolder.adapterPosition)
                }
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(myRecycler)
    }

    private fun listenerEditName(){
        // это функция принимает ввод имени города и передает его в getWeatherFromName
        cityNameText.addTextChangedListener(object :
            TextWatcher {
            override fun afterTextChanged(edit: Editable?) { // отслеживает изменения в строке
                val city = edit.toString()
                getWeatherFromName(city)
            }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })
    }

    private fun refreshAdapter() {
        val myAdapter = myRecycler.adapter
        if (myAdapter != null) {
            myAdapter.notifyDataSetChanged()
        }
    }

    private fun swipeRefresh(){
        val swipeRefresh: SwipeRefreshLayout = findViewById(R.id.go_refreshMain)
        val runnable = Runnable {
            city_name.text = ""
            currentTemp.text = ""
            descr.text = ""
            swipeRefresh.isRefreshing = false
            refreshAdapter()
        }

     swipeRefresh.setOnRefreshListener { swipeRefresh.postDelayed(runnable, 800L) }

     go_refreshMain.setColorSchemeResources(
         android.R.color.holo_orange_light,
         android.R.color.holo_red_light
     )
    }

    private fun itemsIterrator() {
        items?.forEach { getWeatherListTemp(it.name) }
        items?.clear()
    }

    // выполнение и обработка запроса к API
    fun getWeatherFromName(city: String) {
        val call = apiClient.currentWeather(city)
        call.enqueue(object : Callback<CurrentDataWeather> { // объект для получения ответа
            override fun onFailure(call: Call<CurrentDataWeather>?, t: Throwable?) {
                t?.printStackTrace()
            }
            override fun onResponse(call: Call<CurrentDataWeather>?, response: Response<CurrentDataWeather>?) {
                if (response != null) {
                    val weather: CurrentDataWeather? = response.body()
                    val main = weather?.main
                    weather?.let {
                        presentData(it)
                    }
                }
            }
        })
    }

    // функция прописывающая отображение данных из датаклассов во вью
    private fun presentData(main: CurrentDataWeather) {
        with(main) {
            if(cityNameText.text.toString() == ""){
                city_name.text = ""
                currentTemp.text = ""
                descr.text = ""
            }
            else {
                city_name.text = main.name
                currentTemp.text = "${main.main.temp} °C"
                descr.text = main.weather[0].description
            }
        }
    }

    // выполнение и обработка запроса к API
    private fun getWeatherListTemp(city: String) {
        val call = apiClient.currentWeather(city) // передаем город в качестве аргумента в функцию запроса, Call - Синхронно отправить запрос и вернуть его ответ.
        call.enqueue(object : Callback<CurrentDataWeather> { // объект для получения ответа
            override fun onFailure(call: Call<CurrentDataWeather>?, t: Throwable?) {
                t?.printStackTrace()
            }
            override fun onResponse(call: Call<CurrentDataWeather>?, response: Response<CurrentDataWeather>?) {
                if (response != null) {
                    val weather: CurrentDataWeather? = response.body()
                    val main = weather?.main
                    weather?.let {
                        updateDataTemp(it)
                        progressBarMain.visibility = View.INVISIBLE
                    }
                }
            }
        })
    }

    // функция прописывающая отображение данных из датаклассов во вью
    private fun updateDataTemp(main: CurrentDataWeather) {
        items?.add(MainItem(main.name, "${main.main.temp} °C"))
        refreshAdapter()
    }

    // функция сохранения данных sharedPreferences
    private fun saveData() {
        val sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val gson = Gson()
        val json = gson.toJson(items)
        editor.putString("task list", json)
        editor.apply()
    }

    // функция чтения данных sharedPreferences
    private fun loadData() {
        val sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE)
        val gson = Gson()
        val json = sharedPreferences.getString("task list", items.toString())
        val type = object: TypeToken<ArrayList<MainItem>>() {}.type
        items = if(json == null)
            ArrayList()
        else
            gson.fromJson(json, type)
    }

    fun onNameClicked(view: View) {
        if(city_name.text.toString() == "") {
        }
        else {
            val goTestActivityIntent = Intent(this@MainActivity, SecondActivity::class.java)
            val counterString = city_name.text // преобразование объекта в строку
            goTestActivityIntent.putExtra(
                SecondActivity.PLACE_NAME,
                counterString
            ) // добавляет значение в интент (ключ, и соответсвующее ему
            // значение, которое потом получается при приеме ключа другим активити)
            startActivity(goTestActivityIntent) // запуск активити
        }
    }

    override fun onPause() {
        super.onPause()
        saveData()
    }
}