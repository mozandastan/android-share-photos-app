package com.ozzystudio.sharephotoapp.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.ozzystudio.sharephotoapp.R

class UserActivity : AppCompatActivity() {

    private lateinit var auth : FirebaseAuth

    private lateinit var inEmail: EditText
    private lateinit var inPassword: EditText
    private lateinit var btnSingIn: EditText
    private lateinit var btnSingUp: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)

        auth = FirebaseAuth.getInstance()

        // Automatic Login
        val currentUser = auth.currentUser
        if(currentUser != null){
            val intent = Intent(this, FeedActivity::class.java)
            startActivity(intent)
            finish() // bu aktivitenin lifecycleını bitirmek için, finish kullanılınca ilgili activity backstage'den silinir, geri tuşu çalışmaz
        }

        inEmail = findViewById(R.id.in_email)
        inPassword = findViewById(R.id.in_password)
    }

    fun fncSignIn(view: View){
        //Layoutta onClicke direkt bu fonksiyon atanabiliyor.
        //println("SIGN IN")

        val email = inEmail.text.toString()
        val password = inPassword.text.toString()

        auth.signInWithEmailAndPassword(email,password).addOnCompleteListener { task ->
            if(task.isSuccessful){

                val currentUser = auth.currentUser
                Toast.makeText(applicationContext, "Hoşgeldin ${currentUser?.email}", Toast.LENGTH_LONG).show()

                val intent = Intent(this, FeedActivity::class.java)
                startActivity(intent)
                finish() // bu aktivitenin lifecycleını bitirmek için
            }
        }.addOnFailureListener { exception ->
            Toast.makeText(applicationContext, exception.localizedMessage, Toast.LENGTH_LONG).show()
        }
    }
    fun fncSignUp(view: View) {
        //println("SIGN UP")

        val email = inEmail.text.toString()
        val password = inPassword.text.toString()

        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val intent = Intent(this, FeedActivity::class.java)
                startActivity(intent)
                finish() // bu aktivitenin lifecycleını bitirmek için
            }
        }.addOnFailureListener { exception ->
            Toast.makeText(applicationContext, exception.localizedMessage, Toast.LENGTH_LONG).show()
        }
    }
}