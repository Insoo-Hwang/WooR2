package com.example.woor2

import android.content.Context
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.woor2.databinding.ActivityMapsBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var latitude = 0.0
        var longitude = 0.0

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.confirmButton.visibility = View.INVISIBLE

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        binding.searchButton.setOnClickListener {
            if (binding.editText.text.toString().isNotEmpty()) {
                val location = getLocationFromAddress (applicationContext, binding.editText.text.toString());

                binding.confirmButton.visibility = View.INVISIBLE

                if (location != null) {
                    showCurrentLocation(location)
                    latitude = location.latitude
                    longitude = location.longitude
                    binding.confirmButton.visibility = View.VISIBLE
                };
            }
        }

        binding.confirmButton.setOnClickListener {
            val intent = Intent(this, AddingPlanActivity::class.java)
            val loc = binding.editText.text.toString()

            intent.putExtra("location", loc)
            intent.putExtra("latitude", latitude)
            intent.putExtra("longitude", longitude)
            startActivity(intent)
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
        val msg = """
            Latitutde : ${curPoint.latitude}
            Longitude : ${curPoint.longitude}
            """.trimIndent()
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()

        //화면 확대, 숫자가 클수록 확대
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(curPoint, 15f))

        /*
        //마커 찍기 - 필요 없
        val targetLocation = Location("")
        targetLocation.latitude = 37.4937
        targetLocation.longitude = 127.0643
        showMyMarker(targetLocation)
        */
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap


        val korea = LatLng(37.5665, 126.9780)
        mMap.addMarker(MarkerOptions().position(korea).title("Marker in Korea"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(korea))
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(korea, 12f))
    }
}