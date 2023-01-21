package com.example.interfaces.objects

import android.util.Log
import android.widget.Toast
import com.example.interfaces.objects.CheckTime.timeFormat
import java.util.*

object BotResponse {
    var responseType = 0

    //Booleans
    var initialStage: Boolean = true // Check if its the initial stage
    var scheduleStage: Boolean = false // Check if its the schedule stage
    var showStage: Boolean = false // Check if its the show schedule stage
    var cancelStage: Boolean = false // Check if its the cancel event stage

    var homeIntro: Boolean = false // check if the intro paragraph should be executed
    var submitSchedule: Boolean = false // check if an event has been scheduled
    var submitShow: Boolean = false // check if the user has requested to see their schedule
    var submitCancel: Boolean = false // check if the user has requested to cancel their schedule
    var validInput: Boolean = true // if the input is valid check
    var emptyEvents: Boolean = false // check if the date has no schedule
    var emptyExtraCheck: Boolean = false // check if the date has no schedule extra Check
    var showTimeTaken: Boolean = false // check if the user has finished a task and should show time

    lateinit var storeDate: String // stores the date chosen
    lateinit var storeTime: String  // stores time in singular string
    lateinit var storePlaceName:String // stores the place name

    lateinit var dayString: String // stores day of the date
    lateinit var monthString: String // stores month of the date
    lateinit var yearString: String // stores year of the date

    var startTimer: Long = 0 // tracking how long it takes to do a task start
    var endTimer: Long = 0 // tracking how long it takes to do a task finish

    lateinit var startTime: String // stores the start time
    lateinit var finishTime: String // stores the finish time

    // Voice Assistant Exclusive
    var initialStart: Boolean = true // check if initial start for the voice assistant, converting Time
    private var tempStoreTimeStart: Int = 0 // stores the start time
    private var tempStoreTimeFinish: Int = 0 // stores the finish time


    fun homeIntroParagraph(): String {
        return "What would you like to do?" + "\n1) Schedule An Event\n2) Show My Schedule\n3) Cancel An Event"
        // returns the home intro
    }

    //splits the date and checks
    private fun splitDateFunctionChatbot(message: String): Boolean {

        val delimiter = "/" //what to split the message by
        val parts = message.split(delimiter, ignoreCase = true) // splits the message by '/', thus 3 parts.
        // parts[0] is the day parts[1] is the month and parts[2] is the year
        return try { // try catch clause to catch invalid inputs
            if (parts[0].length !in (1..2) // checks if the day length is in 1 to 2
                || parts[1].length !in (1..2) || parts[2].length != 4) { // same for month and year
                    validInput = false // input is invalid
                    return validInput // returns the boolean
            } else { // if the correct lengths are provided, else clause is met
                dayString = parts[0]
                dayString.toInt()    // these parts are assigning the variables to the
                monthString = parts[1]  // split up parts and checking if they can be integers
                monthString.toInt()  // if they cannot be cast to int, try clause fails and error
                yearString = parts[2] // as the user has put an invalid input
                yearString.toInt()

                if (dayString.toInt() !in (1..31)) { // checks if day number is not within range of 1 to 31
                    validInput = false // input is invalid
                    return validInput // returns the boolean
                } else { // if the number is in range
                    if (dayString.length == 1) { // checks if the length is 1
                        dayString = "0$dayString" // this is to make the values stored in the database
                    }                            // is consistent
                }

                if (monthString.toInt() !in (1..12)) { // checks if month number is not within range of 1 to 12
                    validInput = false // input is invalid
                    return validInput // returns the boolean
                } else { // if the number is in range
                    if (monthString.length == 1) { // checks if the length is 1
                        monthString = "0$monthString" // this is to make the values stored in the database
                    }                                  // is consistent
                }

                storeDate = "$dayString/$monthString/$yearString" // what will be stored in the database

                validInput = true // sets valid input to true
                validInput // returns the boolean check

            }

        } catch (e: Exception) { // if the try cause fails it makes the input valid and returns
            validInput = false // input is invalid
            validInput // returns the boolean check

        }
    }

    private fun splitDateFunctionAssistant(message: String): Boolean {

        try { // tries to execute the following code, if it fails raises exception
            // the input must follow the format
            val delimiter1 = "of" // value in which the message is split by
            val parts = message.split(delimiter1, ignoreCase = true) // splits it into two parts
            //e.g. 24th of march 2021 is split into 24th and march 2021
            var daySuffix = parts[0] // gets the first part

            for (i in Constants.ordinalSuffix) { // loops the ordinalSuffix function in constants object,
                // pulling each ordinalSuffix to use
                if (daySuffix.contains(i)) { // if the day has the suffix appended proceed
                    daySuffix = daySuffix.replace(i, "") // replaces the suffix with empty strings
                    daySuffix = daySuffix.replace("\\s".toRegex(), "") // replaces the
                    //spaces with blank spaces
                    daySuffix.toInt() // attempt to convert the day to int, raises exception if failed
                }
            }
            storeDate = daySuffix // building the store date variable, starting with the day

            var monthYear = parts[1] // this part is the month and year part of the e.g. 24th of 'March 2021'

            for (i in Constants.Months) { // loops the months function in constants object, pulling each month to use
                if (monthYear.contains(i)) { // if the day has the suffix appended proceed
                    storeDate += "/${Constants.Months.indexOf(i)}" // adds to the day and appends the month to the store date
                    monthYear = monthYear.replace(i, "") // replaces the month with empty string
                    monthYear = monthYear.replace("\\s".toRegex(), "") // replaces the
                    //spaces with blank spaces
                    monthYear.toInt() // attempt to convert the year to int, raises exception if failed
                    storeDate += "/$monthYear" // adds the day and month, appends the year to the store date
                }
            }

            val check = splitDateFunctionChatbot(storeDate) // checks the date to make sure its correct
            // and there's no invalid input like 100/03/2021

            return if (check) { // if the check is fine and true, the input is valid
                validInput = true
                validInput // returns the boolean
            } else { // if the check is invalid and false, the input is false
                validInput = false
                validInput // if the check is fine and true, the input is valid
            }

        } catch (e: Exception) { // if the input is wrong at any point and fails to work, raise exception
            validInput = false
            return validInput // returns false for the valid input
        }

    }

