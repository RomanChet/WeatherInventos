package com.example.weatherappinventos

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.whenCreated
import androidx.lifecycle.whenResumed
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.weatherappinventos.apiprocessing.WeatherApiClient
import com.example.weatherappinventos.database.WeatherDatabase
import com.example.weatherappinventos.dataclass.CurrentDataWeather
import com.example.weatherappinventos.dataclass.MainItem
import com.example.weatherappinventos.recyclerview.MainAdapter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.city_item.*
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Main
import retrofit2.HttpException
import java.lang.Runnable


@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {

    private var items: MutableList<MainItem> = ArrayList()

    private lateinit var apiClient: WeatherApiClient
    private var counter = true
    private val db = WeatherDatabase

    private val coroutineJob = Job()
    private val mainCoroutine = CoroutineScope(Main + coroutineJob)

    init {
        mainCoroutine.launch {
            whenCreated {
                if (checkNetwork()) {
                    iterateItems()
                } else {
                    noDataInfo(true)
                }
            }
            whenResumed {
                getWeatherFromName()
                iterateItems()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        apiClient = WeatherApiClient()

        db.start(applicationContext)

        loadData()
        swipeRefresh()
        listenerEditName()

        val myAdapter = MainAdapter(items, object : MainAdapter.Callback {
            override fun onItemClicked(item: MainItem) {
                val goSecondActivityIntent = Intent(
                    applicationContext, SecondActivity::class.java
                )
                val counterName = item.name
                val counterTemp = item.temp

                db.insertFun(applicationContext, counterName, counterTemp)

                startActivity(goSecondActivityIntent)
            }
        })

        myRecycler.adapter = myAdapter

        val swipeHandler = object : SwipeToDelete(applicationContext) {
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
                cityNameText.text = null // очистка строки ввода для удобства
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

        val returnedName = db.getAll(applicationContext).name
        val returnedTemp = db.getAll(applicationContext).temp

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
        mainCoroutine.cancel()
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
            mainCoroutine.launch { whenCreated { iterateItems() } }
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
                mainCoroutine.launch { getWeatherFromName(city) }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })
    }

    suspend fun getWeatherFromName(city: String = "") {
        try {
            val response = apiClient.currentWeather(city)
            val weather: CurrentDataWeather = response
            presentData(weather)
            city_name.text = weather.name

        } catch (e: HttpException) {
            toHandleHttpErrors(e.code(), counter)

        } catch (e: Exception) {
            city_name.text = city
        }
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

    private suspend fun iterateItems() {
        items.forEach { getWeatherListTemp(it.name) }
    }

    private suspend fun getWeatherListTemp(city: String = "") {
        try {
            val response = apiClient.currentWeather(city)
            val weather: CurrentDataWeather = response
            weather.let {
                progressBarMain.visibility = View.INVISIBLE
                setupDataTemp(it)
            }

        } catch (e: HttpException) {
            toHandleHttpErrors(e.code(), counter)

        } catch (e: Exception) {
            checkTransmissionErrors()
        }
    }

    private fun toHandleHttpErrors(code: Int, value: Boolean) {
        if (code == 401 && value) {
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
        if (code == 404 && value) city_name.text = ""
        counter = false
    }

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
            db.insertFun(this, city_name.text.toString(), currentTemp.text.toString())
            startActivity(goTestActivityIntent)
        }
    }
}
