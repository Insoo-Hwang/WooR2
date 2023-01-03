package com.example.woor2

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.woor2.databinding.ActivityLoginBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class LoginActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loginButton.setOnClickListener { //로그인 버튼 클릭 리스너
            val id = binding.id.text.toString()
            val password = binding.password.text.toString()

            if(TextUtils.isEmpty(id) || TextUtils.isEmpty(password)) { // 아이디나 패스워드 입력 안했을 시 토스트
                Toast.makeText(this, "아이디와 비밀번호를 입력해주세요", Toast.LENGTH_SHORT).show()
            }
            else {
                logIn(id, password)
            }
        }

    }

    private fun logIn(id: String, password: String) { // 로그인을 처리하는 함수
        Firebase.auth.signInWithEmailAndPassword(id, password)
            .addOnCompleteListener(this) {
                if (it.isSuccessful) {
                    startActivity(
                        Intent(this, MainActivity::class.java)
                    )
                    finish()
                } else {
                    Log.w("LoginActivity", "signInWithEmail", it.exception)
                    Toast.makeText(this, "로그인에 실패하였습니다. 아이디와 비밀번호를 확인해주세요", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private var backPressedTime: Long = 0 // backbutton 2번에 종료
    override fun onBackPressed() {
        if(System.currentTimeMillis() - backPressedTime >= 2000) {
            backPressedTime = System.currentTimeMillis()
            Toast.makeText(this, "한번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show()
        } else {
            finish()
        }
    }
}