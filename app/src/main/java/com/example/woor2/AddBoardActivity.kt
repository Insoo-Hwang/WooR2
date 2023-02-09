package com.example.woor2

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.woor2.databinding.ActivityAddBoardBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class AddBoardActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        val binding = ActivityAddBoardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val db : FirebaseFirestore = Firebase.firestore
        val boardsRef = db.collection("boards")

        binding.BoardSaveButton.setOnClickListener {
            val title = binding.BoardTitleText.text.toString()
            val text = binding.BoardTextText.text.toString()
            val user = Firebase.auth.currentUser?.uid
            if(title == "" || text == ""){
                Toast.makeText(this, "필수값을 입력해주세요.", Toast.LENGTH_SHORT).show()
            }
            else {
                val boardMap = hashMapOf(
                    "title" to title,
                    "text" to text,
                    "user" to user
                )
                boardsRef.add(boardMap)
                    .addOnSuccessListener { }.addOnFailureListener {}
                startActivity(
                    Intent(this, MainActivity::class.java)
                )
                Toast.makeText(this, "저장이 완료되었습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}