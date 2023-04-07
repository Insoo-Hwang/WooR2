package com.example.woor2

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddingPlanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val e = intent.extras?:return
        val code = e.getString("code")
        val mode = e.getInt("mode")
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
                binding.DateText.setText(it["date"].toString())
                if(mode == 2 || copy) {
                    binding.PublicCheck.isEnabled = false
                    binding.PublicCheck.isChecked = false
                    copy = true
                }
                else
                    binding.PublicCheck.isChecked = it["public"] as Boolean
                val gson = Gson()
                val jsonArray = gson.fromJson(it["items"].toString(), Array<LocData>::class.java)
                for (data in jsonArray) {
                    viewModel.addItem(Item4(data.location, data.latitude.toString().toDouble(), data.longitude.toString().toDouble()))
                }
            }
        }

        viewModel.itemsListData.observe(this){
            adapter.notifyDataSetChanged()
        }
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
                onBackPressed()
                Toast.makeText(this, "저장이 완료되었습니다.", Toast.LENGTH_SHORT).show()
            }
        }

        binding.MapButton.setOnClickListener {
            val intent = Intent(this, MapsActivity2::class.java)
            startActivityForResult(intent, 1)
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
}

data class LocData(val location: String, val latitude: Double, val longitude: Double)