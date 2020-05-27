package com.example.myapps.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapps.Chat

import com.example.myapps.R
import com.example.myapps.User
import com.example.myapps.activity.MainActivityCallback
import com.example.myapps.adapter.ChatAdapter
import com.example.myapps.util.DATA_MATCHES
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_matches.*

/**
 * A simple [Fragment] subclass.
 */
class MatchesFragment : Fragment() {

    private lateinit var userId:String
    private lateinit var userDatabase: DatabaseReference
    private lateinit var chatDatabase: DatabaseReference
    private var callback: MainActivityCallback? = null

    private var chatsAdapter = ChatAdapter(ArrayList())

    fun setCallback(callback: MainActivityCallback) {
        this.callback = callback
        userId = callback.onGetUserId()
        userDatabase = callback.getUserDatabase()
        chatDatabase = callback.getChatDatabase()

        fetchData()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_matches, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        matchesRV.apply {
            setHasFixedSize(false)
            layoutManager = LinearLayoutManager(context)
            adapter = chatsAdapter
        }
    }
    private fun fetchData() {
        userDatabase.child(userId).child(DATA_MATCHES).addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
            if (p0.hasChildren()){
                p0.children.forEach { child ->
                    val matchid = child.key
                    val chatid = child.value.toString()
                    if (!matchid.isNullOrEmpty()){
                        userDatabase.child(matchid).addListenerForSingleValueEvent(object :ValueEventListener{
                            override fun onCancelled(p0: DatabaseError) {

                            }
                            override fun onDataChange(p0: DataSnapshot) {
                            val user = p0.getValue(User::class.java)
                                if (user!=null){
                                    val chat = Chat(userId,chatid,user.uid,user.name,user.imageUrl)
                                    chatsAdapter.addElement(chat)
                                }
                             }
                        })
                    }
                }
            }
            }
        })
    }

}
