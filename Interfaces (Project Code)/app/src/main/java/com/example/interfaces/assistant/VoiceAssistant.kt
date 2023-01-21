package com.example.interfaces.assistant

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.interfaces.R
import com.example.interfaces.database.DatabaseHandler
import com.example.interfaces.database.Reminders
import com.example.interfaces.objects.BotResponse
import com.example.interfaces.objects.StoreEvents
import kotlinx.android.synthetic.main.fragment_voice_assistant.*
import java.util.*


class VoiceAssistant : Fragment(),TextToSpeech.OnInitListener {

    private val RqSpeechRec = 102 // speech request code
    private var toggleOn: Boolean = true // toggle feature to turn off the text to speech
    private var tts: TextToSpeech? = null // initializes the text to speech

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_voice_assistant, container, false)
    }

    override fun onViewCreated(itemView: View, savedInstanceState: Bundle?) {
        super.onViewCreated(itemView, savedInstanceState)

        tts = TextToSpeech(activity, this) // constructs the text to speech for this activity

        startMessage() // shows the start message to the user in the response box
        speech_btn.setOnClickListener {
            speechInput() // adds on click listener to teh speech box so the user can speech to text
        }
        text_to_speech_button.setOnClickListener {
            toggleSpeech() // calls the toggle function to turn on or off the text to speech
        }
    }

    private fun toggleSpeech() { // toggle speech function
        toggleOn = if (toggleOn) { //if the speech icon is green sets it to red to signify off
            // checks if toggle on is true meaning on, if so sets it false
            text_to_speech_button.setImageResource(R.drawable.speech_red)
            tts!!.stop() // stops the text to speech to stop when icon is red
            false // returns false
        } else {
            // sets the text to speech back to on
            text_to_speech_button.setImageResource(R.drawable.speech_green) // sets the icon to the
            // green variant
            true // returns true for toggle on
        }

    }

    private fun startMessage() { // start message function to show the user.
        temp_text.text = BotResponse.homeIntroParagraph() // sets the response text to the intro
        BotResponse.responseType = 0 // response type is 0 to reset it
        BotResponse.initialStage = true // initial stage is true to reset it
        BotResponse.scheduleStage = false // schedule stage is false to reset it
        BotResponse.showStage = false // show stage stage is false to reset it
        BotResponse.cancelStage = false // cancel stage stage is false to reset it
    }

    @Suppress("DEPRECATION") //suppresses deprecated java used methods that work in kotlin
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data) // on the result from the speech recognition
        if (requestCode == RqSpeechRec && resultCode == Activity.RESULT_OK) { // checks if request code matches
            val result = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            live_text.text = result?.get(0).toString() // pulls the data from the speech and show it on the live
            // transcription beneath the button for the user to see the text
            var response = BotResponse.assistantConversationFlow(result?.get(0).toString())
            // the text from the user is sent to the bot response function to get the reponse back
            if (BotResponse.submitShow) { // if the user request to see the schedule of a date
                val schedule = readDatabase() // calls the read data function
                // this is here after the bot response and before the next message to align the order
                if (!BotResponse.emptyExtraCheck) { // for the delete function to check if there's data to delete
                    response += "\n $schedule"
                    // if there's data it outputs the schedule when needed
                }
            }
            if (BotResponse.homeIntro) { // if the home intro boolean is true, add the intro paragraph
                response += "\n\n ${BotResponse.homeIntroParagraph()}" // adds the response to the home intro
            }
            if (BotResponse.submitSchedule) { // if the user has correctly submitted an event
                // it calls the insert into database function
                insertIntoDatabase() //calls the insert function to insert the data scheduled
            }
            if (BotResponse.submitCancel) { // if the user has requested to delete an event
                // if the submit cancel boolean is true, it calls the delete even function
                deleteEventInDatabase() // calls the delete function
            }

            if (BotResponse.showTimeTaken) {
                // if the showTimeTaken  boolean is true, it calls the show time taken function
                showTimeTaken()
            }

            temp_text.text = response // sets the response text box equal to the response

            if (toggleOn) { // if the toggle is on, the text to speech will speak it
                textToSpeech(response) // calls the text to speech with the response.
            }
        }
    }

    private fun showTimeTaken() {
        Toast.makeText(activity, "Elapsed Time: ${(BotResponse.endTimer - BotResponse.startTimer)} ", Toast.LENGTH_LONG)
            .show() // the purpose of the code here is to show the time taken
        BotResponse.showTimeTaken = false // for the user to insert/delete/show using the chatbot
    }

    @Suppress("DEPRECATION") //suppresses deprecated java used methods that work in kotlin
    private fun speechInput() { // speech input function
        if (!SpeechRecognizer.isRecognitionAvailable(activity)) {
            Toast.makeText(activity, "Speech Recognition Is Not Available", Toast.LENGTH_SHORT).show()
            // if the user cannot user speech to text, tells them.
        } else {
            tts!!.stop() // stops any prior text to speech
            val i = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH) // Starts an activity that will prompt
            // the user for speech and send it through a speech recognizer.
            i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            // Informs the recognizer which speech model to prefer when performing
            i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault()) // This tag informs the recognizer
            // to perform speech recognition in a language different than the default
            i.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say Something!") // Text prompt to show
            // to the user when asking them to speak.
            startActivityForResult(i, RqSpeechRec) // starts the intent
        }
    }

    override fun onInit(status: Int) { // onIt is for the text to speech function
        // initializes the text to speech and outputs if there is any errors in the log
        if (status == TextToSpeech.SUCCESS) { // Denotes a successful operation for the text to speech
            val result = tts!!.setLanguage(Locale.UK) // ets the text-to-speech language to UK Language
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "The Language Specified is not supported!")
                // Denotes the language data is missing.
            }
            // Sets the listener that will be notified of various events related to the synthesis of a given utterance.
            tts!!.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {
                }
                // Called when an utterance has successfully completed processing.
                override fun onDone(utteranceId: String) {
                    Log.e("Voice Assistant", "Finished")
                    speechInput()
                }
                // Called when an error has occurred during processing.
                override fun onError(utteranceId: String) {
                    Log.e("Voice Assistant", "error on $utteranceId")
                }
            })

        } else {
            Log.e("TTS", "Failed Initialization")
            //Denotes a failed operation for the speech to text
        }
    }



    private fun textToSpeech(response: String) {
        tts!!.speak(response, TextToSpeech.QUEUE_FLUSH, null, "") // Speaks the text
        // this function does the text to speech, removes prior text to speech in queue
    }

    private fun insertIntoDatabase() {
        BotResponse.submitSchedule = false // sets submit schedule to false as data has been inserted into database
        val reminder = Reminders(BotResponse.storeDate, BotResponse.storePlaceName, "Basic Description",
                BotResponse.startTime, BotResponse.finishTime) //passes the values to be stored
        context?.let { DatabaseHandler(it) }?.insertData(reminder) //gets the context and calls the insert
        //data function in the database
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
            Log.e("Voice Assistant null and empty", "HELLO")
            for (i in 0 until data.size) { // iterates through all the data/events
                BotResponse.emptyEvents = true
                val event = ("${data[i].date}, ${(data[i].place).capitalize()}, ${data[i].startTime} To ${data[i].finishTime} \n" +
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

    private fun deleteEventInDatabase() {
        BotResponse.submitCancel = false // sets it back to false so it doesn't trigger unnecessary deletes

        val db = activity?.let { DatabaseHandler(it) } // object of database handler, connects to it
        db?.deleteData(BotResponse.storeDate, BotResponse.storePlaceName, BotResponse.startTime, BotResponse.finishTime)
        // calls the delete data function with the values passed acquired from the bot response object
    }

}