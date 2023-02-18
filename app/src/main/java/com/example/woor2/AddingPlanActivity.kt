package com.example.woor2

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

class AddingPlanActivity: AppCompatActivity() {
    private val viewModel by viewModels<AddPlanViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityAddingPlanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val e = intent.extras?:return
        val intentLocation = e.getString("location")
        val intentLatitude = e.getDouble("latitude")
        val intentLongitude = e.getDouble("longitude")

        binding.LocationTextview.setText(intentLocation)

        val adapter = AddPlanAdapter(viewModel)
        binding.addplanrecycleView.adapter = adapter
        binding.addplanrecycleView.layoutManager = LinearLayoutManager(this)
        binding.addplanrecycleView.setHasFixedSize(true)
        viewModel.itemsListData.observe(this){
            adapter.notifyDataSetChanged()
        }
        binding.AddPlanButton.setOnClickListener {
            if(binding.LocationTextview.text.toString() == ""){
                Toast.makeText(this, "장소를 입력해주세요.", Toast.LENGTH_SHORT).show()
            }
            else {
                viewModel.addItem(Item4(binding.LocationTextview.text.toString(), intentLatitude, intentLongitude))
                binding.LocationTextview.setText("")
            }
        }
        val db : FirebaseFirestore = Firebase.firestore
        val schedulesRef = db.collection("schedules")

        binding.PlanSaveButton.setOnClickListener {
            val title = binding.NameText.text.toString()
            val date = binding.DateText.text.toString()
            val user = Firebase.auth.currentUser?.uid
            val public = binding.PublicCheck.isChecked
            if(title == "" || date == ""){
                Toast.makeText(this, "필수값을 입력해주세요.", Toast.LENGTH_SHORT).show()
            }
            else {
                val scheduleMap = hashMapOf(
                    "title" to title,
                    "date" to date,
                    "user" to user,
                    "public" to public,
                    "location" to intentLocation
                )
                schedulesRef.add(scheduleMap)
                    .addOnSuccessListener { }.addOnFailureListener {}
                startActivity(
                    Intent(this, MainActivity::class.java)
                )
                Toast.makeText(this, "저장이 완료되었습니다.", Toast.LENGTH_SHORT).show()
            }
        }

        binding.MapButton.setOnClickListener {
            startActivity(
                Intent(this, MapsActivity::class.java)
            )
        }
    }
}