package com.example.weatherappinventos

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_test.*
import java.util.*


class TestAct : AppCompatActivity() {

    companion object { // для того, чтобы обращаться к методам и свойствам объекта
        // через имя содержащего его класса без явного указания имени объекта.
        // (к тотал_коунт можно обратиться за классом)
        const val TOTAL_COUNT = "total_count" // объявляем переменную-ключ для (приема)передачи данных
        // из другого активити (будет использоваться в PutExtra)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)
        showName()
    }

    // функция извлечения значения из ТОТАЛ_КАУНТ
    fun showName() {
        val count : String = intent.getStringExtra(TOTAL_COUNT) //извлечение значения из интент маин активити
        textView2.text = count // для наглядности
    }
}

