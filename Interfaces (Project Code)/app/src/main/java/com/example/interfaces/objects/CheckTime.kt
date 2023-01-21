package com.example.interfaces.objects

object CheckTime {

    private lateinit var firstPart: String // first part of the time passed e.g. XX out of XX:YY
    private lateinit var secondPart: String // second part of the time passed e.g. YY out of XX:YY

    fun timeFormat(time: String): String {
        println(time)

        val delimiter = ":" // what is used to split the input
        val parts = time.split(delimiter, ignoreCase = true) // splits the time passed using :
        if (parts[0].length == 1) {
            firstPart = "0${parts[0]}"  // if the time passed is 1 character it adds the 0 at the start
            println(firstPart)
        } else {
            firstPart = parts[0] // if its already in the proper format set it equal straight away
        }
        if (parts[1].length == 1) {
            secondPart = "0${parts[1]}" // if the time passed is 1 character it adds the 0 at the start
            println(secondPart)
        } else {
            secondPart = parts[1]  //if its already in the proper format set it equal straight away
        }
        // this is just to add consistency to the database and other areas.


        return "$firstPart:$secondPart" // returns the formatted time
    }
}