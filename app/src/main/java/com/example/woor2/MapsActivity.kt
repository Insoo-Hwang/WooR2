package com.example.woor2

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.woor2.databinding.ActivityMapsBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse
import com.google.android.libraries.places.api.net.PlacesClient
import java.util.*


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var mPlacesClient: PlacesClient // place 진입점
    private lateinit var mFusedLocationProviderClient: FusedLocationProviderClient // Fused Location Provider 진입점

    private val mDefaultLocation = LatLng(37.5665, 126.9780) // 기본 좌표
    private val DEFAULT_ZOOM = 15f // 기본 줌
    private val PERMISSION_REQUEST_ACCESS_FINE_LOCATION = 1
    private var mLocationPermissionGranted = false

    //장치의 현재 위치
    private var mLastKnownLocation: Location? = null
    private var mCameraPosition: CameraPosition? = null

    //액티비티 상태 저장용 키
    private val KEY_CAMERA_POSITION = "camera_position"
    private val KEY_LOCATION = "location"

    //현재 장소를 선택하는데 사용되는 변수
    private val M_MAX_ENTRIES = 5
    private lateinit var mLikelyPlaceName: Array<String?>
    private lateinit var mLikelyPlaceAddress: Array<String?>
    private lateinit var mLikelyPlaceAttributions: Array<List<Any?>?>
    private lateinit var mLikelyPlaceLatLng: Array<LatLng?>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState != null) { //저장된 인스턴스 상태에서 위치 및 카메라 위치 받기
            mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION)
            mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION)
        }

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val e = intent.extras?:return
        val intentName = e.getString("name")
        val intentDate = e.getString("date")
        val intentPublic = e.getBoolean("public")
        var latitude = e.getDouble("latitude")
        var longitude = e.getDouble("longitude")
        var loc = e.getString("location")

        // PlaceClient 구성
        Places.initialize(getApplication(), getString(R.string.google_api_key))
        mPlacesClient = Places.createClient(this)

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this) // FusedLocationProviderClient 구성

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        binding.confirmButton.visibility = View.INVISIBLE

        binding.searchButton.setOnClickListener {
            if (binding.editText.text.toString().isNotEmpty()) {
                val location = getLocationFromAddress (applicationContext, binding.editText.text.toString())

                binding.confirmButton.visibility = View.INVISIBLE

                if (location != null) {
                    showCurrentLocation(location)
                    latitude = location.latitude
                    longitude = location.longitude
                    loc = binding.editText.text.toString()
                    binding.confirmButton.visibility = View.VISIBLE
                }
            }
        }

        binding.confirmButton.setOnClickListener {
            val intent = Intent(this, AddingPlanActivity::class.java)

            intent.putExtra("name", intentName)
            intent.putExtra("date", intentDate)
            intent.putExtra("public", intentPublic)
            intent.putExtra("location", loc)
            intent.putExtra("latitude", latitude)
            intent.putExtra("longitude", longitude)

            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            setResult(Activity.RESULT_OK, intent)
            startActivity(intent)
        }
    }

    @Suppress("SENSELESS_COMPARISON")
    override fun onSaveInstanceState(outState: Bundle) {
        if (mMap != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, mMap.getCameraPosition())
            outState.putParcelable(KEY_LOCATION, mLastKnownLocation)
            super.onSaveInstanceState(outState)
        }
    }

    // 옵션 메뉴 설정
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.current_place_menu, menu)
        return true
    }

    // 메뉴 옵션 클릭 처리
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.option_get_place) {
            showCurrentPlace()
        }
        return super.onOptionsItemSelected(item)
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
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(curPoint, DEFAULT_ZOOM))

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

        // 정보창 내용에서 여러 줄의 텍스트를 사용하기 위해 사용자 정의 정보창 어댑터 사용
        mMap.setInfoWindowAdapter(object : GoogleMap.InfoWindowAdapter {

            // getInfoContents()가 호출되도록 null 반환
            override fun getInfoWindow(arg0: Marker): View? {
                return null
            }

            override fun getInfoContents(p0: Marker): View? {

                // 정보창, 제목 및 스니펫의 레이아웃 inflate
                val infoWindow = layoutInflater.inflate(
                    R.layout.custom_info_contents,
                    findViewById<FrameLayout>(R.id.map), false
                )

                val title = infoWindow.findViewById<TextView>(R.id.title)
                title.text = p0.title

                val snippet = infoWindow.findViewById<TextView>(R.id.snippet)
                snippet.text = p0.snippet

                return infoWindow
            }
        })

        // 유저권한 요청
        getLocationPermission()

        // 내 위치 레이어 및 관련 컨트롤 구현
        updateLocationUI()

        // 장치의 현재 위치로 지도 위치 설정
        getDeviceLocation()

        /*
        mMap.addMarker(MarkerOptions().position(mDefaultLocation).title("Marker in Default"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(mDefaultLocation))
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, 12f))
        */
    }

    // 위치 권한 요청 결과 처리
    private fun getDeviceLocation() {
        // 가장 최근 기계 위치 열기, null 일 수 있음, 위치가 사용 불가한 경우
        try {
            if (mLocationPermissionGranted) {
                val locationResult = mFusedLocationProviderClient.lastLocation
                locationResult.addOnCompleteListener(this) { task ->
                    //작업이 성공적으로 끝났을 경우
                    if (task.isSuccessful) {
                        //맵의 카메라 위치를 현재 위치로 설정
                        mLastKnownLocation = task.getResult()
                        if (mLastKnownLocation != null) {
                            mMap.moveCamera(
                                CameraUpdateFactory.newLatLngZoom(
                                    LatLng(
                                        mLastKnownLocation!!.latitude,
                                        mLastKnownLocation!!.longitude
                                    ), DEFAULT_ZOOM
                                )
                            )
                        } else {

                            //현재 위치를 못받으면 Log 띄움, 디폴트 위치로 이동하고 로케이션 버튼 불가
                            Log.d(TAG, "Current Location is null. Using defaults.")
                            Log.e(TAG, "Exception: %s", task.exception)
                            mMap.moveCamera(
                                CameraUpdateFactory.newLatLngZoom(
                                    mDefaultLocation, DEFAULT_ZOOM
                                )
                            )
                            mMap.uiSettings.isMyLocationButtonEnabled = false
                        }
                    }
                }
            }
        }

        // 보안 예외 발생
        catch (e: SecurityException) {
            Log.e("Exception %s", e.message!!)
        }
    }

    // 장치 위치 사용 권한 요청
    private fun getLocationPermission() {

        // 권한을 요청, 결과는 콜백으로 조정됨

        // 권한을 얻은 경우
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mLocationPermissionGranted = true
        }

        // 권한 얻기에 실패한 경우
        else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSION_REQUEST_ACCESS_FINE_LOCATION
            )
        }
    }

    // 위치 권한 요청 결과 처리
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        mLocationPermissionGranted = false
        when(requestCode) {
            PERMISSION_REQUEST_ACCESS_FINE_LOCATION -> {

                // 만약 요청이 취소되면 결과 배열에는 값이 없다
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true
                }
            }
        }
        updateLocationUI()
    }

    // 사용자에게 가능한 장소 목록에서 현재 장소를 선택하라는 메시지를 표시하고 사용자가 위치 권한을 부여한 경우 지도에 현재 장소 표시
    @Suppress("SENSELESS_COMPARISON")
    private fun showCurrentPlace() {
        if (mMap == null) return

        //위치 권한을 받는데 성공한 경우
        if (mLocationPermissionGranted) {

            // 반환할 데이터 형식을 정의하려면 필드 사용
            val placeField: List<Place.Field> = Arrays.asList(
                Place.Field.NAME, Place.Field.ADDRESS,
                Place.Field.LAT_LNG
            )

            // 빌더를 사용하여 FindCurrentPlaceRequest를 작성
            val request = FindCurrentPlaceRequest.newInstance(placeField)

            // 장치의 현재 위치에 가정 적합한 비즈니스 및 기타 관심 지점을 검색
            @SuppressWarnings("MissingPermission")
            val placeResult = mPlacesClient.findCurrentPlace(request)
            placeResult.addOnCompleteListener(object :
                OnCompleteListener<FindCurrentPlaceResponse> {
                override fun onComplete(task: Task<FindCurrentPlaceResponse>) {
                    if (task.isSuccessful && task.result != null) {
                        val likelyPlace = task.getResult()

                        //5개 미만의 항목이 반환되는 경우와 아닌 경우 처리
                        var count: Int
                        if (likelyPlace!!.placeLikelihoods.size < M_MAX_ENTRIES) {
                            count = likelyPlace.placeLikelihoods.size
                        } else {
                            count = M_MAX_ENTRIES
                        }

                        var i = 0
                        mLikelyPlaceName = arrayOfNulls(count)
                        mLikelyPlaceAddress = arrayOfNulls(count)
                        mLikelyPlaceAttributions = arrayOfNulls(count)
                        mLikelyPlaceLatLng = arrayOfNulls(count)

                        for (placeLikelihood in likelyPlace.placeLikelihoods) {

                            //가능성이 높은 장소들의 리스트 만들기, 유저에게 보여주기 위함
                            mLikelyPlaceName[i] = placeLikelihood.place.name
                            mLikelyPlaceAddress[i] = placeLikelihood.place.address
                            mLikelyPlaceAttributions[i] = placeLikelihood.place.attributions
                            mLikelyPlaceLatLng[i] = placeLikelihood.place.latLng

                            i++

                            if (i > (count - 1)) {
                                break
                            }
                        }

                        //사용자에게 보여줄 수 있는 장소 목록 작성, 선택된 장소에 마커
                        this@MapsActivity.openPlacesDialog()
                    } else {
                        Log.e(TAG, "Exception: %s", task.exception)
                    }
                }
                })
        } else {

            // 권한을 주지 않은 유저
            Log.i(TAG, "위치 권한을 얻지 못한 유저")

            // 유저가 선택할 수 있는 장소가 없기에 기본 마커 추가
            mMap.addMarker(
                MarkerOptions()
                    .title(getString(R.string.default_info_title))
                    .position(mDefaultLocation)
                    .snippet(getString(R.string.default_info_snippet))
            )

            // 유저 권한 요청
            getLocationPermission()
        }
    }

    // 사용자가 가능한 장소 목록에서 장소를 선택할 수 있는 양식을 표시
    private fun openPlacesDialog() {

        // 요청, 유저에게 현재 위치를 선택하도록
        val listener = DialogInterface.OnClickListener { dialog, which ->

            // which 인수는 선택된 학목의 위치를 포함
            var markerLatLng = mLikelyPlaceLatLng[which]!!
            var markerSnippet = mLikelyPlaceAddress[which]!!
            if (mLikelyPlaceAttributions[which] != null) {
                markerSnippet = """
                    $markerSnippet
                    ${mLikelyPlaceAttributions[which]}
                """.trimIndent()
            }

            // 선택된 장소에 정보창과 함께 마커 추가, 해당 장소에 대한 정보 표시
            mMap.addMarker(
                MarkerOptions()
                    .title(mLikelyPlaceName[which])
                    .position(markerLatLng)
                    .snippet(markerSnippet)
            )

            // 맵의 카메라 마커 장소로 위치시키기
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(markerLatLng, DEFAULT_ZOOM))
        }

        // 대화상자 화면에 나타내기
        val dialog = AlertDialog.Builder(this)
            .setTitle(R.string.pick_place)
            .setItems(mLikelyPlaceName, listener)
            .show()
    }

    // 사용자가 위치 권한을 부여했는지 여부에 따라 지도의 UI 설정을 업데이트
    @Suppress("SENSELESS_COPARISON", "NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    private fun updateLocationUI() {
        if (mMap == null) {
            return
        }
        try {

            // 권한이 있는 경우
            if (mLocationPermissionGranted) {
                mMap.isMyLocationEnabled=true
                mMap.uiSettings.isMyLocationButtonEnabled=true
            }

            // 권한이 없는 경우
            else {
                mMap.isMyLocationEnabled=false
                mMap.uiSettings.isMyLocationButtonEnabled=false
                mLastKnownLocation=null

                // 권한 신청
                getLocationPermission()
            }
        } catch (e:SecurityException){
            e.message?.let { Log.e("Exception: %s", it) }
        }
    }
}