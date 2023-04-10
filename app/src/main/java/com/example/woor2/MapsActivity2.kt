package com.example.woor2

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.example.woor2.databinding.ActivityMaps2Binding
import com.google.android.gms.maps.model.LatLng
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView
import java.util.*
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

        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        @SuppressLint("MissingPermission")
        val currentLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)

        if (currentLocation != null) {
            showCurrentLocation(currentLocation)
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

            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
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

        mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(curPoint.latitude, curPoint.longitude), true)
    }
}