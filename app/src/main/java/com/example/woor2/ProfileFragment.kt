package com.example.woor2

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.woor2.databinding.FragmentProfileBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.zxing.integration.android.IntentIntegrator

class ProfileFragment: Fragment() {

    private lateinit var binding: FragmentProfileBinding

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
    }

    fun startQR(view : View){
        IntentIntegrator(requireActivity()).setCameraId(0).initiateScan()
    }
}