package com.example.woor2

import android.content.Context
import android.content.Intent
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.woor2.databinding.FragmentSearchBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class SearchFragment: Fragment()  {

    private lateinit var binding: FragmentSearchBinding
    private val viewModel by viewModels<SearchViewModel>()
    private lateinit var adapter: SearchAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        val recyclerView = binding.searchrecycleView
        adapter = SearchAdapter(viewModel)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
        recyclerView.setHasFixedSize(true)

        val icon = context?.let { ContextCompat.getDrawable(it, R.drawable.search) }
        context?.let { ContextCompat.getColor(it, R.color.A) }
            ?.let { icon?.setColorFilter(it, PorterDuff.Mode.SRC_ATOP) }
        binding.SearchingButton.setImageDrawable(icon)

        binding.SearchingButton.setOnClickListener{
            viewModel.deleteAll()
            adapter.notifyDataSetChanged()
            val searchValue = binding.SearchText.text.toString()
            if(searchValue.equals("")){
                Toast.makeText(context, "검색값을 입력해주세요.", Toast.LENGTH_LONG).show();
            }
            else {
                val db = Firebase.firestore
                db.collection("schedules").whereEqualTo("title", searchValue).get()
                    .addOnSuccessListener {
                        for (doc in it) {
                            if(doc["public"] as Boolean) {
                                viewModel.addItem(Item3(doc["title"].toString(), doc.id))
                            }
                        }
                    }
            }
        }

        viewModel.itemClickEvent.observe(viewLifecycleOwner){
            val intent = Intent(activity, AddingPlanActivity::class.java)
            intent.putExtra("code", viewModel.items[viewModel.itemClickEvent.value!!].id)
            intent.putExtra("mode", 2)
            startActivity(intent)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

}