    private fun timeFormatFunction(timeToFormat: String): Boolean {
        var morning = false // check if the message has am or pm
        try { // tries to execute the following code, if it fails raises exception
            var timeToCheck = timeToFormat
            for (i in Constants.suffixAmPm) { // loops the suffixAmPm function in constants object,
                // pulling each suffix to use
                if (timeToCheck.contains(i)) { // checks if the message has the suffix is in the message
                    morning = i == "a.m." // if i is a.m. then morning is true
                    timeToCheck = timeToCheck.replace(i, "") // replaces the suffix with blank space
                    timeToCheck = timeToCheck.replace("\\s".toRegex(), "")
                    //replace the spaces with blank space
                }
            }

            when (timeToCheck.count()) { //counts to see how many characters the input has
                1,2 ->  { // this checks inputs like '10'pm '1'am or '1'pm and converts to 01 and such or 13:00 or 22
                    timeToCheck = if (morning) { // if its morning, formats with the morning values 1am turns to 01:00
                        timeToCheck.replace(timeToCheck, Constants.timeFormatAMNoColon[timeToCheck.toInt()])
                    } else { // if its not morning, formats with the afternoon values 1pm turns to 13:00
                        timeToCheck.replace(timeToCheck, Constants.timeFormatPMNoColon[timeToCheck.toInt()])
                    } // this is all for consistency.
                }
                4, 5 ->  {  // this checks inputs like '1:30'pm '10:30'pm converts to '13:30' and such or 13:00 or 22:30
                    val delimiter1 = ":" //splits the values by :
                    val parts = timeToCheck.split(delimiter1, ignoreCase = true) // split to equal parts
                    // e.g. 10:30 is split to '10'  and '30'
                    var partOne = parts[0] // stores the first part

                    partOne = if (morning) { // if its morning, formats with the morning values 1:30am turns to 01:30
                        partOne.replace(partOne, Constants.timeFormatAMWithColon[partOne.toInt()])
                    } else { // if its not morning, formats with the afternoon values 1:30pm turns to 13:30
                        partOne.replace(partOne, Constants.timeFormatPMWithColon[partOne.toInt()])
                    } // this is all for consistency.
                    timeToCheck = partOne + ":" + parts[1]
                }
            }

            if (initialStart) { // since this method is called twice, need to check
                // if its the start time or the finish time. e.g. start time or finish time
                tempStoreTimeStart  = ConvertTime.formatting((timeToCheck)) //converts the time to minutes
                startTime = timeToCheck // sets the start to the time time e.g. 10:30
                initialStart = false // no longer the start time to so false
            } else {
                tempStoreTimeFinish = ConvertTime.formatting((timeToCheck)) //converts the time to minutes
                finishTime = timeToCheck // sets the start to the time time e.g. 11:30
                initialStart = true // sets it to false so when its called again (in pairs) start time is first
            }

            validInput = true
            return validInput // returns valid input which is set to true
        } catch (e: Exception) { // if the input is wrong at any point and fails to work, raise exception
            validInput = false
            return validInput // returns valid input as false
        }
    }

