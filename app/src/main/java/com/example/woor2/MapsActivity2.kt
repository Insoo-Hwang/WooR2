package com.example.woor2

import android.R
import android.os.Bundle
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.example.woor2.databinding.ActivityMaps2Binding
import net.daum.mf.map.api.MapView


class MapsActivity2: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMaps2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        val mapView = MapView(this)

        val mapViewContainer = binding.mapView as ViewGroup
        mapViewContainer.addView(mapView)
    }
}