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
import kotlinx.android.synthetic.main.fragment_events_list.*


class ActivityType : Fragment() {

    private val args: ActivityTypeArgs by navArgs() // allows me to pull the arguments passed
    lateinit var date: String // initialises the date String variable
    lateinit var startTime: String // initialises the startTime String variable
    lateinit var endTime: String // initialises the endTime String variable

    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle? ): View? {

        val view =  inflater.inflate(R.layout.fragment_activity_type, container, false)

        date = args.date // receives the date string from the ChooseTime fragment
        startTime = args.startTime // receives the startTime string from the ChooseTime fragment
        endTime = args.EndTime // receives the endTime string from the ChooseTime fragment

        (activity as AppCompatActivity).supportActionBar?.title = "$date Choose An Activity"
        // sets the action bar title to the date and Choose Activity

        Log.e("Activity Type", "$date DATE, $startTime START TIME, $endTime END TIME")

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // creates an array of all the possible activity types
        val activities = arrayOf("Restaurant", "Cinema", "Supermarket", "Hospital")

        //sets the adapter layout view to a simple list
        val arrayAdapter: ArrayAdapter<String> = ArrayAdapter( // list is populated with the activities array
            requireActivity(), android.R.layout.simple_list_item_1, activities)

        listView.adapter = arrayAdapter // Sets the data behind this ListView.

        // '_' is placed in the parameters that aren't used. parent, view and id.
        listView.setOnItemClickListener { _, _, position, _ ->
            Toast.makeText(activity, "Item Selected ${activities[position]}", Toast.LENGTH_LONG)
                .show()
            val action = ActivityTypeDirections.actionActivityTypeToActivityPlace(date, startTime, endTime, position)
            Navigation.findNavController(view).navigate(action) // sends the date and time
            // to the next activity. // position will be used as the type of activity to look for, position corresponds
        }
    }
}