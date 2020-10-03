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
import com.example.weatherappinventos.dataclass.CurrentDataWeather
import com.example.weatherappinventos.dataclass.MainItem
import com.example.weatherappinventos.recyclerview.MainAdapter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_second.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {

    private var items: MutableList<MainItem> = ArrayList()

    private lateinit var apiClient: WeatherApiClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        apiClient = WeatherApiClient(this)

        loadData()
        swipeRefresh()
        listenerEditName()

        val myAdapter = MainAdapter(items, object : MainAdapter.Callback {
            override fun onItemClicked(item: MainItem) {
                val goSecondActivityIntent = Intent(
                    this@MainActivity,
                    SecondActivity::class.java
                )
                val counterString = item.name
                goSecondActivityIntent.putExtra(
                    SecondActivity.PLACE_NAME,
                    counterString
                )
                startActivity(goSecondActivityIntent)
                finish()
            }
        })

        myRecycler.adapter = myAdapter

        iterateItems()

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

    override fun onStart() {
        super.onStart()

        val returnedName: String? = intent.getStringExtra(RETURNED_PLACE_NAME)
        val returnedTemp: String? = intent.getStringExtra(RETURNED_PLACE_TEMP)

        val index =
            items.indexOfFirst {
                it.name == returnedName
            }
        if (index != -1) {
            items[index] = MainItem(returnedName.toString(), returnedTemp.toString())
        }
        refreshAdapter()
        checkNetwork()
    }

    override fun onPause() {
        super.onPause()
        saveData()
    }

    private fun checkNetwork() {
        val cm = baseContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetworkInfo
        if (activeNetwork != null && activeNetwork.isConnected) {
            items
        } else {
            Handler().postDelayed({
                progressBarMain.visibility = View.INVISIBLE
            }, 1000)
            val toast = Toast.makeText(
                baseContext,
                "Ошибка загрузки! Попробуйте обновить страницу!",
                Toast.LENGTH_SHORT
            )
            toast.setGravity(Gravity.CENTER, 0, 0)
            toast.show()
        }
    }

    private fun invalidRequest() {
        Handler().postDelayed({
            progressBarSecond.visibility = View.INVISIBLE
        }, 1000)
        val toast = Toast.makeText(
            baseContext,
            "Ошибка загрузки! Попробуйте обновить страницу!",
            Toast.LENGTH_SHORT
        )
        toast.setGravity(Gravity.CENTER, 0, 0)
        toast.show()
    }

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
            city_name.text = ""
            currentTemp.text = ""
            descr.text = ""
            swipeRefresh.isRefreshing = false
            iterateItems()
            refreshAdapter()
            checkNetwork()
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
                invalidRequest()
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
            finish()
        }
    }

    companion object {
        const val RETURNED_PLACE_NAME = "returned_place_name"
        const val RETURNED_PLACE_TEMP = "returned_place_temp"
    }
}