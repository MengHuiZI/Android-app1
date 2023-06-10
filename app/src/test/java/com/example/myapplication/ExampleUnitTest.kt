package com.example.myapplication

import org.junit.Test

import org.junit.Assert.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }
    fun getDate():String{
        val calendar = Calendar.getInstance()
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd")
//        val year=calendar.get(Calendar.YEAR)
//        val date=calendar.get(Calendar.MONTH)
//        val day=calendar.get(Calendar.DAY_OF_MONTH)
        return simpleDateFormat.format(calendar.time)
    }
    @Test
    fun a(){
        val a=getDate()
        println(a)
    }
}