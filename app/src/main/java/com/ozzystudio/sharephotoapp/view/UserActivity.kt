package com.ozzystudio.sharephotoapp.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.ozzystudio.sharephotoapp.databinding.ActivityUserBinding

class UserActivity : AppCompatActivity() {

    private lateinit var auth : FirebaseAuth
    private lateinit var binding: ActivityUserBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_user)
        binding = ActivityUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        // Automatic Login
        val currentUser = auth.currentUser
        if(currentUser != null) {
            val intent = Intent(this, FeedActivity::class.java)
            startActivity(intent)
            finish()
            //To end the lifecycle of this activity, when finish is used,
            // the relevant activity will be deleted from the backstage and the back button will not work.
        }
    }

    fun fncSignIn(view: View){
        //This function has assigned directly to onClick in the layout.

        val email = binding.inEmail.text.toString()
        val password = binding.inPassword.text.toString()

        auth.signInWithEmailAndPassword(email,password).addOnCompleteListener { task ->
            if(task.isSuccessful){

                val currentUser = auth.currentUser
                Toast.makeText(applicationContext, "Hoşgeldin ${currentUser?.email}", Toast.LENGTH_LONG).show()

                val intent = Intent(this, FeedActivity::class.java)
                startActivity(intent)
                finish() // To finish the lifecycle of this activity
            }
        }.addOnFailureListener { exception ->
            Toast.makeText(applicationContext, exception.localizedMessage, Toast.LENGTH_LONG).show()
        }
    }
    fun fncSignUp(view: View) {
        //This function has assigned directly to onClick in the layout.

        val email = binding.inEmail.text.toString()
        val password = binding.inPassword.text.toString()

        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val intent = Intent(this, FeedActivity::class.java)
                startActivity(intent)
                finish() // To finish the lifecycle of this activity
            }
        }.addOnFailureListener { exception ->
            Toast.makeText(applicationContext, exception.localizedMessage, Toast.LENGTH_LONG).show()
        }
    }
}