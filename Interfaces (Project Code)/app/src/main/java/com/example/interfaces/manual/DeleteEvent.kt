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
import com.example.interfaces.objects.BotResponse
import kotlinx.android.synthetic.main.fragment_confirm_activity.dateText
import kotlinx.android.synthetic.main.fragment_confirm_activity.dateText2
import kotlinx.android.synthetic.main.fragment_confirm_activity.endTimeText
import kotlinx.android.synthetic.main.fragment_confirm_activity.no_button
import kotlinx.android.synthetic.main.fragment_confirm_activity.startTimeText
import kotlinx.android.synthetic.main.fragment_confirm_activity.yes_button
import kotlinx.android.synthetic.main.fragment_delete_event.*

class DeleteEvent : Fragment() {

    private val args: DeleteEventArgs by navArgs()
    lateinit var date: String // initialises the date String variable
    lateinit var startTime: String // initialises the startTime String variable
    lateinit var endTime: String // initialises the endTime String variable
    lateinit var place: String // initialises the place String variable


    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle? ): View? {

        val view =  inflater.inflate(R.layout.fragment_delete_event, container, false)

        date = args.date // receives the date string from the ActivityPlace fragment
        startTime = args.startTime // receives the startTime string from the ActivityPlace fragment
        endTime = args.endTime // receives the endTime string from the ActivityPlace fragment
        place = args.place // receives the place string from the ActivityPlace fragment

        (activity as AppCompatActivity).supportActionBar?.title = "$date Confirm Event Deletion"
        // sets the action bar title to the date and Confirm Event Deletion

        Log.e("Delete Activity", "$date, $startTime, $endTime, $place")

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        startTimeText.text = startTime // sets the start Time Text to the start time value passed.
        endTimeText.text = endTime // sets the end Time Text to the end time value passed.
        event_text.text = place // sets the event Text to the place value passed.
        dateText.text = date // sets the date Text to the date value passed.
        dateText2.text = date // sets the date Text to the date value passed.

        yes_button.setOnClickListener {

            BotResponse.endTimer = System.nanoTime() // finishes the end timer for showing the schedule
            Toast.makeText(activity, "Elapsed Time: ${(BotResponse.endTimer-BotResponse.startTimer)} ", Toast.LENGTH_LONG)
                .show()

            deleteEventInDatabase()
            Toast.makeText(activity, "Event Deleted", Toast.LENGTH_LONG)
                .show()

            // once the event has been deleted, it goes back to the date picker fragment
            val action = DeleteEventDirections.actionDeleteEventToDatePicker()
            Navigation.findNavController(view).navigate(action)

        }

        no_button.setOnClickListener {

            Toast.makeText(activity, "Deletion Cancelled", Toast.LENGTH_LONG)
                .show()

            // once cancelled, it goes back to the date picker fragment
            val action = DeleteEventDirections.actionDeleteEventToDatePicker()
            Navigation.findNavController(view).navigate(action)

        }
    }

    private fun deleteEventInDatabase() {
        BotResponse.submitCancel = false // sets it back to false so it doesn't trigger unnecessary deletes

        val db = activity?.let { DatabaseHandler(it) } // object of database handler, connects to it
        db?.deleteData(date, place, startTime, endTime)
        // calls the delete data function with the values passed acquired from the bot response object
    }
}