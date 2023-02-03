package com.example.woor2

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.woor2.databinding.ActivityAddingPlanBinding

class AddingPlanActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityAddingPlanBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}