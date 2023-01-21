package com.example.interfaces.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.interfaces.R
import com.example.interfaces.objects.Constants.RECEIVE_ID
import com.example.interfaces.objects.Constants.SEND_ID
import kotlinx.android.synthetic.main.message_item.view.*

class MessageAdapter: RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() { // creates the recylcer view

    var messagesList = mutableListOf<Message>() //creates a modifiable list for the messages

    inner class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // creates the message view holder
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        return MessageViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.message_item, parent, false))
        // returns the message view holder
    }

    override fun getItemCount(): Int {
        return messagesList.size
        // returns the number of messages in the list
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val currentMessage = messagesList[position] // gets the current message and stores it

        when (currentMessage.id) { // does a when check for the id for the type of id it is
            SEND_ID -> {  // if the id is send id then :
                holder.itemView.tv_message.apply {
                    text = currentMessage.message // sets the message boxes in the message adapter layout to the text
                    visibility = View.VISIBLE
                } // this is the user sending text
                holder.itemView.tv_bot_message.visibility = View.GONE
                // makes the bot message item box invisible, only the user box
            }

            RECEIVE_ID -> { // if the id is receive id then :
                holder.itemView.tv_bot_message.apply {
                    text = currentMessage.message // sets the message boxes in the message adapter layout to the text
                    visibility = View.VISIBLE
                } // this is the bot responding back
                holder.itemView.tv_message.visibility = View.GONE
                // makes the user message item box invisible, only the bot box
            }
        }

    }

    fun insertMessage(message: Message) {
        this.messagesList.add(message) // inserts the message into the adapter
        notifyItemInserted(messagesList.size) // notifies the view to update that it has been inserted
        // does the animation to simulate a real messaging application for familiarity
    }
}