    // chatbot section
    fun chatbotConversationFlow(_message: String): String {
        var message =_message.toLowerCase(Locale.ROOT) // sets the users response to lowercase

        if (initialStage) { // checks if its the initial stage
            homeIntro = false // sets the home intro to stop the intro paragraph
            showTimeTaken = false
            return when (message) { // return the string when the message is equal to the options
                "schedule an event", "1", "schedule event" -> { //if the user types any of these then:
                    startTimer = System.nanoTime() // sets the start time to system nano time
                    initialStage = false // initial stage is false to stop loops and checks
                    scheduleStage = true // schedule stage is true to move the user on
                    "Input The Day You Want To Schedule On (XX/XX/XXXX)\n" +
                            "e.g. 24/03/2021" // returns the next instructions , bot responds
                }
                "show my schedule", "2", "show schedule" -> { //if the user types any of these then:
                    startTimer = System.nanoTime() // sets the start time to system nano time
                    initialStage = false // initial stage is false to stop loops and checks
                    showStage = true // show stage is true to move the user on
                    return "Input The Day You Want To See The Schedule For (XX/XX/XXXX)\n" +
                            "e.g. 24/03/2021" // returns the next instructions, bot responds
                }
                "cancel an event", "3", "cancel event" -> { //if the user types any of these then:
                    startTimer = System.nanoTime() // sets the start time to system nano time
                    initialStage = false // initial stage is false to stop loops and checks
                    cancelStage = true // cancel stage is true to move the user on
                    return "Input The Day You Want To Cancel An Event On (XX/XX/XXXX)\n" +
                            "e.g. 24/03/2021" // returns the next instructions , bot responds
                }
                else -> { // if none of the options are met, tells the user what to do.
                    homeIntro = true // send the into paragraph again.
                    "Please Enter One Of Options Mentioned"
                }
            }
        }

        if (scheduleStage) { // checks if its the schedule stage, to being the process

            if (responseType == 0 ) { // the response type corresponds to each stage in the
                //                        scheduling stage. As they progress it changes
                return when (message) { // return the string when the message is equal to the options
                    "help" -> {
                        "Enter 'XX/XX/XXXX' e.g. 24/03/2021\n" + // returns the help message
                                "or Type 'Home' To Go Back"
                    }
                    "home" -> {
                        scheduleStage = false // no longer scheduling an event thus false
                        initialStage = true // back to the start so initial stage is true
                        responseType = 0 // resets the response type to zero
                        "What would you like to do?" + "\n1) Schedule An Event\n2) Show My Schedule\n3) Cancel An Event"
                        // returns the intro paragraph
                    }
                    else -> { // if none of the options are chosen, checks the input for the date
                        val check = splitDateFunctionChatbot(message) // calls the split date function
                        if (check) { // if the input is correct, allows the user to proceed further
                            responseType = 1 // change back to 1
                            "The Day Chosen Is $storeDate\n" + // returns the next step of instructions
                                    "Enter The Time To Schedule\n"+
                                    "XX:XX To XX:XX\n"+
                                    "e.g. 10:00 To 13:00"
                        } else { // if the input is false, response type is set to current stage
                            responseType = 0 // and returns the help message to the user.
                            "Invalid Input, Enter 'XX/XX/XXXX' \n" +
                                    "e.g. 24/03/2021" +
                                    "or Type 'Home' To Go Back"
                        }
                    }
                }
            }

            if (responseType == 1 ) { // this stage is getting the time to schedule
                return when (message) { // return the string when the message is equal to the options
                    "help" -> {
                        "Enter 'XX:XX To XX:XX' e.g. 10:00 To 13:00\n" + // returns the help message
                                "or Type 'Home' To Go Back"
                    }
                    "home" -> {
                        scheduleStage = false // no longer scheduling an event thus false
                        initialStage = true // back to the start so initial stage is true
                        responseType = 0 // resets the response type to zero
                        "What would you like to do?\n" +
                                "1) Schedule An Event\n" + // returns the intro paragraph
                                "2) Show My Schedule\n" +
                                "3) Cancel An Event"
                    }
                    else -> {  // if none of the options are chosen, checks the input for the time
                        try {
                            storeTime = message //stores the time message to use when checking
                            // if the user entered the correct input
                            val delimiter = "to" // what is used to split the input
                            val parts = message.split(delimiter, ignoreCase = true)
                            // splits the XX:XX To XX:XX two parts, 'XX:XX' and 'XX:XX'
                            startTime = parts[0] // initial time
                            startTime = startTime.replace("\\s".toRegex(), "") // replace spaces with empty strings
                            startTime = timeFormat(startTime) // formats the time properly to be stored in the database
                            val startConvert = ConvertTime.formatting((startTime)) // converts the time to minutes

                            finishTime = parts[1] // end time
                            finishTime = finishTime.replace("\\s".toRegex(), "") // replace spaces with empty strings
                            finishTime = timeFormat(finishTime) // formats the time properly to be stored in the database
                            val finishConvert = ConvertTime.formatting((finishTime)) // converts the time to minutes


                            // checks if the start time is bigger than finish time. if both of
                            // them are not in the range of 0 to 1440 minutes and if they're equal
                            // if so its an incorrect input
                            if ((startConvert > finishConvert) || (startConvert !in (0..1440)) || (finishConvert !in (0..1440))
                                    || (startConvert == finishConvert)) {
                                responseType = 1
                                return  "Invalid Input, Make Sure Initial Time Is \n" +
                                        "Greater Than Final Time and within \n" +
                                        "24 Hours of The Day"
                            }

                            responseType = 2  // sets response type to 2 to advance the stages
                            "$startTime To $finishTime Chosen\n" +
                                    "Choose an Activity Type\n" + // responds back with the time chosen
                                    "1) Restaurant\n" + // and the next stage instructions
                                    "2) Cinema\n" +
                                    "3) Supermarket\n" +
                                    "4) Hospital"
                        } catch (e: Exception) { // if there's an exception tells the user what to do.
                            responseType = 1
                            "Enter 'XX:XX To XX:XX' e.g. 10:00 To 13:00\n" +
                                    "or Type 'Home' To Go Back"
                        }
                    }
                }
            }

            if (responseType == 2 ) {
                return when (message) { // return the string when the message is equal to the options
                    "help" -> {
                        "Choose an Activity. Type:\n" +
                                "1) Restaurant\n" +
                                "2) Cinema\n" +       // returns the help message
                                "3) Supermarket\n" +
                                "4) Hospital\n" +
                                "or Type 'Home' To Go Back"
                    }
                    "home" -> {
                        scheduleStage = false // no longer scheduling an event thus false
                        initialStage = true // back to the start so initial stage is true
                        responseType = 0 // resets the response type to zero
                        "What would you like to do?" + "\n1) Schedule An Event\n2) Show My Schedule\n3) Cancel An Event"
                        // returns the intro paragraph

                    }
                    "restaurant", "1" -> { // if the user chooses restaurant or 1, return the next stage steps
                        responseType = 3 // specifies which activity is called in the next section
                        "Select a restaurant\n" +
                                "1) Restaurant 1\n" +
                                "2) Restaurant 2\n" +
                                "3) Restaurant 3"
                    }
                    "cinema", "2" -> { // if the user chooses cinema or 2, return the next stage steps
                        responseType = 4 // specifies which activity is called in the next section
                        "Select a cinema\n" +
                                "1) Cinema 1\n" +
                                "2) Cinema 2\n" +
                                "3) Cinema 3"
                    }
                    "supermarket", "3" -> { // if the user chooses supermarket or 3, return the next stage steps
                        responseType = 5 // specifies which activity is called in the next section
                        "Select a supermarket\n" +
                                "1) Supermarket 1\n" +
                                "2) Supermarket 2\n" +
                                "3) Supermarket 3"
                    }
                    "hospital", "4" -> { // if the user chooses hospital or 4, return the next stage steps
                        responseType = 6 // specifies which activity is called in the next section
                        "Select a supermarket\n" +
                                "1) Hospital 1\n" +
                                "2) Hospital 2\n" +
                                "3) Hospital 3"
                    }
                    else -> { // if none of the options are met, error message is sent out
                        responseType = 2
                        "Enter One Of Options"
                    }
                }

            }

            if (responseType in 3..6 ) { // checks the response type is within a range

                when (responseType) { // check the response type values
                    3 -> when (message) { // check the message values
                        "1", "2", "3" -> message = "restaurant $message"
                    } // if the user chose a numerical value to select, its converted to the place name
                    4 -> when (message) {
                        "1", "2", "3" -> message = "cinema $message"
                    } // ^same
                    5 -> when (message) {
                        "1", "2", "3" -> message = "supermarket $message"
                    } // ^same
                    6 -> when (message) {
                        "1", "2", "3" -> message = "hospital $message"
                    } // e.g if they chose 1 for hospital activity, it will save the response as Hospital 1
                }
                if (message == "help") { // if the user types in help
                    return "Enter The Place Name Or Respective Number." // returns help message
                }
                if (message == "home") {
                        scheduleStage = false // no longer scheduling an event thus false
                        initialStage = true // back to the start so initial stage is true
                        responseType = 0 // resets the response type to zero
                        return "What would you like to do?" + "\n1) Schedule An Event\n2) Show My Schedule\n3) Cancel An Event"
                        // returns the intro paragraph

                } else {
                    return if (Constants.Places.contains(message.toLowerCase(Locale.ROOT))) { // checks if the
                        storePlaceName = message //  event place is in the acceptable options and stores it so for the database
                        responseType = 7 // sets the response type to 7 to advance forward
                        "Is This Activity Correct? \n" + // returns to the user to confirm the activity in the next step
                                "$storeDate, $storeTime at ${storePlaceName.capitalize(Locale.ROOT)}\n" + // return the event to scheduled
                                "1) Yes\n" +
                                "2) No"
                    } else {
                        "Enter The Place Name Or Respective Number." // returns error message for incorrect output
                    }
                }
            }

            if (responseType == 7 ) {
                return when (message) { // check the response type values

                    "help" -> { // returns to the user what to type to advance
                        "Enter Yes (1) to confirm or No/2/Home to go back to the menu."
                    }
                    "home", "no", "2" -> { // if any of these options are chosen no event is scheduled
                        scheduleStage = false // schedule stage is false and is returns to initial stage
                        initialStage = true
                        "What would you like to do?" + "\n1) Schedule An Event\n2) Show My Schedule\n3) Cancel An Event"
                        //returns the intro paragraph
                    }
                    "yes", "1" -> { // if yes or 1, schedules the event
                        endTimer = System.nanoTime() // end the timer. end time minus start time to get how long it
                        showTimeTaken = true // set to true the time so the time elapsed is shown on screen
                        // took for the user to do the task. to be used in the evaluation
                        println("Elapsed Time in nanoseconds : ${endTimer- startTimer}")
                        scheduleStage = false // schedule stage is false since  event has been scheduled
                        initialStage = true // initial stage to go back to menu
                        responseType = 0 // resets the response type
                        homeIntro = true // signifies the chatbot to type the intro paragraph
                        submitSchedule = true //signifies the event has been scheduled to insert into database
                        "Your Event Has Been Scheduled!" // returns event has been scheduled
                    }
                    else -> {
                        responseType = 7 // loops here till an option is given
                        "Enter One Of Options"
                    }
                }
            }
        }

        if (showStage) { // this is for displaying the schedule on a date

            if (responseType == 0 ) {
                return when (message) {
                    "help" -> { //returns to the user instructions on how to answer the question
                        "Enter 'XX/XX/XXXX' e.g. 24/03/2021\n" +
                                "or Type 'Home' To Go Back"
                    }
                    "home" -> {
                        showStage = false // no longer showing the events thus false
                        initialStage = true // back to the start so initial stage is true
                        responseType = 0 // resets the response type to zero

                        "What would you like to do?\n" +
                                "1) Schedule An Event\n" + // returns the intro paragraph
                                "2) Show My Schedule\n" +
                                "3) Cancel An Event"
                    }
                    else -> {
                        val check = splitDateFunctionChatbot(message)  // calls the split date function to check the date
                        if (check) {  // if the input is correct, allows the user to proceed further
                            endTimer = System.nanoTime() // end the timer. end time minus start time to get how long it
                            showTimeTaken = true // set to true the time so the time elapsed is shown on screen
                            showStage = false // no longer showing the events thus false
                            initialStage = true // initial stage to go back to menu
                            responseType = 0 // resets the response type
                            homeIntro = true // signifies to do the intro paragraph
                            submitShow = true // signifies to read the database for the date. returns all the events
                            "Your Schedule for $message is..." // returns to the user the string

                        } else {
                            responseType = 0 // sets response to zero and tells the user what to do
                            // error handling
                            "Enter 'XX/XX/XXXX' e.g. 24/03/2021\n" +
                                    "or Type 'Home' To Go Back"
                        }
                    }
                }
            }
        }

        if (cancelStage) { // this is for cancelling an event on a given date

            if (responseType == 0 ) {
                return when (message) {
                    "help" -> { //returns to the user instructions on how to answer the question
                        "Enter 'XX/XX/XXXX' e.g. 24/03/2021\n" +
                                "or Type 'Home' To Go Back"
                    }
                    "home" -> {
                        cancelStage = false // no longer cancelling an event thus false
                        initialStage = true // back to the start so initial stage is true
                        responseType = 0 // resets the response type to zero

                        "What would you like to do?" + "\n1) Schedule An Event\n2) Show My Schedule\n3) Cancel An Event"
                        // returns the intro paragraph
                    }
                    else -> {  // if none of the previous options were met, moves to the else
                        val check = splitDateFunctionChatbot(message) // calls the split date function to check the date
                        if (check) { // if the input is correct, allows the user to proceed further
                            responseType = 1 // increments the response type to move onto next stage
                            submitShow = true // signifies to read the database for the date. returns all the events
                            emptyExtraCheck = true // stops the schedule being shown
                            if (emptyEvents) {
                                cancelStage = false // no longer cancelling an event thus false
                                initialStage = true // back to the start so initial stage is true
                                responseType = 0 // resets the response type to zero
                                homeIntro = true
                                return "No Events Scheduled"
                            } else {
                                submitShow = true // signifies to read the database for the date. returns all the events
                                emptyExtraCheck = false // allows the schedule to be shown
                                "Your Schedule On $message is... \n" + // returns to the user the string for the
                                        "Which event would you like to cancel?" // next part instructions
                            }


                        } else {
                            responseType = 0 //returns the instructions to the user to enter
                            "Enter 'XX/XX/XXXX' e.g. 24/03/2021\n" +
                                    "or Type 'Home' To Go Back"
                        }
                    }
                }
            }

            if (responseType == 1) {
                if (message == "home") {
                    cancelStage = false // no longer cancelling an event thus false
                    initialStage = true // back to the start so initial stage is true
                    responseType = 0 // resets the response type to zero
                    return "What would you like to do?" + "\n1) Schedule An Event\n2) Show My Schedule\n3) Cancel An Event"
                    // returns the intro paragraph
                } else {
                    return try { // tries to return a value
                        val value = (message.toInt() - 1) * 4 // all the values for events
                        // when read are stored in a list, each event is in groups of 4
                        // so if the user was to choose option 1 to delete, it will be
                        // 0 in the list to 3 for that event.
                        Log.e("ChatBot",  " $value VALUE HEREEE")

                        storeDate = StoreEvents.events[value] // gets the value for the date of the event
                        storePlaceName = StoreEvents.events[value+1] // gets the value for the place of the event
                        startTime = StoreEvents.events[value+2] // gets the value for the start time of the event
                        finishTime = StoreEvents.events[value+3] // gets the value for the finish time of the event
                        storeTime = "$startTime To $finishTime" // joins the time to be more easier to read

                        val event = "$storeDate, $storeTime at ${storePlaceName.capitalize(Locale.ROOT)}\n"
                        // combines the event values to show the user
                        responseType = 2 // advances the user to next stage of options
                        "Is this the Correct Event? \n" + // ask the user if the event is the correct one
                                "'$event'\n" +
                                "1) Yes\n" +
                                "2) No"

                    }  catch (e: Exception) { // if there's an exception tells the user what to do.
                        responseType = 1
                        "Invalid Input, Enter The Number of The Place"
                    }
                }
            }

            if (responseType == 2 ) {

                return when (message) {
                    "help" -> { //returns to the user instructions on how to answer the question
                        "Enter 'Yes'/'1' to Cancel Then Event\n " +
                                "Or 'No'/'2'/'Home' to go back to the menu."
                    }
                    "yes", "1" -> {  // if the user response is either yes or 1, it
                        // tells them the event has been cancelled
                        endTimer = System.nanoTime() // end the timer. end time minus start time to get how long it
                        // took for the user to do the task. to be used in the evaluation
                        showTimeTaken = true // set to true the time so the time elapsed is shown on screen
                        responseType = 0
                        cancelStage = false
                        initialStage = true
                        homeIntro = true
                        submitCancel = true
                        "Your Event Has Been Cancelled!"
                    }
                    "home", "no", "2" -> { // if the user response is either no or 2, it
                        // goes back to the start
                        cancelStage = false
                        initialStage = true
                        "What would you like to do?\n" +
                                "1) Schedule An Event\n" +
                                "2) Show My Schedule\n" +
                                "3) Cancel An Event"
                    }
                    else -> { // if no option is chosen tells the user to enter the correct
                        // option
                        responseType = 2
                        "Enter 'Yes'/'1' to Cancel Then Event\n " +
                                "Or 'No'/'2'/'Home' to go back to the menu."
                    }
                }
            }
        }
        return "Error, Out Of Bounds" // if there's an error overall, returns out of bounds.
    }

