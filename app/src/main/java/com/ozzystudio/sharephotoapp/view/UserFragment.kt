package com.ozzystudio.sharephotoapp.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.ozzystudio.sharephotoapp.R
import com.ozzystudio.sharephotoapp.databinding.FragmentUserBinding

class UserFragment : Fragment() {

    private lateinit var binding: FragmentUserBinding
    private lateinit var auth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = Firebase.auth
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUserBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnLogin.setOnClickListener { fncSignIn() }
        binding.btnSignup.setOnClickListener { fncSignUp() }

        // Auto Login
        val currentUser = auth.currentUser
        if(currentUser != null) {
            goToFeedScreen()
        }
    }
    private fun fncSignIn(){

        val email = binding.inEmail.text.toString()
        val password = binding.inPassword.text.toString()

        if(email.isNotEmpty() && password.isNotEmpty()){
            auth.signInWithEmailAndPassword(email,password).addOnSuccessListener { task ->

                goToFeedScreen()

            }.addOnFailureListener { exception ->
                Toast.makeText(requireContext(), exception.localizedMessage, Toast.LENGTH_LONG).show()
            }
        }

    }
    private fun fncSignUp() {

        val email = binding.inEmail.text.toString()
        val password = binding.inPassword.text.toString()

        if(email.isNotEmpty() && password.isNotEmpty()){

            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    goToFeedScreen()
                }
            }.addOnFailureListener { exception ->
                Toast.makeText(requireContext(), exception.localizedMessage, Toast.LENGTH_LONG).show()
            }
        }
    }
    private fun goToFeedScreen(){
        findNavController().navigate(R.id.action_userFragment_to_feedFragment)
    }
}