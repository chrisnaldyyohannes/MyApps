package com.example.myapps.activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.myapps.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firebaseAuthListener = FirebaseAuth.AuthStateListener {
        val user = firebaseAuth.currentUser
        if (user != null) {
            startActivity(MainActivity.newIntent(this))
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
    }

    override fun onStart() {
        super.onStart()
        firebaseAuth.addAuthStateListener(firebaseAuthListener)
    }

    override fun onStop() {
        super.onStop()
        firebaseAuth.removeAuthStateListener(firebaseAuthListener)
    }

    fun onLogin(v: View) {
        if (!edtEmail.text.toString().isNullOrEmpty() && !edtPassword.text.toString().isNullOrEmpty()) {
            firebaseAuth.signInWithEmailAndPassword(edtEmail.text.toString(),edtPassword.text.toString())
                .addOnCompleteListener { task ->
                    if (!task.isSuccessful)
                    {
                        Toast.makeText(this,"Login Fail ${task.exception?.localizedMessage}",Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
    companion object {
        fun newIntent(context: Context?) = Intent(context,LoginActivity::class.java)

    }
}
