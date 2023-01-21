package com.example.interfaces.manual

import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TimePicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.example.interfaces.R
import com.example.interfaces.objects.CheckTime
import com.example.interfaces.objects.ConvertTime
import kotlinx.android.synthetic.main.fragment_choose_time.*


class ChooseTime : Fragment(), TimePickerDialog.OnTimeSetListener {

    private val args: ChooseTimeArgs by navArgs() // allows me to pull the arguments passed

    lateinit var date: String // initialises the date String variable
    lateinit var startTime: String // initialises the startTime String variable
    lateinit var endTime: String // initialises the endTime String variable
    lateinit var combinedTime: String // initialises the combinedTime String variable

    var hour = 0  // initialises the hour Int variable
    var minute = 0 // initialises the minute Int variable

    var savedHour = 0 // initialises the savedHour Int variable
    var savedMinute = 0 // initialises the savedMinute Int variable

    var isStartTime = true // initialises the isStartTime Boolean variable
    var startTimeBoolean = false // initialises the startTimeBoolean Boolean variable
    var endTimeBoolean = false // initialises the endTimeBoolean Boolean variable


    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle? ): View? {
        val view =  inflater.inflate(R.layout.fragment_choose_time, container, false)

        date = args.dates // receives the date string from the datepicker fragment
        (activity as AppCompatActivity).supportActionBar?.title = "$date Choose Time"
        // sets the action bar title to the date and Choose Time

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        StartTime.setOnClickListener {
            isStartTime = true // sets the start time on click listener to open the time dialog picker
            TimePickerDialog(activity, this, hour, minute, true).show()
            //time boolean is true because its the start time set and not the end time
        }

        FinishTime.setOnClickListener {
            isStartTime = false // sets the start time on click listener to open the time dialog picker
            TimePickerDialog(activity, this, hour, minute, true).show()
            //time boolean is false because its the end time set and not the start time
        }

        button_activity.setOnClickListener {
            if (startTimeBoolean && endTimeBoolean) { // if both values are set for the time proceeds
                if (timeCheck(combinedTime)) { // checks if the time input is valid
                    val action = ChooseTimeDirections.actionChooseTimeToActivityType(date, startTime, endTime)
                    Navigation.findNavController(view).navigate(action)  // sends the date and time
                    // to the next activity
                } else { // if the combined time is false and invalid, it outputs to the screen
                    Toast.makeText(activity, "Invalid Input", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }

        StartDate.text = date // sets the start date to the date passed from previous fragment
        FinishDate.text = date // sets the end date to the date passed from previous fragment

        super.onViewCreated(view, savedInstanceState)
    }

    // Called when the user is done setting a new time and the dialog has closed
    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {

        if (isStartTime) { // if the start time is the one chosen
            startTimeBoolean = true // sets the boolean to true to signify start time has been set
            StartTime.hint = "$hourOfDay:$minute" // sets end time the hint to the time chosen
            startTime = "$hourOfDay:$minute" // sets the startTime value to be stored in the database to the time
        } else { // if the end time is the one chosen
            endTimeBoolean = true // sets the boolean to true to signify end time has been set
            FinishTime.hint = "$hourOfDay:$minute" // sets the start time hint to the time chosen
            endTime = "$hourOfDay:$minute" // sets the endTime value to be stored in the database to the time
            combinedTime = "$startTime to $endTime" // combines the time together to be checked later

        }
        Log.e("Choose Time", "$savedHour is the hour and $savedMinute is the minutes")
    }

    private fun timeCheck(combinedTime: String): Boolean {

        val delimiter = "to" // what is used to split the input
        val parts = combinedTime.split(delimiter, ignoreCase = true)
        // splits the XX:XX To XX:XX two parts, 'XX:XX' and 'XX:XX'
        startTime = parts[0] // initial time
        startTime = startTime.replace("\\s".toRegex(), "") // replace spaces with empty strings
        startTime =
            CheckTime.timeFormat(startTime) // formats the time properly to be stored in the database
        val startConvert = ConvertTime.formatting((startTime)) // converts the time to minutes

        endTime = parts[1] // end time
        endTime = endTime.replace("\\s".toRegex(), "") // replace spaces with empty strings
        endTime =
            CheckTime.timeFormat(endTime) // formats the time properly to be stored in the database
        val finishConvert = ConvertTime.formatting((endTime)) // converts the time to minutes

        Log.e("Choose Time Combined Time", "$startTime To $endTime TIME")

        // checks if the start time is bigger than finish time. if both of them are not in the range of 0 to 1440 minutes and if they're equal
        // if so its an incorrect input
        return !((startConvert > finishConvert) || (startConvert !in (0..1440)) || (finishConvert !in (0..1440)) || (startConvert == finishConvert))
    }


}