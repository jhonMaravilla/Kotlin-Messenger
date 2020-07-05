package com.kotlin.messenger.adapter

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat.startActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.kotlin.messenger.R
import com.kotlin.messenger.main.ChatLogActivity
import com.kotlin.messenger.model.Message
import com.kotlin.messenger.model.User
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.layout_latest_messages.view.*

class LatestMessageRow(val chatMessage: Message, val senderID: String) :
    Item<GroupieViewHolder>() {
    override fun getLayout(): Int {
        return R.layout.layout_latest_messages
    }

    var user: User? = null

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {

        val ref = FirebaseDatabase.getInstance().getReference("/users/$senderID")

        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                user = p0.getValue(User::class.java)

                Picasso.get().load(user?.profileImageUrl)
                    .into(viewHolder.itemView.latestmessage_img_user)
                viewHolder.itemView.latestmessage_txt_name.text = user?.username
                viewHolder.itemView.latestmessage_txt_message.text = chatMessage.text

            }

        })
    }
}