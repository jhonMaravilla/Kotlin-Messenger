package com.kotlin.messenger.main

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.kotlin.messenger.R
import com.kotlin.messenger.model.User
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_new_message.*
import kotlinx.android.synthetic.main.layout_newmessage.view.*

class NewMessageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_message)

        supportActionBar?.title = "Select User"

        fetchUsers(this)
    }

    private fun fetchUsers(context: Context) {
       val ref = FirebaseDatabase.getInstance().getReference("/users")

        ref.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                val adapter = GroupAdapter<GroupieViewHolder>()

                p0.children.forEach{
                    val user = it.getValue(User::class.java)
                    if(user != null){
                        adapter.add(UserItem(user))
                    }

                }

                adapter.setOnItemClickListener { item, view ->
                    val userItem = item as UserItem

                    val intent = Intent(view.context, ChatLogActivity::class.java)
                    intent.putExtra("MESSAGE_TO", userItem.user)
                    startActivity(intent)
                    finish()
                }

                newmessage_recyclerview.adapter = adapter
                newmessage_recyclerview.layoutManager = LinearLayoutManager(context)

            }

        })
    }
}

class UserItem(val user:User): Item<GroupieViewHolder>(){
    override fun getLayout(): Int {
        return R.layout.layout_newmessage
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        Picasso.get().load(user.profileImageUrl).into(viewHolder.itemView.newmessage_img_thumbnail)
        viewHolder.itemView.newmessage_txt_name.text = user.username
    }

}