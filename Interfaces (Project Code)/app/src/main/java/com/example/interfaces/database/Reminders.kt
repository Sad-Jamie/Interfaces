package com.example.interfaces.database

class Reminders {
    var id: Int = 0 // initialises the id variable
    var date: String = "" // initialises the date variable
    var place: String = "" // initialises the place variable
    var description: String = "" // initialises the description variable
    var startTime: String = "" // initialises the startTime variable
    var finishTime: String = "" // initialises the finishTime variable

    //constructs the reminder object with the values passes
    constructor(date:String, place:String, description:String, startTime:String, finishTime:String) {
        this.date = date
        this.place = place
        this.description = description
        this.startTime = startTime
        this.finishTime = finishTime
    }

    constructor() { //empty constructor to pull information without passing values
    }

}