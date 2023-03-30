package com.example.woor2

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.woor2.databinding.FragmentPlanBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

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
        viewModel.itemsListData.observe(viewLifecycleOwner){
            adapter.notifyDataSetChanged()
        }
        registerForContextMenu(binding.planrecycleView)


        viewModel.itemClickEvent.observe(viewLifecycleOwner){
            val intent = Intent(activity, AddingPlanActivity::class.java)
            intent.putExtra("code", viewModel.itemClickEvent.value!!.toInt())
            startActivity(intent)
        }

        binding.plusButton.setOnClickListener {
            val intent = Intent(activity, AddingPlanActivity::class.java)
            intent.putExtra("location", "")
            startActivity(intent)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onCreateContextMenu(
        menu: ContextMenu,
        v: View,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)
        activity?.menuInflater?.inflate(R.menu.plan_menu, menu)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        val db : FirebaseFirestore = Firebase.firestore
        val schedulesRef = db.collection("schedules")
        when(item.itemId){
            R.id.delete -> {
                Toast.makeText(context, "삭제되었습니다.", Toast.LENGTH_LONG).show();
                schedulesRef.document(viewModel.items[viewModel.itemLongClick].id).delete()
                viewModel.deleteItem(viewModel.itemLongClick)
            }
            else -> return false
        }
        return true
    }
}

