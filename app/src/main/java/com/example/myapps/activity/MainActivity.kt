package com.example.myapps.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.myapps.R
import com.example.myapps.fragments.MatchesFragment
import com.example.myapps.fragments.ProfileFragment
import com.example.myapps.fragments.SwipeFragment
import com.example.myapps.util.DATA_CHATS
import com.example.myapps.util.DATA_USERS
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.lorentzos.flingswipe.SwipeFlingAdapterView
import kotlinx.android.synthetic.main.activity_main.*
import java.io.ByteArrayOutputStream
import java.io.IOException

const val REQ_CODE_PHOTO = 1234

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity(),MainActivityCallback {

    private val firebaseAuth = FirebaseAuth.getInstance()
    private val userId = firebaseAuth.currentUser?.uid
    private lateinit var userDatabase: DatabaseReference
    private lateinit var chatDatabase: DatabaseReference

    private var profileFragment: ProfileFragment? = null
    private var swipeFragment: SwipeFragment? = null
    private var matchesFragment: MatchesFragment? = null

    private var profileTab: TabLayout.Tab? = null
    private var swipeTab: TabLayout.Tab? = null
    private var matchTab: TabLayout.Tab? = null

    private var resultImageUrl: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (userId.isNullOrEmpty()) {
            onSignOut()
        }
        userDatabase = FirebaseDatabase.getInstance().reference.child(DATA_USERS)
        chatDatabase = FirebaseDatabase.getInstance().reference.child(DATA_CHATS)

        profileTab = navTabs.newTab()
        swipeTab = navTabs.newTab()
        matchTab = navTabs.newTab()

        profileTab?.icon = ContextCompat.getDrawable(this,R.drawable.tab_profile)
        swipeTab?.icon = ContextCompat.getDrawable(this,R.drawable.tab_swipe)
        matchTab?.icon = ContextCompat.getDrawable(this,R.drawable.tab_matches)

        navTabs.addTab(profileTab!!)
        navTabs.addTab(swipeTab!!)
        navTabs.addTab(matchTab!!)

        navTabs.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener{
            override fun onTabReselected(tab: TabLayout.Tab?) {
             onTabSelected(tab)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                when(tab) {
                    profileTab -> {
                        if(profileFragment == null)
                        {
                            profileFragment = ProfileFragment()
                            profileFragment!!.setCallback(this@MainActivity)
                        }
                        replaceFragment(profileFragment!!)
                    }
                    swipeTab -> {
                        if (swipeFragment == null)
                        {
                            swipeFragment = SwipeFragment()
                            swipeFragment!!.setCallback(this@MainActivity)
                        }
                        replaceFragment(swipeFragment!!)
                    }
                    matchTab -> {
                        if (matchesFragment == null)
                        {
                            matchesFragment = MatchesFragment()
                            matchesFragment!!.setCallback(this@MainActivity)
                        }
                        replaceFragment(matchesFragment!!)
                    }
                }
            }
        })
        profileTab?.select()
    }
    fun replaceFragment(fragment:Fragment)
    {
        supportFragmentManager.beginTransaction()
        .replace(R.id.fragmentContainer,fragment)
        .commit()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == REQ_CODE_PHOTO)
        {
            resultImageUrl = data?.data
            storeImage()
        }
    }

    fun storeImage() {
        if(resultImageUrl != null && userId != null) {
            val filePath = FirebaseStorage.getInstance().reference.child("profileImage").child(userId)
            var bitmap: Bitmap? = null
            try {
                bitmap = MediaStore.Images.Media.getBitmap(application.contentResolver, resultImageUrl)
            } catch (e: IOException) {
                e.printStackTrace()
            }

            val baos = ByteArrayOutputStream()
            bitmap?.compress(Bitmap.CompressFormat.JPEG, 20, baos)
            val data = baos.toByteArray()

            val uploadTask = filePath.putBytes(data)
            uploadTask.addOnFailureListener { e -> e.printStackTrace() }
            uploadTask.addOnSuccessListener { taskSnapshot ->
                filePath.downloadUrl
                    .addOnSuccessListener { uri ->
                        profileFragment?.updateImageUri(uri.toString())
                    }
                    .addOnFailureListener { e -> e.printStackTrace() }
            }
        }
    }
//    fun storeImg()
//    {
//        if(resultImageUrl != null && userId != null) {
//            val filePath = FirebaseStorage.getInstance().reference.child("profileImage").child(userId)
//            var bitmap: Bitmap? = null
//            try {
//                bitmap = MediaStore.Images.Media.getBitmap(application.contentResolver, resultImageUrl)
//            } catch (e: IOException) {
//                e.printStackTrace()
//            }
//
//            val baos = ByteArrayOutputStream()
//            bitmap?.compress(Bitmap.CompressFormat.JPEG, 20, baos)
//            val data = baos.toByteArray()
//
//            val uploadTask = filePath.putBytes(data)
//            uploadTask.addOnFailureListener { e -> e.printStackTrace() }
//            uploadTask.addOnSuccessListener { taskSnapshot ->
//                filePath.downloadUrl
//                    .addOnSuccessListener { uri ->
//                        profileFragment?.updateImageUri(uri.toString())
//                    }
//                    .addOnFailureListener { e -> e.printStackTrace() }
//            }
//        }
//    }

    override fun onSignOut() {
     firebaseAuth.signOut()
        startActivity(StartUpActivity.newIntent(this))
        finish()
    }

    override fun onGetUserId():String = userId!!

    override fun getUserDatabase(): DatabaseReference = userDatabase
    override fun getChatDatabase(): DatabaseReference = chatDatabase

    override fun profileComplete() {
        swipeTab?.select()
    }

    override fun startActivityforPhoto() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, REQ_CODE_PHOTO)
    }
    companion object {
        fun newIntent(context: Context?) = Intent(context,MainActivity::class.java)

    }
}
