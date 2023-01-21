package com.example.interfaces.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.widget.Toast

const val Database_Name = "ReminderDatabase" // Database name
const val Table_Name = "Reminders" // Table Name To Use
const val Col_Date = "Date" // Date For The Event Scheduled
const val Col_Place = "Place" // Name For The Event Scheduled
const val Col_Description = "Description" // Description For The Event Scheduled
const val Col_StartTime = "StartTime" // Start Time For The Event Scheduled
const val Col_FinishTime = "FinishTime" // Finish Time For The Event Scheduled
const val Col_ID = "id" // Id Event Scheduled

class DatabaseHandler(private var context: Context) : SQLiteOpenHelper(context, Database_Name, null, 1) {
    override fun onCreate(db: SQLiteDatabase?) { // if there is no database, it will create one with these table values
       val createTable = "CREATE TABLE " + Table_Name + " (" + Col_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
               Col_Date + " VARCHAR(256)," + // Date column values allows up to 256 Characters
               Col_Place + " VARCHAR(256)," + // Place column values allows up to 256 Characters
               Col_Description + " VARCHAR(256)," + // Description column values allows up to 256 Characters
               Col_StartTime + " VARCHAR(256)," + // Start Time column values allows up to 256 Characters
               Col_FinishTime + " VARCHAR(256))" // Finish column values allows up to 256 Characters

        db?.execSQL(createTable) // executes the query to create a local database
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // if there's a newer version of sql, no need to implement
    }

    fun insertData(reminder : Reminders) { // function to insert into the database
        val db = this.writableDatabase // opens the database with writable attribute
        val cv = ContentValues() // Creates an empty set of values using the default initial size
        cv.put(Col_Date, reminder.date) // puts the date passed into the empty set
        cv.put(Col_Place, reminder.place) // puts the place passed into the empty set
        cv.put(Col_Description, reminder.description) // puts the description passed into the empty set
        cv.put(Col_StartTime, reminder.startTime) // puts the start time passed into the empty set
        cv.put(Col_FinishTime, reminder.finishTime) // puts the finish time passed into the empty set
        val result = db.insert(Table_Name, null, cv) // inserts the values into the database
        if (result == (-1).toLong()) { // if it fails returns an error to screen
            Toast.makeText(context, "Failed To Schedule Event", Toast.LENGTH_SHORT).show()
        }
        else { // if it does not fail, returns inserted to screen
            Toast.makeText(context, "Event Scheduled", Toast.LENGTH_SHORT).show()
        }

    }

    fun readData(date: String): MutableList<Reminders> { // read data function
        val list : MutableList<Reminders> = ArrayList () //creates a mutable list to add data to

        val db = this.readableDatabase // creates a readable database connection
        val query = "SELECT * from $Table_Name WHERE $Col_Date = '$date'" // selects rows of data from a date
        //val query = "SELECT * from $Table_Name" // if i want to debug and see all the rows
        val result = db.rawQuery(query, null) // Returns: A Cursor object, which is
        // positioned before the first entry.
        if (result.moveToFirst()) { // moves to the first row if there's data
            do {
                val reminder = Reminders () //instantiates reminder
                reminder.id = result.getString(result.getColumnIndex(Col_ID)).toInt() //gets the data for id
                reminder.date = result.getString(result.getColumnIndex(Col_Date)) //gets the data for date
                reminder.place = result.getString(result.getColumnIndex(Col_Place)) //gets the data for place
                reminder.description = result.getString(result.getColumnIndex(Col_Description)) //gets the data for description
                reminder.startTime = result.getString(result.getColumnIndex(Col_StartTime)) //gets the data for start time
                reminder.finishTime = result.getString(result.getColumnIndex(Col_FinishTime)) //gets the data for finish time
                list.add(reminder) // adds the reminder object to the list
            } while (result.moveToNext()) // moves to the next row if there's more data pulled
        }

        result.close() // closes the the cursor object
        db.close() // closes the database connection
        return list // returns the list to be uses
    }

    fun deleteData(date: String, place: String, startTime: String, finishTime: String){ // delete data function
        val db = this.readableDatabase // opens a readable database
        db.delete(Table_Name, "$Col_Date='$date' AND $Col_Place='$place' AND $Col_StartTime='$startTime' AND $Col_FinishTime='$finishTime'", null)
        // deletes the row where the its exactly equal to the event values
        db.close() // closes the database connection
    }


}