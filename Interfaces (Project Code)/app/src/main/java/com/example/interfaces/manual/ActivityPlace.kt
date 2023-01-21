package com.example.interfaces.manual

import android.os.Bundle
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
import kotlinx.android.synthetic.main.fragment_events_list.*

class ActivityPlace : Fragment() {

    val args: ActivityPlaceArgs by navArgs() // allows me to pull the arguments passed
    lateinit var date: String // initialises the date String variable
    lateinit var startTime: String // initialises the startTime String variable
    lateinit var endTime: String // initialises the endTime String variable
    var activityType: Int = 0 // initialises the activityType Int variable
    lateinit var places: Array<String> // initialises the places Array String variable

    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle? ): View? {

        val view =  inflater.inflate(R.layout.fragment_activity_place, container, false)

        date = args.date // receives the date string from the ActivityType fragment
        startTime = args.startTime // receives the startTime string from the ActivityType fragment
        endTime = args.endTime // receives the endTime string from the ActivityType fragment
        activityType = args.typeOfActivity // receives the activityType Int from the ActivityType fragment
        // which was the position in the list

        (activity as AppCompatActivity).supportActionBar?.title = "$date Choose Which Place"
        // sets the action bar title to the date and Choose Which Place

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        when (activityType) { // checks what the value of activity type is
            0 -> { // if the position value is 0 it is the Restaurant
                places = arrayOf("Restaurant 1", "Restaurant 2", "Restaurant 3")
            } /// it then sets the activities array list to the different places to be set
            1 -> { // if the position value is 1 it is the Cinema
                places = arrayOf("Cinema 1", "Cinema 2", "Cinema 3")
            } /// same
            2 -> { // if the position value is 2 it is the Supermarket
                places = arrayOf("Supermarket 1", "Supermarket 2", "Supermarket 3")
            } /// same
            3 -> { // if the position value is 3 it is the hospital
                places = arrayOf("Hospital 1", "Hospital 2", "Hospital 3")
            } /// same

        }

        //sets the adapter layout view to a simple list
        val arrayAdapter: ArrayAdapter<String> = ArrayAdapter( // list is populated with the places array
            requireActivity(), android.R.layout.simple_list_item_1, places)

        listView.adapter = arrayAdapter // Sets the data behind this ListView.

        // '_' is placed in the parameters that aren't used. parent, view and id.
        listView.setOnItemClickListener { _, _, position, _ ->
            Toast.makeText(activity, "Item Selected ${places[position]}", Toast.LENGTH_LONG)
                .show()
            val action = ActivityPlaceDirections.actionActivityPlaceToConfirmActivity(date, startTime, endTime,places[position])
            Navigation.findNavController(view).navigate(action)
        }
    }

}