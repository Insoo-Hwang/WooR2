package com.example.woor2

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.woor2.databinding.ActivityMaps2Binding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import net.daum.mf.map.api.MapPoint
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

        var latitude = 0.0
        var longitude = 0.0
        var loc = ""

        binding.AddButton.visibility = View.INVISIBLE

        startTracking()
        Timer().schedule(2000) {
            stopTracking()
        }

        binding.searchBtn.setOnClickListener {
            val location = getLocationFromAddress (applicationContext, binding.editTextSearch.text.toString())

            if (location != null) {
                showCurrentLocation(location)
                latitude = location.latitude
                longitude = location.longitude
                loc = binding.editTextSearch.text.toString()
                binding.AddButton.visibility = View.VISIBLE
            }
        }

        binding.AddButton.setOnClickListener {
            val intent = Intent(this, AddingPlanActivity::class.java)

            intent.putExtra("location", loc)
            intent.putExtra("latitude", latitude)
            intent.putExtra("longitude", longitude)
            intent.putExtra("code", -1)
            intent.putExtra("mode", 1)

            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            setResult(Activity.RESULT_OK, intent)
            finish()
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

    private fun getLocationFromAddress(context: Context, address: String): Location? {
        val geocoder = Geocoder(context)
        val addresses: List<Address>?
        val resLocation = Location("")
        try {
            addresses = geocoder.getFromLocationName(address, 5)
            if (addresses == null || addresses.isEmpty()) {
                return null
            }
            val addressLoc: Address = addresses[0]
            resLocation.latitude = addressLoc.latitude
            resLocation.longitude = addressLoc.longitude
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return resLocation
    }

    private fun showCurrentLocation(location: Location) {
        val curPoint = LatLng(location.latitude, location.longitude)
        /*
        val msg = """
            Latitutde : ${curPoint.latitude}
            Longitude : ${curPoint.longitude}
            """.trimIndent()
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
         */

        mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(curPoint.latitude, curPoint.longitude), true);
    }
}