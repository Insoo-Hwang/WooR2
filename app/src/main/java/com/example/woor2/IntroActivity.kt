package com.example.woor2

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.animation.AccelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import com.example.woor2.databinding.ActivityIntroBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.util.*
import kotlin.concurrent.schedule

class IntroActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityIntroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ObjectAnimator.ofFloat(binding.imageView3, "scaleX", 1f, 1.4f).apply { //X 방향 효과
            duration = 500
            interpolator = AccelerateInterpolator()
            start()
        }
        ObjectAnimator.ofFloat(binding.imageView3, "scaleY", 1f, 1.4f).apply { //Y 방향 효과
            duration = 500
            interpolator = AccelerateInterpolator()
            start()
        }

        Timer().schedule(700) { //0.7초 후 로그인 액티비티로 전환
            if (Firebase.auth.currentUser == null) {
                startActivity(
                    Intent(this@IntroActivity, LoginActivity::class.java) // 로그인 상태가 아니면 로그인 화면으로
                )
            }

            else {
                startActivity(
                    Intent(this@IntroActivity, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)// 로그인 상태면 메인 화면으로
                )
            }
            finish()
        }
    }
}