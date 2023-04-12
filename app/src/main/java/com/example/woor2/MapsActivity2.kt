package com.example.woor2

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.woor2.databinding.ActivityMaps2Binding
import com.google.android.gms.maps.model.LatLng
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MapsActivity2: AppCompatActivity(), MapView.POIItemEventListener {

    private lateinit var mapView:MapView
    private lateinit var binding: ActivityMaps2Binding

    companion object {
        const val BASE_URL = "https://dapi.kakao.com/"
        const val API_KEY = "KakaoAK 6e661352cd0f7b5246437de8952903a1"  // REST API 키
    }

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



        binding.currentLocationFAB.setOnClickListener {
            if (currentLocation != null) {
                showCurrentLocation(currentLocation)
            }
        }

        binding.searchBtn.setOnClickListener {
            val location =
                getLocationFromAddress(applicationContext, binding.editTextSearch.text.toString())

            if (location != null) {
                showCurrentLocation(location)
                latitude = location.latitude
                longitude = location.longitude
                loc = binding.editTextSearch.text.toString()
                binding.AddButton.visibility = View.VISIBLE
                searchKeyword(loc)
            }
        }

        binding.AddButton.setOnClickListener {
            val intent = Intent(this, AddingPlanActivity::class.java)

            intent.putExtra("location", loc)
            intent.putExtra("latitude", latitude)
            intent.putExtra("longitude", longitude)

            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            setResult(RESULT_OK, intent)
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

        mapView.setMapCenterPoint(
            MapPoint.mapPointWithGeoCoord(
                curPoint.latitude,
                curPoint.longitude
            ), true
        )
    }

    // 키워드 검색 함수
    private fun searchKeyword(keyword: String) {
        val retrofit = Retrofit.Builder()          // Retrofit 구성
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(KakaoAPI::class.java)            // 통신 인터페이스를 객체로 생성
        val call = api.getSearchKeyword(API_KEY, keyword)    // 검색 조건 입력

        // API 서버에 요청
        call.enqueue(object: Callback<ResultSearchKeyword> {
            override fun onResponse(call: Call<ResultSearchKeyword>, response: Response<ResultSearchKeyword>) {
                // 통신 성공
                addMarkers(response.body())
            }

            override fun onFailure(call: Call<ResultSearchKeyword>, t: Throwable) {
                // 통신 실패
                Log.w("LocalSearch", "통신 실패: ${t.message}")
            }
        })
    }

    // 검색 결과 처리 함수
    private fun addMarkers(searchResult: ResultSearchKeyword?) {
        if (!searchResult?.documents.isNullOrEmpty()) {
            // 검색 결과 있음
            mapView.removeAllPOIItems() // 지도의 마커 모두 제거
            for (document in searchResult!!.documents) {
                // 결과를 리사이클러 뷰에 추가

                // 지도에 마커 추가
                val point = MapPOIItem()
                point.apply {
                    itemName = document.place_name
                    mapPoint = MapPoint.mapPointWithGeoCoord(document.y.toDouble(),
                        document.x.toDouble())
                    markerType = MapPOIItem.MarkerType.BluePin
                    selectedMarkerType = MapPOIItem.MarkerType.RedPin
                }
                mapView.addPOIItem(point)
            }
        } else {
            // 검색 결과 없음
            Toast.makeText(this, "검색 결과가 없습니다", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onPOIItemSelected(mapView: MapView?, poiItem: MapPOIItem?) {
        // Handle POI item selection event
    }

    override fun onCalloutBalloonOfPOIItemTouched(mapView: MapView?, poiItem: MapPOIItem?, calloutBalloonButtonType: MapPOIItem.CalloutBalloonButtonType?) {
        // Handle callout balloon touch event
    }

    override fun onCalloutBalloonOfPOIItemTouched(mapView: MapView?, poiItem: MapPOIItem?) {
        // Handle callout balloon touch event (deprecated)
    }

    override fun onDraggablePOIItemMoved(mapView: MapView?, poiItem: MapPOIItem?, mapPoint: MapPoint?) {
        // Handle draggable POI item move event
    }
}