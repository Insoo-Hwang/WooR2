package com.example.woor2

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.woor2.databinding.FragmentProfileBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.zxing.integration.android.IntentIntegrator
import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.sin

class ProfileFragment: Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private var location1 =""
    private var latitude1 = 0.0
    private var longitude1 = 0.0
    private var location2 =""
    private var latitude2 = 0.0
    private var longitude2 = 0.0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.uidText.text = Firebase.auth.currentUser?.uid // 테스트용 유저확인
        binding.logoutButton.setOnClickListener { //로그아웃 버튼
            Firebase.auth.signOut()
            startActivity(
                Intent(activity, LoginActivity::class.java)
            )
        }

        binding.CodeCheck.setOnClickListener {
            val code = binding.CodeText.text.toString()
            val intent = Intent(requireActivity(), AddingPlanActivity::class.java)
            intent.putExtra("code", code)
            intent.putExtra("mode", 3)
            startActivity(intent)
        }

        binding.QrCheck.setOnClickListener {
            startQR(requireView())
        }

        binding.appSearchButton.setOnClickListener {
            val address = binding.editTextAppSearch.text.toString()
            val url =
                "kakaomap://search?q=$address"
            openWebPage(url)
        }

        binding.locationTextview1.setOnClickListener {
            val intent = Intent(context, MapsActivity2::class.java)
            intent.putExtra("mode", 3)
            startActivityForResult(intent, 3)
        }

        binding.locationTextview2.setOnClickListener {
            val intent = Intent(context, MapsActivity2::class.java)
            intent.putExtra("mode", 3)
            startActivityForResult(intent, 4)
        }

        binding.locationCircleButton.setOnClickListener {
            val distance = getDistance(latitude1, longitude1, latitude2, longitude2)
            val intent = Intent(context, MapsActivity2::class.java)
            intent.putExtra("mode", 2)
            intent.putExtra("loc1", location1)
            intent.putExtra("lat1", latitude1)
            intent.putExtra("lon1", longitude1)
            intent.putExtra("loc2", location2)
            intent.putExtra("lat2", latitude2)
            intent.putExtra("lon2", longitude2)
            intent.putExtra("distance", distance)
            startActivityForResult(intent, 2)
        }
    }

    fun startQR(view : View){
        IntentIntegrator(requireActivity()).setCameraId(0).initiateScan()
    }

    fun openWebPage(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 3 && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                location1 = data.getStringExtra("location").toString()
                binding.locationTextview1.text = location1
                latitude1 = data.getDoubleExtra("latitude", 0.0)
                longitude1 = data.getDoubleExtra("longitude", 0.0)
            }
        }

        if (requestCode == 4 && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                location2 = data.getStringExtra("location").toString()
                binding.locationTextview2.text = location2
                latitude2 = data.getDoubleExtra("latitude", 0.0)
                longitude2 = data.getDoubleExtra("longitude", 0.0)
            }
        }
    }

    private fun getDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val theta = lon1 - lon2
        var dist =
            sin(deg2rad(lat1)) * sin(deg2rad(lat2)) + cos(deg2rad(lat1)) * cos(
                deg2rad(lat2)
            ) * cos(deg2rad(theta))
        dist = acos(dist)
        dist = rad2deg(dist)
        dist *= 60 * 1.1515 * 1609.344
        return dist //단위 meter
    }

    private fun deg2rad(deg: Double): Double {
        return deg * Math.PI / 180.0
    }

    private fun rad2deg(rad: Double): Double {
        return rad * 180 / Math.PI
    }
}