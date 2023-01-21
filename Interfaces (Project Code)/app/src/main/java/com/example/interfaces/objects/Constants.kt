package com.example.interfaces.objects

object Constants {
    const val SEND_ID = "SEND_ID" //send id is whoever sends the message
    const val RECEIVE_ID = "RECEIVE_ID" //recieve id is whoever recives the message

    val DaySuffix =
            arrayOf(  // this is to convert the day to readable text for the user e.g 1st of april 2020 not 1/4/2020
                    "Null", "1st", "2nd", "3rd", "4th", "5th", "6th", "7th", "8th", "9th",
                    "10th", "11th", "12th", "13th", "14th", "15th", "16th", "17th", "18th", "19th",
                    "20th", "21st", "22nd", "23rd", "24th", "25th", "26th", "27th", "28th", "29th",
                    "30th", "31st"
            )

    val Months = arrayOf( // this is to convert the month to readable text for the user e.g 1st of april 2020 not 1/4/2020
            "Null","january", "february", "march", "april", "may", "june",
            "july", "august", "september", "october", "november", "december"
    )

    val Places = arrayOf( //all the possible places, checks against these values make sure input is valid
            "restaurant 1","restaurant 2", "restaurant 3", "cinema 1", "cinema 2", "cinema 3", "supermarket 1",
            "supermarket 2", "supermarket 3", "hospital 1", "hospital 2", "hospital 3"
    )

    val Events = arrayOf( // all the events that are prebuilt
            "event 1","event 2", "event 3", "event 4", "event 5"
    )

    val ordinalSuffix = arrayOf( // these are the ordinal suffix so it can be removed from the message
        "st","nd", "rd", "th" // so it can be checked if the input is correct.
    )

    val suffixAmPm = arrayOf( // these are the am/pm suffix so it can be removed from the message
            "a.m.","p.m." // so it can be checked if the input is correct.
    )

    val timeFormatAMNoColon = arrayOf( // converts inputs like 1 a.m to 01:00 for consistency
            "null","01:00","02:00","03:00","04:00","05:00","06:00","07:00","08:00","09:00","10:00","11:00","24:00"
    )

    val timeFormatAMWithColon = arrayOf( // converts inputs like 1:30 a.m to 01:30 for consistency
            "null","01","02","03","04","05","06","07","08","09","10","11","24"
    )

    val timeFormatPMNoColon = arrayOf( // converts inputs like 1 p.m to 13:00 for consistency
            "null","13:00","14:00","15:00","16:00","17:00","18:00","19:00","20:00","21:00","22:00","23:00","12:00"
    )

    val timeFormatPMWithColon = arrayOf( // converts inputs like 1:30 p.m to 13:30 for consistency
            "null","13","14","15","16","17","18","19","20","21","22","23","12"
    )
}

