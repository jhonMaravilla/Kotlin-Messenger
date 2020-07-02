package com.kotlin.messenger.main

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.kotlin.messenger.R
import com.kotlin.messenger.model.Message
import com.kotlin.messenger.model.User
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_chat_log.*
import kotlinx.android.synthetic.main.layout_chat_from_me.view.*
import kotlinx.android.synthetic.main.layout_chat_from_user.view.*
import java.text.SimpleDateFormat
import java.util.*

class ChatLogActivity : AppCompatActivity(), View.OnClickListener {
    val adapter = GroupAdapter<GroupieViewHolder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)

        val user = intent.getParcelableExtra<User>("MESSAGE_TO")
        supportActionBar?.title = user.username

        chatlog_btn_send.setOnClickListener(this)

        chatlog_recyclerview.adapter = adapter
        chatlog_recyclerview.layoutManager = LinearLayoutManager(this);

        listenForMessages()
    }

    private fun listenForMessages() {
        val reference = FirebaseDatabase.getInstance().getReference("/messages")

        reference.addChildEventListener(object : ChildEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
                TODO("Not yet implemented")
            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                TODO("Not yet implemented")
            }

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val chatMessage = p0.getValue(Message::class.java)

                if (chatMessage != null) {
                    if (chatMessage.fromID == FirebaseAuth.getInstance().uid) {
                        adapter.add(ChatFromMe(chatMessage.text))
                    } else {
                        adapter.add(ChatFromUser(chatMessage.text))
                    }
                }
            }

            override fun onChildRemoved(p0: DataSnapshot) {
                TODO("Not yet implemented")
            }

        })


    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.chatlog_btn_send -> {
                sendMessage()
            }
        }
    }

    // This function is saving message to firebase database
    private fun sendMessage() {
        val reference = FirebaseDatabase.getInstance().getReference("/messages").push()
        val user = intent.getParcelableExtra<User>("MESSAGE_TO")

        val c = Calendar.getInstance()

        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        val hour = c.get(Calendar.HOUR_OF_DAY)
        val minute = c.get(Calendar.MINUTE)

        val id = reference.key
        val fromID = FirebaseAuth.getInstance().uid
        val toID = user.uid
        val message = chatlog_etxt_message.text.toString()
        val date = "$month-$day-$year"
        val time = "$hour:$minute"

        if (fromID == null) return

        val chatMessage = Message(id!!, fromID, toID, message, date, time)

        reference.setValue(chatMessage)
            .addOnSuccessListener {
                chatlog_etxt_message.setText("")
            }
            .addOnFailureListener {
                Log.d("PUSH", "sendMessage: FAILED")
            }

    }
}


// FOR GROUPIE RECYCLERVIEW
class ChatFromUser(val text: String) : Item<GroupieViewHolder>() {
    override fun getLayout(): Int {
        return R.layout.layout_chat_from_user
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.fromuser_txt_message.text = text
    }

}

class ChatFromMe(val text: String) : Item<GroupieViewHolder>() {
    override fun getLayout(): Int {
        return R.layout.layout_chat_from_me
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.fromme_txt_message.text = text
    }

}