package com.example.woor2

import android.os.Bundle
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.example.woor2.databinding.ActivityMaps2Binding
import net.daum.mf.map.api.MapView
import java.util.Timer
import kotlin.concurrent.schedule


class MapsActivity2: AppCompatActivity() {

    private lateinit var mapView: MapView
    private lateinit var binding: ActivityMaps2Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMaps2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        mapView = MapView(this)

        val mapViewContainer = binding.mapView as ViewGroup
        mapViewContainer.addView(mapView)

        startTracking()
        Timer().schedule(1000) {
            stopTracking()
        }
    }

    // 위치추적 시작
    private fun startTracking() {
        mapView.currentLocationTrackingMode = MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading
    }

    // 위치추적 중지
    private fun stopTracking() {
        mapView.currentLocationTrackingMode = MapView.CurrentLocationTrackingMode.TrackingModeOff
    }
}