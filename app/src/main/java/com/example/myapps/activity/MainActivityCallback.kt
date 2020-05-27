package com.example.myapps.activity

import com.google.firebase.database.DatabaseReference

interface MainActivityCallback {

    fun onSignOut()
    fun onGetUserId():String
    fun getUserDatabase():DatabaseReference
    fun profileComplete()
    fun startActivityforPhoto()
    fun getChatDatabase():DatabaseReference
}