package com.example.woor2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.woor2.databinding.ActivitySignupBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SignupActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.signupButton.setOnClickListener {
            Firebase.auth.createUserWithEmailAndPassword(binding.IDtext.text.toString(), binding.PWtext.text.toString())
            onBackPressed()
        }
    }
}