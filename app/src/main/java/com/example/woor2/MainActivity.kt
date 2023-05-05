package com.example.woor2

import android.content.Intent
import android.content.res.ColorStateList
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.woor2.databinding.ActivityMainBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.ktx.Firebase
import com.google.zxing.integration.android.IntentIntegrator
import com.kakao.sdk.common.util.Utility


private const val TAG_PLAN = "plan_fragment"
private const val TAG_SEARCH = "search_fragment"
private const val TAG_BOARD = "bulletinBoard_fragment"
private const val TAG_PROFILE = "profile_fragment"

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setFragment(TAG_PLAN,PlanFragment()) //임시 시작화면

        binding.navigationView.setOnItemSelectedListener { item-> //바텀 내비 설정
            when(item.itemId) {
                R.id.plan -> setFragment(TAG_PLAN, PlanFragment())
                R.id.search -> setFragment(TAG_SEARCH, SearchFragment())
                R.id.bulletinBoard -> setFragment(TAG_BOARD, BulletinboardFragment())
                R.id.profile ->setFragment(TAG_PROFILE, ProfileFragment())
            }

            val colorStateList = ColorStateList(arrayOf(intArrayOf(android.R.attr.state_checked), intArrayOf()), intArrayOf(
                ContextCompat.getColor(this@MainActivity, R.color.A), ContextCompat.getColor(this@MainActivity, R.color.gray)))
            binding.navigationView.itemIconTintList = colorStateList
            binding.navigationView.itemTextColor = colorStateList

            true
        }

        val keyHash = Utility.getKeyHash(this)
        println("--------------------------------------------------keyHash-------------------------------------------- = $keyHash -------------------------------------------")
    }

    private fun setFragment(tag: String, fragment: Fragment) { //바텀 내비 설정용 함수
        val manager: FragmentManager = supportFragmentManager
        val ft: FragmentTransaction = manager.beginTransaction()

        //트랜잭션에 tag로 전달된 fragment가 없을 경우 add
        if(manager.findFragmentByTag(tag) == null) {
            ft.add(R.id.mainFrameLayout, fragment, tag)
        }

        //작업이 수월하도록 manager에 add되어있는 fragment들을 변수로 할당해둠
        val plan = manager.findFragmentByTag(TAG_PLAN)
        val search = manager.findFragmentByTag(TAG_SEARCH)
        val board = manager.findFragmentByTag(TAG_BOARD)
        val profile = manager.findFragmentByTag(TAG_PROFILE)

        //모든 프래그먼트 hide
        if(plan!=null){
            ft.hide(plan)
        }
        if(search!=null){
            ft.hide(search)
        }
        if(board!=null){
            ft.hide(board)
        }
        if(profile!=null){
            ft.hide(profile)
        }

        //선택한 항목에 따라 그에 맞는 프래그먼트만 show
        if(tag == TAG_PLAN){
            if(plan!=null){
                ft.show(plan)
            }
        }
        else if(tag == TAG_SEARCH){
            if(search!=null){
                ft.show(search)
            }
        }
        else if(tag == TAG_BOARD){
            if(board!=null){
                ft.show(board)
            }
        }
        else if(tag == TAG_PROFILE){
            if(profile!=null){
                ft.show(profile)
            }
        }

        //마무리
        ft.commitAllowingStateLoss()
    }

    private var backPressedTime: Long = 0 // backbutton 2번에 종료
    override fun onBackPressed() {
        if(System.currentTimeMillis() - backPressedTime >= 2000) {
            backPressedTime = System.currentTimeMillis()
            Toast.makeText(this, "한번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show()
        } else {
            super.onBackPressed()
            finishAffinity()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result  = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if(result != null){
            if(result.contents != null){
                val intent = Intent(this, AddingPlanActivity::class.java)
                intent.putExtra("code", result.contents.toString())
                intent.putExtra("mode", 3)
                startActivity(intent)
            }
        }else{
            super.onActivityResult(requestCode, resultCode, data)
        }
    }
}