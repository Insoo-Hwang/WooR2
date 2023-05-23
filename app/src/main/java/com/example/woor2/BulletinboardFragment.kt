package com.example.woor2

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.woor2.databinding.FragmentBullentinBoardBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class BulletinboardFragment: Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentBullentinBoardBinding.inflate(inflater, container, false)

        binding.cardView1.setOnClickListener{
            val intent = Intent(requireActivity(), AddingPlanActivity::class.java)
            intent.putExtra("code", "b81B03OmMDmRhK1Nim2a")
            intent.putExtra("mode", 3)
            startActivity(intent)
        }

        binding.cardView2.setOnClickListener{
            val intent = Intent(requireActivity(), AddingPlanActivity::class.java)
            intent.putExtra("code", "gXzD4sGm1VAhJJkugZMY")
            intent.putExtra("mode", 3)
            startActivity(intent)
        }

        binding.cardView3.setOnClickListener{
            val intent = Intent(requireActivity(), AddingPlanActivity::class.java)
            intent.putExtra("code", "mOyATlMW0s9H5o7TCCLm")
            intent.putExtra("mode", 3)
            startActivity(intent)
        }

        binding.cardView4.setOnClickListener{
            val intent = Intent(requireActivity(), AddingPlanActivity::class.java)
            intent.putExtra("code", "P75Vj6PdX0DhPjyZkI5J")
            intent.putExtra("mode", 3)
            startActivity(intent)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}