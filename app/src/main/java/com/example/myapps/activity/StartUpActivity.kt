package com.example.myapps.activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.myapps.R

class StartUpActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start_up)
    }

    fun onLogin(v: View) {
        startActivity(LoginActivity.newIntent(this))
    }

    fun onSignup(v: View) {
        startActivity(SignUpActivity.newIntent(this))
    }
    companion object {
        fun newIntent(context: Context?) = Intent(context,MainActivity::class.java)

    }
}
