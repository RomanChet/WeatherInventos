package com.example.weatherappinventos

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import com.example.weatherappinventos.API.WeatherNetworkClient
import com.example.weatherappinventos.DataClass.DataWeather
import com.example.weatherappinventos.DataClass.MainItem
import com.example.weatherappinventos.MainResView.MainAdapter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.city_item.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
/**
    От меня:
    Спасибо за прекрасное задание! В процессе его выполнения разобрался как работатает API вообще, с такими базовыми вещами как RecyclerView,
Data Class и со всем причастным. У меня было время не отвлекаясь, понять эти моменты. Самое крутое то, что с каждым днем, решая множество мелких задач,
всё быстрее понимал как их можно сделать.
    Всегда хотел реализовать себя в будущем как разработчик и вот, первый наношаг. Хочется набираться оыта, решать задачи, искать и находить решения
каждый день!
    Надеюсь, что для работы новичка самоучки это хороший результат. Очень интересно услышать Ваше мнение. Готов и хочу работать дальше!
    Не знаком с Вами лично, но очень захотелось поделиться! С уважением, Рома Четвериков!
*/

class MainActivity : AppCompatActivity() {

    // создание массива данных,  в котором будет содержаться списов городов на главной странице
    private var items : MutableList<MainItem>? = ArrayList()

    // переменные для обновления по свайпу вниз
    private lateinit var handler: Handler
    private lateinit var runnable: Runnable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // загрузка данных SharedPreference
        loadData()

        // обновление активити по свайпу
        handler = Handler()
        val swipeRefresh = findViewById<SwipeRefreshLayout>(R.id.go_refreshMain)
        swipeRefresh.setOnRefreshListener {
            runnable = Runnable {
                city_name.text = ""
                currentTemp.text = ""
                descr.text = ""
                swipeRefresh.isRefreshing = false
            }
            handler.postDelayed(
                runnable, 800.toLong()
            )
        }
        go_refreshMain.setColorSchemeResources(
            android.R.color.holo_blue_bright,
            android.R.color.holo_green_light,
            android.R.color.holo_orange_light,
            android.R.color.holo_red_light
        )

         // клик по элементу приходит сюда, и дальше передается название города во второе активити
         val myAdapter = items?.let {
             MainAdapter(it, object : MainAdapter.Callback {
                 override fun onItemClicked(item: MainItem) {
                     val goTestActivityIntent = Intent(this@MainActivity, SecondActivity::class.java)
                     val counterString = item.name // преобразование объекта в строку
                     goTestActivityIntent.putExtra(SecondActivity.TOTAL_COUNT, counterString) // добавляет значение в интент (ключ, и соответсвующее ему
                     // значение, которое потом получается при риеме ключа другим активити)
                     startActivity(goTestActivityIntent) // запуск активити
                 }
             })

         }

        // привязываем ресайклервью к адаптеру (инцилизация)
        myRecycler.adapter = myAdapter

        // удаление свайпом влево
        val swipeHandler = object : SwipeToDeleteCallback(this) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val adapter = myAdapter
                if (adapter != null) {
                    adapter.removeAt(viewHolder.adapterPosition)
                }
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(myRecycler)

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
                    val weather: DataWeather? = response.body()
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
            if(cityNameText.text.toString() == ""){
                city_name.text = ""
                currentTemp.text = ""
                descr.text = ""
            }
            else{
                city_name.text = main.name
                currentTemp.text = "${main.main.temp} °C"
                descr.text = main.weather[0].description
            }
        }
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

    // жена попросила чтобы до добавления в избранное открывать детальный прогноз, кликнув на название населенного пункта вверху. Сделал так.
    fun onNameClicked(view: View) {
        if(city_name.text.toString() == "") {
        }
        else {
            val goTestActivityIntent = Intent(this@MainActivity, SecondActivity::class.java)
            val counterString = city_name.text // преобразование объекта в строку
            goTestActivityIntent.putExtra(
                SecondActivity.TOTAL_COUNT,
                counterString
            ) // добавляет значение в интент (ключ, и соответсвующее ему
            // значение, которое потом получается при риеме ключа другим активити)
            startActivity(goTestActivityIntent) // запуск активити
        }
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