    fun assistantConversationFlow(_message: String): String {
        var message =_message.toLowerCase(Locale.ROOT) // sets the users response to lowercase

        if (initialStage) { // checks if its the initial stage
            homeIntro = false
            showTimeTaken = false
            return when (message) { // return the string when the message is equal to the options

                "schedule an event", "1", "one" -> { //if the user says any of these then:
                    startTimer = System.nanoTime() // sets the start time to system nano time
                    initialStage = false // initial stage is false to stop loops and checks
                    scheduleStage = true // schedule stage is true to move the user on
                    "Say What Day You Want To Schedule On\n" +
                            "e.g. '24th Of March 2021'" // returns the next instructions , bot responds
                }
                "show my schedule", "2", "two" -> { //if the user says any of these then:
                    startTimer = System.nanoTime() // sets the start time to system nano time
                    initialStage = false // initial stage is false to stop loops and checks
                    showStage = true // show stage is true to move the user on
                    "Say What Day You Want To See Your Schedule\n" +
                            "e.g. '24th Of March 2021'" // returns the next instructions, bot responds
                }
                "cancel an event", "3", "three" -> { //if the user says any of these then:
                    startTimer = System.nanoTime() // sets the start time to system nano time
                    initialStage = false // initial stage is false to stop loops and checks
                    cancelStage = true // cancel stage is true to move the user on
                    "Say What Day You Want To Cancel An Event On\n" +
                            "e.g. '24th Of March 2021'" // returns the next instructions , bot responds
                }
                else -> { // if none of the options are met, tells the user what to do.
                    "Speak One Of The Options Mentioned\n"+
                            "What would you like to do?" +
                            "\n1) Schedule An Event\n2) Show My Schedule\n3) Cancel An Event"
                }
            }
        }

        if (scheduleStage) { // checks if its the schedule stage, to being the process

            if (responseType == 0) {// the response type corresponds to each stage in the
                //                        scheduling stage. As they progress it changes
                return when (message) { // return the string when the message is equal to the options
                    "help" -> {
                        "Say What Day You Want To Schedule On\n" + // returns the help message
                                "e.g. '24th Of March 2021'"
                    }
                    "home" -> {
                        scheduleStage = false // no longer scheduling an event thus false
                        initialStage = true // back to the start so initial stage is true
                        responseType = 0 // resets the response type to zero
                        "What would you like to do?\n" +
                                "1) Schedule An Event\n" +
                                "2) Show My Schedule\n" +
                                "3) Cancel An Event"
                    }
                    else -> { // if none of the options, checks the input for the date
                        val check = splitDateFunctionAssistant(message) // calls the split date function
                        return if (check) { // if the input is valid it continues
                            responseType = 1 // sets response type to 1 to advance
                            "The Day Chosen Is $message\n" + // returns the next step of instructions
                                    "Say The Time To Schedule\n"+
                                    "e.g. '10:30 AM To 1:30 PM'"
                        } else { // if the input is false, response type is set to current stage
                            responseType = 0 // and returns the help message to the user.
                            "Invalid Input, Say What Day You Want To Schedule On\n" +
                                    "e.g. '24th Of March 2021'"
                        }
                    }
                }
            }

            if (responseType == 1) { // this stage is getting the time to schedule
                return when (message) { // return the string when the message is equal to the options
                    "help" -> {
                        "Say The Time To Schedule\n"+ // returns the help message
                                "e.g. '10:30 AM To 1:30 PM'"
                    }
                    "home" -> {
                        scheduleStage = false // no longer scheduling an event thus false
                        initialStage = true // back to the start so initial stage is true
                        responseType = 0 // resets the response type to zero
                        "What would you like to do?" + "\n1) Schedule An Event\n2) Show My Schedule\n3) Cancel An Event"
                        // returns the intro paragraph
                    }
                    else -> { // if none of the options are chosen, checks the input for the time
                        try {
                            initialStart = true
                            val delimiter1 = "to" //splits the message using to
                            val parts = message.split(delimiter1, ignoreCase = true) // splits the parts equally
                            // e.g 10:30am To 12:00pm will be '10:30am' and '12:00pm'
                            val check = timeFormatFunction(parts[0]) // check if the start time is correctly formatted
                            val check2 = timeFormatFunction(parts[1]) // check if the finish time is correctly formatted
                            if (check && check2) { // if both checks are true it continues
                                // checks if the start time is bigger than finish time. if both of them are not in the range of 0 to 1440 minutes and if they're equal
                                // if so its an incorrect input
                                if ((tempStoreTimeStart > tempStoreTimeFinish) || (tempStoreTimeStart !in (0..1440))
                                        || (tempStoreTimeFinish !in (0..1440)) || (tempStoreTimeStart == tempStoreTimeFinish)) {
                                    responseType = 1
                                    return  "Invalid Input, Make Sure Initial Time Is \n" +
                                            "Greater Than Final Time and within \n" +
                                            "24 Hours of The Day\n" + // returns the invalid input mesasge
                                            "e.g. '10:30 AM To 1:30 PM'"
                                }
                                storeTime = "$startTime to $finishTime" //stores the time message to use when checking
                                responseType = 2 // sets response type to 2 to advance the stages
                                "The Time Chosen Is $storeTime\n" +
                                        "Say The Type Of Activity To Choose From \n" +
                                        "1) Restaurant\n" +
                                        "2) Cinema\n" + // responds back with the time chosen
                                        "3) Supermarket\n" + // and the next stage instructions
                                        "4) Hospital"
                            } else { // if there's an exception tells the user what to do.
                                responseType = 1
                                "Say The Time To Schedule\n"+ // returns the error message
                                        "e.g. '10:30 AM To 1:30 PM'"
                            }
                        } catch (e: Exception) {
                            responseType = 1
                            "Say The Time To Schedule\n"+ // returns the error message
                                    "e.g. '10:30 AM To 1:30 PM'"
                        }
                    }
                }
            }

            if (responseType == 2 ) {
                return when (message) { // return the string when the message is equal to the options
                    "help" -> {
                        "Choose an Activity. Say The Name or Number:\n" +
                                "1) Restaurant\n" +
                                "2) Cinema\n" +       // returns the help message
                                "3) Supermarket\n" +
                                "4) Hospital\n" +
                                "or Type 'Home' To Go Back"
                    }
                    "home" -> {
                        scheduleStage = false // no longer scheduling an event thus false
                        initialStage = true // back to the start so initial stage is true
                        responseType = 0 // resets the response type to zero
                        "What would you like to do?\n" +
                                "1) Schedule An Event\n" + // returns the intro paragraph
                                "2) Show My Schedule\n" +
                                "3) Cancel An Event"
                    }
                    "restaurant", "1" -> { // if the user chooses restaurant or 1, return the next stage steps
                        responseType = 3 // specifies which activity is called in the next section
                        "Say Which Restaurant\n" +
                                "1) Restaurant 1\n" +
                                "2) Restaurant 2\n" +
                                "3) Restaurant 3"
                    }
                    "cinema", "2" -> { // if the user chooses cinema or 2, return the next stage steps
                        responseType = 4 // specifies which activity is called in the next section
                        "Say Which Cinema\n" +
                                "1) Cinema 1\n" +
                                "2) Cinema 2\n" +
                                "3) Cinema 3"
                    }
                    "supermarket", "3" -> { // if the user chooses supermarket or 3, return the next stage steps
                        responseType = 5 // specifies which activity is called in the next section
                        "Say Which Supermarket\n" +
                                "1) Supermarket 1\n" +
                                "2) Supermarket 2\n" +
                                "3) Supermarket 3"
                    }
                    "hospital", "4" -> { // if the user chooses hospital or 4, return the next stage steps
                        responseType = 6 // specifies which activity is called in the next section
                        "Say Which Hospital\n" +
                                "1) Hospital 1\n" +
                                "2) Hospital 2\n" +
                                "3) Hospital 3"
                    }
                    else -> { // if none of the options are met, error message is sent out
                        responseType = 2
                        "Error\n " +
                                "Choose an Activity. Type:\n" +
                                "1) Restaurant\n" +
                                "2) Cinema\n" +
                                "3) Supermarket\n" +
                                "4) Hospital\n" +
                                "or Type 'Home' To Go Back"
                    }
                }
            }

            if (responseType in 3..6 ) { // checks the response type is within a range

                when (responseType) { // check the response type values
                    3 -> when (message) { // check the message values
                        "1", "2", "3" -> message = "restaurant $message"
                    } // if the user chose a numerical value to select, its converted to the place name
                    4 -> when (message) {
                        "1", "2", "3" -> message = "cinema $message"
                    } // ^same
                    5 -> when (message) {
                        "1", "2", "3" -> message = "supermarket $message"
                    } // ^same
                    6 -> when (message) {
                        "1", "2", "3" -> message = "hospital $message"
                    } // e.g if they chose 1 for hospital activity, it will save the response as Hospital 1
                }
                if (message == "home") {
                    scheduleStage = false // no longer scheduling an event thus false
                    initialStage = true // back to the start so initial stage is true
                    responseType = 0 // resets the response type to zero
                    return "What would you like to do?" + "\n1) Schedule An Event\n2) Show My Schedule\n3) Cancel An Event"
                    // returns the intro paragraph
                } else {
                    return if (Constants.Places.contains(message.toLowerCase(Locale.ROOT))) { // checks if the
                        storePlaceName = message //  event place is in the acceptable options and stores it so for the database
                        responseType = 7 // sets the response type to 7 to advance forward
                        "Is this activity correct? \n" + // returns to the user to confirm the activity in the next step
                                "$storeDate, $storeTime at $storePlaceName\n" +
                                "1) Yes\n" +
                                "2) No"
                    } else { // since the user cant see the previous text like the chatbot, the error
                        // must remind the user of the options
                        var errorString = "Enter The Number of The Place or Name.\n" //
                        when (responseType) { // checks which response type and adds the
                            // corresponding error message
                            3 -> errorString += "Say Which Restaurant\n" +
                                    "1) Restaurant 1\n" +
                                    "2) Restaurant 2\n" +
                                    "3) Restaurant 3"
                            4 -> errorString += "Say Which Cinema\n" +
                                    "1) Cinema 1\n" +
                                    "2) Cinema 2\n" +
                                    "3) Cinema 3"
                            5 -> errorString += "Say Which Supermarket\n" +
                                    "1) Supermarket 1\n" +
                                    "2) Supermarket 2\n" +
                                    "3) Supermarket 3"
                            6 -> errorString += "Say Which Hospital\n" +
                                    "1) Hospital 1\n" +
                                    "2) Hospital 2\n" +
                                    "3) Hospital 3"
                        }
                        return errorString // returns the string to the voice assistant
                    }
                }
            }

            if (responseType == 7 ) {
                return when (message) { // check the response type values

                    "help" -> { // returns to the user what to say to advance
                        "Say 'Yes'/'1' to Confirm Or\n" +
                                "'No'/'2'/'Home' to go back to the Menu."
                    }
                    "home", "no", "2" -> { // if any of these options are chosen no event is scheduled
                        scheduleStage = false // schedule stage is false and is returns to initial stage
                        initialStage = true
                        "What would you like to do?\n" +
                                "1) Schedule An Event\n" +
                                "2) Show My Schedule\n" +
                                "3) Cancel An Event"
                        // returns the intro paragraph
                    }
                    "yes", "1" -> { // if yes or 1, schedules the event
                        endTimer = System.nanoTime() // end the timer. end time minus start time to get how long it
                        // took for the user to do the task. to be used in the evaluation
                        showTimeTaken = true // set to true the time so the time elapsed is shown on screen
                        scheduleStage = false // schedule stage is false since  event has been scheduled
                        initialStage = true // initial stage to go back to menu
                        responseType = 0 // resets the response type
                        homeIntro = true // signifies the chatbot to type the intro paragraph
                        submitSchedule = true //signifies the event has been scheduled to insert into database
                        "Your Event Has Been Scheduled!\n" // returns event has been scheduled
                    }
                    else -> {
                        responseType = 7 // loops here till an option is given
                        "Say 'Yes'/'1' to Confirm Or\n" + // returns the instructions to advance
                                "'No'/'2'/'Home' to go back to the Menu."
                    }
                }
            }
        }

        if (showStage) { // this is for displaying the schedule on a date
            if (responseType == 0 ) {

                return when (message) {
                    "help" -> { //returns to the user instructions on how to answer the question
                        "Say What Day You Want To See Your Schedule\n" +
                                "e.g. '24th Of March 2021'"
                    }
                    "home" -> {
                        showStage = false // no longer showing the events thus false
                        initialStage = true // back to the start so initial stage is true
                        responseType = 0 // resets the response type to zero
                        "What would you like to do?" + "\n1) Schedule An Event\n2) Show My Schedule\n3) Cancel An Event"
                        // returns the intro paragraph
                    }
                    else -> { // calls the split date function to check the date
                        val check = splitDateFunctionAssistant(message)
                        return if (check) { // if the input is correct, allows the user to proceed further
                            endTimer = System.nanoTime() // end the timer. end time minus start time to get how long it
                            // took for the user to do the task. to be used in the evaluation
                            showTimeTaken = true // set to true the time so the time elapsed is shown on screen
                            showStage = false // no longer showing the events thus false
                            initialStage = true // initial stage to go back to menu
                            responseType = 0 // resets the response type
                            homeIntro = true // signifies to do the intro paragraph
                            submitShow = true // signifies to read the database for the date. returns all the events
                            "Your Schedule for $message is... \n" // returns the schedule for the date

                        } else {
                            responseType = 0 // sets response to zero and tells the user what to do
                            // error handling
                            "Say What Day You Want To Schedule On\n" +
                                    "e.g. '24th Of March 2021'"
                        }
                    }
                }
            }
        }

        if (cancelStage) { // this is for cancelling an event on a given date

            if (responseType == 0 ) {
                return when (message) {
                    "help" -> { //returns to the user instructions on how to answer the question
                        "Say What Day You Want To Cancel An Event On\n" +
                                "e.g. '24th Of March 2021'"
                    }
                    "home" -> {
                        cancelStage = false // no longer cancelling an event thus false
                        initialStage = true // back to the start so initial stage is true
                        responseType = 0 // resets the response type to zero
                        "What would you like to do?" + "\n1) Schedule An Event\n2) Show My Schedule\n3) Cancel An Event"
                        // returns the intro paragraph
                    }
                    else -> { // if none of the previous options were met, moves to the else
                        val check = splitDateFunctionAssistant(message) // calls the assistant date function to check the date
                        Log.e("Voice Assistant Cancel", "$storeDate THE DATE")
                        if (check) { // if the input is correct, allows the user to proceed further
                            responseType = 1 // increments the response type to move onto next stage
                            emptyExtraCheck = true // stops the schedule being shown
                            submitShow = true // signifies to read the database for the date. returns all the events
                            if (emptyEvents) {
                                cancelStage = false // no longer cancelling an event thus false
                                initialStage = true // back to the start so initial stage is true
                                responseType = 0 // resets the response type to zero
                                homeIntro = true // signifies to show the intro paragraph
                                return "No Events Scheduled" // returns response
                            } else {
                                submitShow = true // signifies to read the database for the date. returns all the events
                                emptyExtraCheck = false // allows the schedule to be shown
                                "Your Schedule On $message is... \n" + // returns to the user the string for the
                                        "Which event would you like to cancel?" // next part instructions
                            }
                        } else {
                            responseType = 0 //returns the instructions to the user to say
                            "Say What Day You Want To Cancel An Event On\n" +
                                    "e.g. '24th Of March 2021'"
                        }
                    }
                }
            }

            if (responseType == 1) {
                if (message == "home") {
                    cancelStage = false // no longer cancelling an event thus false
                    initialStage = true // back to the start so initial stage is true
                    responseType = 0 // resets the response type to zero
                    return "What would you like to do?" + "\n1) Schedule An Event\n2) Show My Schedule\n3) Cancel An Event"
                } else {
                    return try { // tries to return a value
                        val value = (message.toInt() - 1) * 4 // all the values for events
                        // when read are stored in a list, each event is in groups of 4
                        // so if the user was to choose option 1 to delete, it will be
                        // 0 in the list to 3 for that event.
                        Log.e("ChatBot",  " $value VALUE HEREEE")

                        storeDate = StoreEvents.events[value] // gets the value for the date of the event
                        storePlaceName = StoreEvents.events[value+1] // gets the value for the place of the event
                        startTime = StoreEvents.events[value+2] // gets the value for the start time of the event
                        finishTime = StoreEvents.events[value+3] // gets the value for the finish time of the event
                        storeTime = "$startTime To $finishTime" // joins the time to be more easier to read

                        val event = "$storeDate, $storeTime at ${storePlaceName.capitalize(Locale.ROOT)}\n"
                        // combines the event values to show the user
                        responseType = 2 // advances the user to next stage of options
                        "Is this the Correct Event? \n" + // ask the user if the event is the correct one
                                "'$event'\n" +
                                "1) Yes\n" +
                                "2) No"

                    }  catch (e: Exception) { // if there's an exception tells the user what to do.
                        responseType = 1
                        submitShow = true // show the events to cancel
                        "Invalid Input, Enter The Number of The Place"
                    }
                }
            }

            if (responseType == 2 ) {

                return when (message) {
                    "help" -> { //returns to the user instructions on how to answer the question
                        "Say 'Yes'/'1' to Cancel The Event\n " +
                                "Or 'No'/'2'/'Home' to go back to the Menu."
                    }

                    "yes", "1" -> { // if the user response is either yes or 1, it
                        // tells them the event has been cancelled
                        endTimer = System.nanoTime() // end the timer. end time minus start time to get how long it
                        // took for the user to do the task. to be used in the evaluation
                        showTimeTaken = true // set to true the time so the time elapsed is shown on screen
                        responseType = 0 // resets the response time
                        cancelStage = false // cancel stage is complete so its false
                        initialStage = true // initial stage is true to reset
                        homeIntro = true // signifies to show the intro paragraph
                        submitCancel = true // signifies to delete the event
                        "Your Event Has Been Cancelled!\n" // returns the event has been cancelled
                    }
                    "home", "no", "2" -> {// if the user response is either no or 2, it
                        // goes back to the start
                        cancelStage = false
                        initialStage = true
                        "What would you like to do?\n" +
                                "1) Schedule An Event\n" + // returns the intro paragraph
                                "2) Show My Schedule\n" +
                                "3) Cancel An Event"
                    }
                    else -> {// if no option is chosen tells the user to enter the correct
                        // option
                        responseType = 2 // sets the response type to 2 to loop
                        "Say 'Yes'/'1' to Cancel The Event\n " + // returns this message
                                "Or 'No'/'2'/'Home' to go back to the Menu."
                    }
                }
            }
        }
        return "Error, Out Of Bounds" // if there's an error overall, returns out of bounds.
    }
}