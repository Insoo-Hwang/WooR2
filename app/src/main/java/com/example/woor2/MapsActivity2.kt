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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.woor2.databinding.ActivityMaps2Binding
import com.google.android.gms.maps.model.LatLng
import net.daum.mf.map.api.*
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

        val e = intent.extras?:return
        val array = e.getSerializable("array")
        val mode = e.getBoolean("mode")

        if (mode) {
            binding = ActivityMaps2Binding.inflate(layoutInflater)
            setContentView(binding.root)

            mapView = MapView(this)
            val mapViewContainer = binding.mapView as ViewGroup
            mapViewContainer.addView(mapView)

            drawPolyLine(array as ArrayList<Item4>)

            binding.editTextSearch.visibility = View.INVISIBLE

            binding.searchBtn.setImageResource(R.drawable.close)
            binding.searchBtn.setOnClickListener {
                finish()
            }
        }
        else {
            binding = ActivityMaps2Binding.inflate(layoutInflater)
            setContentView(binding.root)

            mapView = MapView(this)
            mapView.setCalloutBalloonAdapter(CustomBalloonAdapter(layoutInflater))  // 커스텀 말풍선 등록
            mapView.setPOIItemEventListener(this)  // 마커 클릭 이벤트 리스너 등록

            val mapViewContainer = binding.mapView as ViewGroup
            mapViewContainer.addView(mapView)

            var latitude = 0.0
            var longitude = 0.0
            var loc = ""

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

            binding.searchBtn.setImageResource(R.drawable.search)
            binding.searchBtn.setOnClickListener {
                val location =
                    getLocationFromAddress(
                        applicationContext,
                        binding.editTextSearch.text.toString()
                    )

                if (location != null) {
                    showCurrentLocation(location)
                    latitude = location.latitude
                    longitude = location.longitude
                    loc = binding.editTextSearch.text.toString()
                    searchKeyword(loc)
                }
            }
            /*
        binding.AddButton.setOnClickListener {
            val intent = Intent(this, AddingPlanActivity::class.java)

            intent.putExtra("location", loc)
            intent.putExtra("latitude", latitude)
            intent.putExtra("longitude", longitude)

            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            setResult(RESULT_OK, intent)
            finish()
        }
         */
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

    // 커스텀 말풍선 클래스
    class CustomBalloonAdapter(inflater: LayoutInflater): CalloutBalloonAdapter {
        val mCalloutBalloon: View = inflater.inflate(R.layout.balloon_layout, null)
        val name: TextView = mCalloutBalloon.findViewById(R.id.ball_tv_name)
        val address: TextView = mCalloutBalloon.findViewById(R.id.ball_tv_address)
        val add: TextView = mCalloutBalloon.findViewById(R.id.textView)

        override fun getCalloutBalloon(poiItem: MapPOIItem?): View {
            // 마커 클릭 시 나오는 말풍선
            name.text = poiItem?.itemName   // 해당 마커의 정보 이용 가능
            //address.text = "getCalloutBalloon"
            address.text = ""
            add.text = "클릭해서 추가"
            return mCalloutBalloon
        }

        override fun getPressedCalloutBalloon(poiItem: MapPOIItem?): View {
            // 말풍선 클릭 시
            //address.text = "getPressedCalloutBalloon"
            address.text = ""
            add.text = "클릭해서 추가"
            return mCalloutBalloon
        }
    }

    override fun onPOIItemSelected(mapView: MapView?, poiItem: MapPOIItem?) {
        // Handle POI item selection event
    }

    override fun onCalloutBalloonOfPOIItemTouched(mapView: MapView?, poiItem: MapPOIItem?, calloutBalloonButtonType: MapPOIItem.CalloutBalloonButtonType?) {
        // Handle callout balloon touch event
        val builder = AlertDialog.Builder(this)
        val itemList = arrayOf("장소 추가", "취소")
        builder.setTitle("${poiItem?.itemName}")
        builder.setItems(itemList) { dialog, which ->
            when(which) {
                0 -> {
                    val intent = Intent(this, AddingPlanActivity::class.java)
                    if (poiItem != null) {
                        intent.putExtra("location", poiItem.itemName)
                        getLocationFromAddress(applicationContext,poiItem.itemName.toString())?.let { intent.putExtra("latitude", it.latitude) }
                        getLocationFromAddress(applicationContext,poiItem.itemName.toString())?.let { intent.putExtra("longitude", it.longitude) }
                    }
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    setResult(RESULT_OK, intent)
                    finish()
                }
                1 -> dialog.dismiss()   // 대화상자 닫기
            }
        }
        builder.show()
    }

    override fun onCalloutBalloonOfPOIItemTouched(mapView: MapView?, poiItem: MapPOIItem?) {
        // Handle callout balloon touch event (deprecated)
    }

    override fun onDraggablePOIItemMoved(mapView: MapView?, poiItem: MapPOIItem?, mapPoint: MapPoint?) {
        // Handle draggable POI item move event
    }

    private fun drawPolyLine(arrayList: ArrayList<Item4>) {
        val polyline = MapPolyline()
        val size = arrayList.size

        for (i in 0 until size) {
            val num = i+1
            polyline.addPoint(MapPoint.mapPointWithGeoCoord(arrayList[i].latitude,arrayList[i].longitude))
            val point = MapPOIItem()
            point.apply {
                itemName = "$num. " + arrayList[i].location
                mapPoint = MapPoint.mapPointWithGeoCoord(arrayList[i].latitude,
                    arrayList[i].longitude)
                markerType = MapPOIItem.MarkerType.BluePin
                selectedMarkerType = MapPOIItem.MarkerType.RedPin
            }
            mapView.addPOIItem(point)
        }

        mapView.addPolyline(polyline)
        val mapPointBounds = MapPointBounds(polyline.mapPoints)
        val padding = 100 // px
        mapView.moveCamera(CameraUpdateFactory.newMapPointBounds(mapPointBounds, padding))
    }
}