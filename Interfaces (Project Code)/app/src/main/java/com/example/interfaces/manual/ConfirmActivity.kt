package com.example.interfaces.manual

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.example.interfaces.R
import com.example.interfaces.database.DatabaseHandler
import com.example.interfaces.database.Reminders
import com.example.interfaces.objects.BotResponse
import kotlinx.android.synthetic.main.fragment_confirm_activity.*


class ConfirmActivity : Fragment() {

    private val args: ConfirmActivityArgs by navArgs() // allows me to pull the arguments passed
    lateinit var date: String // initialises the date String variable
    lateinit var startTime: String // initialises the startTime String variable
    lateinit var endTime: String // initialises the endTime String variable
    lateinit var place: String // initialises the place String variable


    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle? ): View? {

        val view =  inflater.inflate(R.layout.fragment_confirm_activity, container, false)

        date = args.date // receives the date string from the ActivityPlace fragment
        startTime = args.startTime // receives the startTime string from the ActivityPlace fragment
        endTime = args.endTime // receives the endTime string from the ActivityPlace fragment
        place = args.place // receives the place string from the ActivityPlace fragment

        (activity as AppCompatActivity).supportActionBar?.title = "$date Confirm Event"
        // sets the action bar title to the date and Confirm Event

        Log.e("Confirm Activity", "$date, $startTime, $endTime, $place")

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        startTimeText.text = startTime // sets the start Time Text to the start time value passed.
        endTimeText.text = endTime // sets the end Time Text to the end time value passed.
        dateText.text = date // sets the date Text to the date value passed.
        dateText2.text = date // sets the date Text to the date value passed.
        place_text.text = place // sets the place Text to the place value passed.

        yes_button.setOnClickListener {
            BotResponse.endTimer = System.nanoTime() // finishes the end timer for showing the schedule
            Toast.makeText(activity, "Elapsed Time: ${(BotResponse.endTimer-BotResponse.startTimer)} ", Toast.LENGTH_LONG)
                .show()

            insertIntoDatabase() // if the user has correctly submitted an event
            // it calls the insert into database function

            // once the event has been inserted, it goes back to the date picker fragment
            val action = ConfirmActivityDirections.actionConfirmActivityToDatePicker()
            Navigation.findNavController(view).navigate(action)

        }

        no_button.setOnClickListener {

            // once cancelled, it goes back to the date picker fragment
            val action = ConfirmActivityDirections.actionConfirmActivityToDatePicker()
            Navigation.findNavController(view).navigate(action)
            Toast.makeText(activity, "Event Schedule Cancelled", Toast.LENGTH_LONG)
                .show()
        }
    }

    private fun insertIntoDatabase() {
        BotResponse.submitSchedule = false // sets submit schedule to false as data has been inserted into database
        val reminder = Reminders(date,place, "Basic Description",
            startTime, endTime) //passes the values to be stored
        context?.let { DatabaseHandler(it) }?.insertData(reminder) //gets the context and calls the insert
        //data function in the database to insert the data
    }

}