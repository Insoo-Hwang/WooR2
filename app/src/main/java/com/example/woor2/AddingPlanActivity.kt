package com.example.woor2

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.woor2.databinding.ActivityAddingPlanBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson

class AddingPlanActivity: AppCompatActivity() {

    private val viewModel by viewModels<AddPlanViewModel>()
    private lateinit var binding: ActivityAddingPlanBinding
    private var latitude = 0.0
    private var longitude = 0.0
    private var mode = -1
    private val PERMISSION_REQUEST_ACCESS_FINE_LOCATION = 1
    private val PERMISSION_REQUEST_ACCESS_COARSE_LOCATION = 1
    private var mLocationPermissionGranted = false
    private var mLocationPermissionGranted2 = false
    private lateinit var dialog: CustomDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddingPlanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val e = intent.extras?:return
        val code = e.getString("code")
        mode = e.getInt("mode")
        var copy = false

        val adapter = AddPlanAdapter(viewModel)
        binding.addplanrecycleView.adapter = adapter
        binding.addplanrecycleView.layoutManager = LinearLayoutManager(this)
        binding.addplanrecycleView.setHasFixedSize(true)

        val db : FirebaseFirestore = Firebase.firestore
        val schedulesRef = db.collection("schedules")
        if(mode != 1) {
            db.collection("schedules").document(code.toString()).get().addOnSuccessListener {
                copy = it["copy"] as Boolean
                binding.NameText.setText(it["title"].toString())
                if(mode < 2)
                    binding.DateText.setText(it["date"].toString())
                if(mode == 2 || mode == 3 || copy) {
                    binding.PublicCheck.isEnabled = false
                    binding.PublicCheck.isChecked = false
                    copy = true
                }
                else
                    binding.PublicCheck.isChecked = it["public"] as Boolean
                val gson = Gson()
                val jsonArray = gson.fromJson(it["items"].toString(), Array<LocData>::class.java)
                jsonArray.forEach {
                    viewModel.addItem(Item4(it.location.replace("_", " "), it.latitude.toDouble(), it.longitude.toDouble()))
                }
            }
        }

        viewModel.itemsListData.observe(this){
            adapter.notifyDataSetChanged()
        }

        dialog = CustomDialog(this)

        binding.AddPlanButton.setOnClickListener {
            if(binding.LocationTextview.text.toString() == ""){
                Toast.makeText(this, "장소를 입력해주세요.", Toast.LENGTH_SHORT).show()
            }
            else {
                viewModel.addItem(Item4(binding.LocationTextview.text.toString(), latitude, longitude))
                binding.LocationTextview.setText("")
            }
        }


        binding.PlanSaveButton.setOnClickListener {
            val title = binding.NameText.text.toString()
            val date = binding.DateText.text.toString()
            val user = Firebase.auth.currentUser?.uid
            val public = binding.PublicCheck.isChecked
            for(i in viewModel.items){
                val temp = i.location.replace(" ", "_")
                i.location = temp
            }

            if(title == "" || date == "" || viewModel.items.isEmpty()){
                Toast.makeText(this, "필수값을 입력해주세요.", Toast.LENGTH_SHORT).show()
            }
            else {
                val scheduleMap = hashMapOf(
                    "title" to title,
                    "date" to date,
                    "user" to user,
                    "public" to public,
                    "items" to viewModel.items,
                    "copy" to copy
                )
                if(mode != 0) {
                    schedulesRef.add(scheduleMap)
                }else{
                    db.collection("schedules").document(code.toString()).update(scheduleMap as Map<String, Any>)
                }
                Toast.makeText(this, "저장이 완료되었습니다.", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)

            }
        }

        binding.MapButton.setOnClickListener {
            getLocationPermission()
            getLocationPermission2()
            if (mLocationPermissionGranted && mLocationPermissionGranted2) {
                val intent = Intent(this, MapsActivity2::class.java)
                intent.putExtra("mode", 3)
                intent.putExtra("array", viewModel.items)
                startActivityForResult(intent, 1)
            }
        }

        binding.routeButton.setOnClickListener {
            val intent = Intent(this, MapsActivity2::class.java)
            intent.putExtra("mode", 1)
            intent.putExtra("array", viewModel.items)
            startActivityForResult(intent, 2)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                binding.LocationTextview.setText(data.getStringExtra("location"))
                latitude = data.getDoubleExtra("latitude", 0.0)
                longitude = data.getDoubleExtra("longitude", 0.0)
            }
        }
    }

    override fun onBackPressed() {
        if (mode == 3) {
            finishAffinity() // 앱 종료
        } else {
            dialog.myDig()
            dialog.setOnClickListener(object : CustomDialog.ButtonClickListener {
                override fun onClicked() {
                    finish()
                }
            })
            //super.onBackPressed() // 기본 뒤로가기 동작 수행
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

    private fun getLocationPermission2() {
        // 권한을 요청, 결과는 콜백으로 조정됨
        // 권한을 얻은 경우
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mLocationPermissionGranted2 = true
        }
        // 권한 얻기에 실패한 경우
        else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION),
                PERMISSION_REQUEST_ACCESS_COARSE_LOCATION
            )
        }
    }
}

data class LocData(val location: String, val latitude: String, val longitude: String)