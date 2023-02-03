package com.example.woor2

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.woor2.databinding.FragmentBullentinBoardBinding

class BulletinboardFragment: Fragment() {

    private val viewModel by viewModels<BoardViewModel>()
    private lateinit var adapter: BoardAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentBullentinBoardBinding.inflate(inflater, container, false)
        val recyclerView = binding.boardrecycleView
        adapter = BoardAdapter(viewModel)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
        recyclerView.setHasFixedSize(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}