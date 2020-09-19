package com.example.weatherappinventos

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import com.example.weatherappinventos.API.WeatherNetworkClient
import com.example.weatherappinventos.DataClass.DataWeather
import com.example.weatherappinventos.MainResView.MainAdapter
import com.example.weatherappinventos.MainResView.MainItem
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    // создание массива данных,  в котором будет содержаться списов городов на главной странице
    private var items : MutableList<MainItem>? = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

         // загрузка данных SharedPreference
         loadData()

         // клик по элементу приходит сюда, и дальше передается название города во второе активити
         val myAdapter = items?.let {
             MainAdapter(it, object : MainAdapter.Callback {
                 override fun onItemClicked(item: MainItem) {
                     val goTestActivityIntent = Intent(this@MainActivity, TestAct::class.java)
                     val counterString = item.name // преобразование объекта с нулем в строку
                     goTestActivityIntent.putExtra(TestAct.TOTAL_COUNT, counterString) // добавляет значение в интент (ключ, и соответсвующее ему
                     // значение, которое потом получается при риеме ключа другим активити)
                     startActivity(goTestActivityIntent) // запуск активити
                 }
             })
         }

        // привязываем ресайклервью к адаптеру (инцилизация)
        myRecycler.adapter = myAdapter

        // кнопка добавления города из поля ввода в список
        addbutton.setOnClickListener() {
            items?.add(MainItem(city_name.text as String, currentTemp.text as String))
            myAdapter?.notifyDataSetChanged()
        }

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
    // тут творится волшебство(выполнение и обработка запроса к API)
    fun getWeatherFromName(city: String) {
        val network = WeatherNetworkClient(applicationContext)
        val call = network.clientCall(city) // передаем город в качестве аргумента в функцию запроса, Call - Синхронно отправить запрос и вернуть его ответ.
        call.enqueue(object : Callback<DataWeather> { // объект для получения ответа
            override fun onFailure(call: Call<DataWeather>?, t: Throwable?) {
                t?.printStackTrace()
            }

            override fun onResponse(call: Call<DataWeather>?, response: Response<DataWeather>?) {
                if (response != null) {
                    val weather: DataWeather? = response?.body()
                    val main = weather?.main
                    weather?.let {
                        presentData(it)
                    }
                }
            }
        })
    }

    // функция прописывающая отображение данных из датаклассов во вью
    private fun presentData(main: DataWeather) {
        with(main) {
            descr.text = main.weather[0].description
            city_name.text = main.name
            currentTemp.text = "Temp: ${main.main.temp}"
            lowTemp.text = "Temp l: ${main.main.temp_min}"
            highTemp.text = "Temp h: ${main.main.temp_max}"
        }
    }

    // функция для кнопки перехода ко 2 активити
    fun goTestActivity(view: View) {
        val goTestActivityIntent = Intent(this, TestAct::class.java)
        // запуск активити
        startActivity(goTestActivityIntent)
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
    // автоматическое сохранение данных при выходе из приложения
    override fun onDestroy() {
        super.onDestroy()
        saveData()
    }
}

// сделать ResView список +
// хранить его в БД +
// этот список должен оставаться при перезапуске приложения +
// вью каждого элемента списка содержит имя и температуру +
// настроить редактирование (удалять, добавлять элемент) этой БД
// погода на главном экране сделана на основе того, как уже работает, только значение параметра запроса city (в запросе это параметр "q") берется из БД
// при нажатии на элемент, использовать его текстовое значение (параметр q или же city) и передавать его во второе активити, которое работает по принципу исходника (делается запрос, парсинг на основе города)

// https://www.youtube.com/watch?v=r6MQ4VAHI_U&t=910s
// работаем с массивом LIST. в нем можно по имени элемента получать айди. по этому айди можно удалять элемент.
// этот массив при выходе из приложения преобразуется в джейсон строку и с помощью SharedPreferences сохраняется
// при новом запуске  помощью SharedPreferences получаем строку, делаем обратное преобразование в List и выводим его элементы в RecyclerView