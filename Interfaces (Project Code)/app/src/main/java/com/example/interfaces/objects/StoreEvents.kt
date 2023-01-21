package com.example.interfaces.objects

import android.util.Log
import com.example.interfaces.database.Reminders

object StoreEvents { // this is just to store the values so i can delete specific events if needed
    val events = mutableListOf<String>() //creates empty list of strings

    fun parseEventsData(data: MutableList<Reminders>?) {
        events.clear() // clears it so not past data is stored, resets every time when needed
        if (data != null) {
            for (i in 0 until data.size) {
                events.add(data[i].date) // adds the date of the event
                events.add(data[i].place) // adds the place of the event
                events.add(data[i].startTime) // adds the startTime of the event
                events.add(data[i].finishTime) // adds the finishTime of the event
            }
        }

        if (data != null) { // if  data is not null it then proceeds to print the values out
            Log.e("Store Events", events.toString()) // this is just to debug
            Log.e("Store Events", events.size.toString())
        }
    }
}