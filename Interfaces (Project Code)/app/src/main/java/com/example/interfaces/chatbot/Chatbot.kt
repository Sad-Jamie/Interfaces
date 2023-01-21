package com.example.interfaces.chatbot

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.interfaces.R
import com.example.interfaces.adapter.Message
import com.example.interfaces.adapter.MessageAdapter
import com.example.interfaces.database.DatabaseHandler
import com.example.interfaces.database.Reminders
import com.example.interfaces.objects.BotResponse
import com.example.interfaces.objects.Constants.RECEIVE_ID
import com.example.interfaces.objects.Constants.SEND_ID
import com.example.interfaces.objects.Time
import com.example.interfaces.objects.StoreEvents
import kotlinx.android.synthetic.main.fragment_chatbot.*
import kotlinx.coroutines.*
import java.util.*

class Chatbot : Fragment() {
    private lateinit var adapter: MessageAdapter // creates object of the adapter

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_chatbot, container, false) //inflates the
        // screen with the fragment chatbot layout, the screen for the chatbot
    }

    override fun onViewCreated(itemView: View, savedInstanceState: Bundle?) {
        super.onViewCreated(itemView, savedInstanceState)

        adapter = MessageAdapter() //instantiates the adapter first
        rv_messages.adapter = adapter //set the adapter for the recycler view
        rv_messages.layoutManager = LinearLayoutManager(activity) //refer to the layout manager
        // with this application context

        clickEvents() // calls the functions to set the on click listeners
        startMessage() // calls the start message function, only occurs at the very start
    }

    private fun startMessage() { // intro message is sent to the user
        customMessage("Welcome To The Chatbot")
        Thread.sleep(100)
        customMessage("What would you like to do?")
        Thread.sleep(100)
        customMessage("1) Schedule An Event\n2) Show My Schedule\n3) Cancel An Event")

        BotResponse.responseType = 0 // resets the boolean values for the stages
        BotResponse.initialStage = true // this is because if the
        BotResponse.scheduleStage = false // user come back to the chatbot, the bot response
        BotResponse.showStage = false // object has not been fully reset
        BotResponse.cancelStage = false // this resets it.
    }

    private fun sendMessage() { // this is the function that sends the messages for the user

        val message = et_message.text.toString() // gets the user message
        val timeStamp = Time.timeStamp() // current time stamp

        if (message.isNotEmpty()) { // check if the message is empty
            et_message.setText("") // sets the input box to empty so the user can type again

            adapter.insertMessage(Message(message, SEND_ID, timeStamp)) // inserts the message to the adapter
            rv_messages.scrollToPosition(adapter.itemCount - 1) // scrolls to the latest message

            botResponse(message)  // calls the botResponse function to get the response to the message
        }
    }

    private fun botResponse(message: String) { // this function calls the botresponse object to get the
        //response from the chatbot.
        val timestamp = Time.timeStamp() // current time stamp

        GlobalScope.launch {
            delay(1000) // delays the time to simulate someone typing back

            withContext(Dispatchers.Main) {

                var response = BotResponse.chatbotConversationFlow(message) // gets the response

                adapter.insertMessage(Message(response, RECEIVE_ID, timestamp)) // inserts the bot
                // message into the adapter to be shown
                rv_messages.scrollToPosition(adapter.itemCount - 1) // scrolls to the latest message

                if (BotResponse.submitShow) { // if the user request to see the schedule of a date
                    val schedule = readDatabase() // calls the read data function
                    // this is here after the bot response and before the next message to align the order
                    if (!BotResponse.emptyExtraCheck) { // for the delete function to check if there's data to delete
                        adapter.insertMessage(Message(schedule, RECEIVE_ID, timestamp))
                        rv_messages.scrollToPosition(adapter.itemCount - 1)
                        // if there's data it outputs the schedule when needed
                    }
                }

                if (BotResponse.homeIntro) { // if the home intro boolean is true, send the intro paragraph
                    response = BotResponse.homeIntroParagraph() // gets the response from the function
                    // in the bot response object
                    // this is here after the bot response and before the next message to align the order
                    adapter.insertMessage(Message(response, RECEIVE_ID, timestamp))// inserts the bot
                    // message into the adapter to be shown
                    rv_messages.scrollToPosition(adapter.itemCount - 1) // scrolls to the latest message
                }

                if (BotResponse.submitSchedule) { // if the user has correctly submitted an event
                    // it calls the insert into database function
                    insertIntoDatabase()
                }

                if (BotResponse.submitCancel) {
                    // if the submit cancel boolean is true, it calls the delete even function
                    deleteEventInDatabase()
                }

                if (BotResponse.showTimeTaken) {
                    // if the showTimeTaken  boolean is true, it calls the show time taken function
                    showTimeTaken()
                }

            }
        }
    }

    private fun showTimeTaken() {
        Toast.makeText(activity, "Elapsed Time: ${(BotResponse.endTimer-BotResponse.startTimer)} ", Toast.LENGTH_LONG)
            .show() // the purpose of the code here is to show the time taken
        BotResponse.showTimeTaken = false // for the user to insert/delete/show using the chatbot
    }

    private fun insertIntoDatabase() {
        BotResponse.submitSchedule = false // sets submit schedule to false as data has been inserted into database
        val reminder = Reminders(BotResponse.storeDate,BotResponse.storePlaceName, "Basic Description",
            BotResponse.startTime, BotResponse.finishTime) //passes the values to be stored
        context?.let { DatabaseHandler(it) }?.insertData(reminder) //gets the context and calls the insert
        //data function in the database
    }

    private fun deleteEventInDatabase() {
        BotResponse.submitCancel = false // sets it back to false so it doesn't trigger unnecessary deletes

        val db = activity?.let { DatabaseHandler(it) } // object of database handler, connects to it
        db?.deleteData(BotResponse.storeDate, BotResponse.storePlaceName, BotResponse.startTime, BotResponse.finishTime)
        // calls the delete data function with the values passed acquired from the bot response object
    }


    private fun readDatabase(): String {
        BotResponse.submitShow = false // sets it back to false so it doesn't trigger unnecessary reads

        var showSchedule = "" // initialises the initial variable that will be output to the user

        val db = activity?.let { DatabaseHandler(it) } // object of database handler, connects to it
        val data = db?.readData(BotResponse.storeDate) // calls the read function from the handler
        // this gets all the rows from the database associated with the date

        StoreEvents.parseEventsData(data) // calls the store events object to store the data incase
        //events needs to be deleted

        if (data != null && data.isNotEmpty()) { // if the data is not null and not empty
            for (i in 0 until data.size) { // iterates through all the data/events
                BotResponse.emptyEvents = true
                val event = ("${data[i].date}, ${(data[i].place).capitalize(Locale.ROOT)}, ${data[i].startTime} To ${data[i].finishTime} \n" +
                        data[i].description) // stores the pulled data in the event string to then be shown on the screen
                showSchedule += if (i == data.size -1) { // if the iterator i is the last event,
                    // i don't want a new line added at the end
                    "${i + 1}) $event"
                } else {
                    "${i + 1}) $event\n"
                }
                BotResponse.emptyEvents = false
            }
        } else { // if there is no data and/or null
            BotResponse.emptyEvents = true // sets empty events to true to be handled later
            return "No Events Scheduled" // returns there are no events scheduled
        }

        return showSchedule // returns the show schedule to be outputted to the user.
    }

    private fun clickEvents() {

        //sets on click listener for the send button message Send a message
        btn_send.setOnClickListener {
            sendMessage() //sets the send button listener to call the send message button
        }

        //Scroll back to correct position when user clicks on text view
        et_message.setOnClickListener {
            GlobalScope.launch {
                delay(100)
                withContext(Dispatchers.Main) {
                    rv_messages.scrollToPosition(adapter.itemCount - 1)// scrolls to the latest
                }
            }
        }
    }

    private fun customMessage(message: String) { // launch the coroutine
        GlobalScope.launch {
            delay(1000)
            withContext(Dispatchers.Main) {
                val timestamp = Time.timeStamp()  // gets the time stamp
                adapter.insertMessage(Message(message, RECEIVE_ID, timestamp)) // inserts the message
                rv_messages.scrollToPosition(adapter.itemCount -1) // scrolls to the latest
                //message which in this case is the bot message
            }
        }

    }

    //In case there are messages, scroll to the bottom when re-opening app to see latest messages
    override fun onStart() {
        super.onStart()
        GlobalScope.launch {
            delay(100)
            withContext(Dispatchers.Main) {
                rv_messages.scrollToPosition(adapter.itemCount - 1) // scrolls to the latest
                //message
            }
        }
    }


}