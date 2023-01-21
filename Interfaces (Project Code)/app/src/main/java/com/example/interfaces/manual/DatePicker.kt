package com.example.interfaces.manual

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import com.example.interfaces.R
import com.example.interfaces.objects.BotResponse
import kotlinx.android.synthetic.main.fragment_date_picker.*



class DatePicker : Fragment() {

    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle? ): View? {
        return inflater.inflate(R.layout.fragment_date_picker, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //BotResponse.startTimer = 0
        //BotResponse.endTimer = 0

        BotResponse.startTimer = System.nanoTime() // starts time when view is created to be used
        // in evaluation
                                        // '_' in place of 'view 'because view is never used
        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth -> // onclick listener for the
            // calendar so we can get the date and pass the values
            var updatedDay = "$dayOfMonth"  //get the day as string to do length checks
            var updatedMonth = "${month + 1}" //get the month + 1 as string to do length checks
            // plus one because the values are 1 behind

            if ((month+1).toString().length == 1) { // if the day is of 1 length e.g '9'/10/2021
                // makes it '09'/10/2021 for consistency and formatting in the database
                updatedMonth = "0$updatedMonth"
            }

            if (dayOfMonth.toString().length == 1) { // if the month is of 1 length e.g 10/'9'/2021
                // makes it '10/'09'/2021 for consistency and formatting in the database
                updatedDay = "0$updatedDay"
            }
            // outputs to the user what date was picked and stores the date value
            Toast.makeText(requireActivity(), "Selected Date: $updatedDay/$updatedMonth/$year", Toast.LENGTH_SHORT).show()
            val date = "$updatedDay/$updatedMonth/$year" // date values is stored to be passed to next screen
            val action = DatePickerDirections.actionDatePickerToEventsList(date)
            Navigation.findNavController(view).navigate(action)
        }
    }

}