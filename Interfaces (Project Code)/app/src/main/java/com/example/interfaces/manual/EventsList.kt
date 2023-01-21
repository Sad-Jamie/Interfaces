package com.example.interfaces.manual

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation

import androidx.navigation.fragment.navArgs
import com.example.interfaces.R
import com.example.interfaces.database.DatabaseHandler
import com.example.interfaces.objects.BotResponse
import com.example.interfaces.objects.StoreEvents

import kotlinx.android.synthetic.main.fragment_events_list.*
import java.util.*

class EventsList : Fragment() {

    val args: EventsListArgs by navArgs() // allows me to pull the arguments passed
    lateinit var date: String // initialises the date String variable
    private var events = mutableListOf<String>() // initialises the events List variable
    var noEvents: Boolean = false

    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle? ): View? {

        val view =  inflater.inflate(R.layout.fragment_events_list, container, false)

        date = args.dateValue // receives the date string from the datepicker fragment
        (activity as AppCompatActivity).supportActionBar?.title = "$date Schedule"
        // sets the action bar title to the date and schedule

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        BotResponse.endTimer = System.nanoTime() // finishes the end timer for showing the schedule

        Toast.makeText(activity, "Elapsed Time: ${(BotResponse.endTimer-BotResponse.startTimer)} ", Toast.LENGTH_LONG)
            .show()

        createEventsList() // this populates the list view with events from the database
        // if there are any for that date

        add_button.setOnClickListener {
            val action = EventsListDirections.actionEventsListToChooseTime(date)
            Navigation.findNavController(view).navigate(action)
            // set on click listener for the button, passes the date value to be used further
        }
    }

    private fun createEventsList() {

        val db = activity?.let { DatabaseHandler(it) } // object of database handler, connects to it
        val data = db?.readData(date) // calls the read function from the handler
        // this gets all the rows from the database associated with the date

        events.clear() // clear the events incase the user goes back to the fragment

        StoreEvents.parseEventsData(data) // calls the store events object to store the data incase
        //events needs to be deleted

        if (data != null && data.isNotEmpty()) { // if the data is not null and not empty
            for (i in 0 until data.size) { // iterates through all the data/events
                val event = ("${data[i].date}, ${(data[i].place).capitalize(Locale.ROOT)}, ${data[i].startTime} To ${data[i].finishTime} \n" +
                        data[i].description) // stores the pulled data in the event string to then be shown on the screen
                events.add(event) // adds the events to the events list
            }
        } else {
            noEvents = true
            events.add("No Events Scheduled") // adds no events scheduled to the empty list
        }

        //sets the adapter layout view to a simple list
        val arrayAdapter: ArrayAdapter<String> = ArrayAdapter( // list is populated with the events List
                requireActivity(), android.R.layout.simple_list_item_1, events)

        listView.adapter = arrayAdapter // Sets the data behind this ListView.

        // '_' is placed in the parameters that aren't used. parent, view and id.
        listView.setOnItemClickListener { _, _, position, _ ->
            Toast.makeText(activity, "Item Selected ${events[position]}", Toast.LENGTH_LONG)
                    .show()
            val value = position * 4 // all the values for events
            // when read are stored in a list, each event is in groups of 4 so if the user were to
            // choose option 0 to delete, it will be 0 in the list to 3 for that event.

            if (!noEvents) {
                val action = EventsListDirections.actionEventsListToDeleteEvent(StoreEvents.events[value],
                        StoreEvents.events[value+1],StoreEvents.events[value+2],StoreEvents.events[value+3])
                view?.let { Navigation.findNavController(it).navigate(action) }
                // passes the values obtained from the StoreEvents object to the confirm deletion fragment
            }

        }

    }

}