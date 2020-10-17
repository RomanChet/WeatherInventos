package com.example.weatherappinventos

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.weatherappinventos.apiprocessing.WeatherApiClient
import com.example.weatherappinventos.database.*
import com.example.weatherappinventos.dataclass.CurrentDataWeather
import com.example.weatherappinventos.dataclass.MainItem
import com.example.weatherappinventos.recyclerview.MainAdapter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {

    private var items: MutableList<MainItem> = ArrayList()

    private lateinit var apiClient: WeatherApiClient
    private var counter = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        apiClient = WeatherApiClient(this)

        WeatherDatabase.startDb(this)

        loadData()
        swipeRefresh()
        listenerEditName()

        if (checkNetwork()) {
            iterateItems()
        } else {
            noDataInfo(true)
        }

        val myAdapter = MainAdapter(items, object : MainAdapter.Callback {
            override fun onItemClicked(item: MainItem) {
                val goSecondActivityIntent = Intent(
                    this@MainActivity,
                    SecondActivity::class.java
                )
                val counterName = item.name
                val counterTemp = item.temp
                goSecondActivityIntent.putExtra(
                    SecondActivity.PLACE_NAME,
                    counterName
                )

                WeatherDatabase.getDbDao(this@MainActivity).deleteAll()
                WeatherDatabase.getDbDao(this@MainActivity)
                    .insert(WeatherEntity(counterName, counterTemp))

                startActivity(goSecondActivityIntent)
            }
        })

        myRecycler.adapter = myAdapter

        val swipeHandler = object : SwipeToDelete(this) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                myAdapter.removeAt(viewHolder.adapterPosition)
            }
        }

        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(myRecycler)

        addbutton.setOnClickListener() {
            if (city_name.text.toString() == "") {
                items
            } else {
                items.add(MainItem(city_name.text.toString(), currentTemp.text.toString()))
                cityNameText.text = null // обнуление строки ввода для удобства
                city_name.text = ""
                currentTemp.text = ""
                descr.text = ""
                myAdapter.notifyDataSetChanged()
            }
        }
    }

    override fun onResume() {
        super.onResume()

        counter = true
        iterateItems()

        val returnedName = WeatherDatabase.getDbAll(this).name
        val returnedTemp = WeatherDatabase.getDbAll(this).temp

        val index = items.indexOfFirst {
            it.name == returnedName
        }
        if (index != -1) {
            items[index] = MainItem(returnedName, returnedTemp)
            refreshAdapter()
        }

    }

    override fun onPause() {
        super.onPause()
        saveData()
    }

    private fun noDataInfo(value: Boolean) {
        if (counter) {
            Handler().postDelayed({
                progressBarMain.visibility = View.INVISIBLE
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

    private fun saveData() {
        val sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val gson = Gson()
        val json = gson.toJson(items)
        editor.putString("task list", json)
        editor.apply()
    }

    private fun loadData() {
        val sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE)
        val gson = Gson()
        val json = sharedPreferences.getString("task list", items.toString())
        val type = object : TypeToken<ArrayList<MainItem>>() {}.type
        items = if (json == null)
            ArrayList()
        else
            gson.fromJson(json, type)
    }

    private fun refreshAdapter() {
        myRecycler.adapter?.notifyDataSetChanged()
    }

    private fun swipeRefresh() {
        val swipeRefresh: SwipeRefreshLayout = findViewById(R.id.go_refreshMain)
        val runnable = Runnable {
            counter = true
            city_name.text = ""
            currentTemp.text = ""
            descr.text = ""
            swipeRefresh.isRefreshing = false
            iterateItems()
            refreshAdapter()
        }

        swipeRefresh.setOnRefreshListener { swipeRefresh.postDelayed(runnable, 800L) }

        go_refreshMain.setColorSchemeResources(
            android.R.color.holo_orange_light,
            android.R.color.holo_red_light
        )
    }

    private fun listenerEditName() {
        cityNameText.addTextChangedListener(object :
            TextWatcher {
            override fun afterTextChanged(edit: Editable?) {
                val city = edit.toString()
                getWeatherFromName(city)
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })
    }

    fun getWeatherFromName(city: String) {
        val call = apiClient.currentWeather(city)
        call.enqueue(object : Callback<CurrentDataWeather> {
            override fun onFailure(call: Call<CurrentDataWeather>, t: Throwable?) {
                t?.printStackTrace()
                checkTransmissionErrors()
            }

            override fun onResponse(
                call: Call<CurrentDataWeather>, response: Response<CurrentDataWeather>
            ) {
                val weather: CurrentDataWeather? = response.body()
                val main = weather?.main
                weather?.let {
                    presentData(it)
                }
            }
        })
    }

    private fun presentData(main: CurrentDataWeather) {
        with(main) {
            if (cityNameText.text.toString() == "") {
                city_name.text = ""
                currentTemp.text = ""
                descr.text = ""
            } else {
                city_name.text = main.name
                currentTemp.text = "${main.main.temp} °C"
                descr.text = main.weather[0].description
            }
        }
    }

    private fun iterateItems() {
        items.forEach { getWeatherListTemp(it.name) }
    }

    // выполнение и обработка запроса к API
    private fun getWeatherListTemp(city: String) {
        val call = apiClient.currentWeather(city)
        call.enqueue(object : Callback<CurrentDataWeather> { // асинхронный запрос
            override fun onFailure(call: Call<CurrentDataWeather>, t: Throwable?) {
                t?.printStackTrace()
                checkTransmissionErrors()
            }

            override fun onResponse(
                call: Call<CurrentDataWeather>,
                response: Response<CurrentDataWeather>
            ) {
                val weather: CurrentDataWeather? = response.body()
                val main = weather?.main
                weather?.let {
                    progressBarMain.visibility = View.INVISIBLE
                    setupDataTemp(it)
                }
            }
        })
    }

    // функция прописывающая отображение данных из датаклассов во вью
    private fun setupDataTemp(main: CurrentDataWeather) {

        val index =
            items.indexOfFirst {
                it.name == main.name
            }
        if (index != -1) {
            items[index] = MainItem(main.name, "${main.main.temp} °C")
        }
        refreshAdapter()
    }

    fun onNameClicked(view: View) {
        if (city_name.text.toString() == "") {
            items
        } else {
            val goTestActivityIntent = Intent(this@MainActivity, SecondActivity::class.java)
            val counterString = city_name.text // преобразование объекта в строку
            goTestActivityIntent.putExtra(
                SecondActivity.PLACE_NAME,
                counterString
            )
            startActivity(goTestActivityIntent)
        }
    }
}
