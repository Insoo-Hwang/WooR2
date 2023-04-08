package com.example.woor2

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.ktx.Firebase

class LinkReceiverActivity : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_link_receiver)
        handleDynamicLinks()
    }

    fun handleDynamicLinks(){
    Firebase.dynamicLinks
        .getDynamicLink(intent)
        .addOnSuccessListener(this) { pendingDynamicLinkData ->
            var deeplink: Uri? = null
            if(pendingDynamicLinkData != null) {
                deeplink = pendingDynamicLinkData.link
            }
            if(deeplink != null) {
                val regex = ".*\\?(.*)".toRegex()
                val segment = regex.find(deeplink.toString())?.groupValues?.get(1)
                val intent = Intent(this, AddingPlanActivity::class.java)
                intent.putExtra("code", segment)
                intent.putExtra("mode", 3)
                startActivity(intent)
            }
        }
    }
}