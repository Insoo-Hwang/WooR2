package com.example.woor2

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.woor2.databinding.FragmentPlanBinding

class PlanFragment: Fragment() {

    private lateinit var binding: FragmentPlanBinding
    private val viewModel by viewModels<PlanViewModel>()
    private lateinit var adapter: PlanAdapter


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentPlanBinding.inflate(inflater, container, false)
        val recyclerView = binding.planrecycleView
        adapter = PlanAdapter(viewModel)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
        recyclerView.setHasFixedSize(true)

        binding.plusButton.setOnClickListener {
            startActivity(
                Intent(activity, AddingPlanActivity::class.java)
            )
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}

