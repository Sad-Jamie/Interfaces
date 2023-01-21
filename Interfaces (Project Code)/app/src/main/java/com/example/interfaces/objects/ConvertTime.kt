package com.example.interfaces.objects

object ConvertTime {

    fun formatting(Time: String): Int {
        val delimiter = ":" // the reference to which the string is split by
        val parts = Time.split(delimiter, ignoreCase = true) //splits the time XX:YY to 2 parts XX and YY
        val hourToMinutes = (parts[0].toInt()) * 60 // converts the hours into minutes
        val minutesParse = parts[1].toInt() // leaves the minutes  and stores
        return hourToMinutes + minutesParse  // returns the total of the hours in minutes + minutes
    }
}