package com.example.woor2

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.woor2.databinding.ActivityAddLocationBinding

class AddLocationActivity : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        val binding = ActivityAddLocationBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}