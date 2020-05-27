package com.example.myapps.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide

import com.example.myapps.R
import com.example.myapps.User
import com.example.myapps.activity.MainActivityCallback
import com.example.myapps.util.*
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_profile.*

/**
 * A simple [Fragment] subclass.
 */
class ProfileFragment : Fragment() {

    private lateinit var userId: String
    private lateinit var userDatabase: DatabaseReference
    private var callback: MainActivityCallback? = null

    fun setCallback(callback: MainActivityCallback)
    {
        this.callback = callback
        userId = callback.onGetUserId()
        userDatabase = callback.getUserDatabase().child(userId)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        profileLayout.setOnTouchListener { view, event -> true }

        populateInfo()
        photoIV.setOnClickListener { callback?.startActivityforPhoto() }

        applyBtn.setOnClickListener { onApply() }
        signoutBtn.setOnClickListener { callback?.onSignOut() }
    }
    fun populateInfo()
    {
        progressLayout.visibility = View.VISIBLE
        userDatabase.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                progressLayout.visibility = View.GONE
            }

            override fun onDataChange(p0: DataSnapshot) {
               if (isAdded)
               {
                   val user = p0.getValue(User::class.java)
                   nameET.setText(user?.name,TextView.BufferType.EDITABLE)
                   emailET.setText(user?.email,TextView.BufferType.EDITABLE)
                   ageET.setText(user?.age,TextView.BufferType.EDITABLE)
                   if (user?.gender == GENDER_MALE)
                   {
                       radioMale1.isChecked = true
                   }
                   if(user?.gender == GENDER_FEMALE)
                   {
                       radioFemale1.isChecked = true
                   }
                   if (user?.preferredGender == GENDER_MALE)
                   {
                       radioMale2.isChecked = true
                   }
                   if (user?.preferredGender == GENDER_FEMALE)
                   {
                       radioFemale2.isChecked = true
                   }
                   if (!user?.imageUrl.isNullOrEmpty())
                   {
                       populateImage(user?.imageUrl!!)
                   }
                   progressLayout.visibility = View.GONE
               }
            }

        })
    }
    fun onApply()
    {
        if (nameET.text.toString().isNullOrEmpty() || emailET.text.toString().isNullOrEmpty() ||
                rdGrup1.checkedRadioButtonId == -1 || rdGrup2.checkedRadioButtonId == -1)
        {
            Toast.makeText(context,"Please complete the field",Toast.LENGTH_SHORT).show()
        }
        else
        {
            val name = nameET.text.toString()
            val age = ageET.text.toString()
            val email = emailET.text.toString()
            val gender =
                if (radioMale1.isChecked) GENDER_MALE
            else GENDER_FEMALE
            val preferredGender =
                if (radioMale2.isChecked) GENDER_MALE
            else GENDER_FEMALE

            userDatabase.child(DATA_NAME).setValue(name)
            userDatabase.child(DATA_AGE).setValue(age)
            userDatabase.child(DATA_EMAIL).setValue(email)
            userDatabase.child(DATA_GENDER).setValue(gender)
            userDatabase.child(DATA_GENDER_PREFERENCE).setValue(preferredGender)

            callback?.profileComplete()
        }
    }
    fun updateImageUri(uri: String)
    {
        userDatabase.child(DATA_IMAGE_URL).setValue(uri)
        populateImage(uri)
    }
    fun populateImage(uri: String)
    {
        Glide.with(this)
            .load(uri)
            .into(photoIV)
    }
}
