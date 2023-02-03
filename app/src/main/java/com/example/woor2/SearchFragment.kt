package com.example.woor2

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.woor2.databinding.FragmentSearchBinding

class SearchFragment: Fragment()  {

    private val viewModel by viewModels<SearchViewModel>()
    private lateinit var adapter: SearchAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentSearchBinding.inflate(inflater, container, false)
        val recyclerView = binding.searchrecycleView
        adapter = SearchAdapter(viewModel)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
        recyclerView.setHasFixedSize(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}