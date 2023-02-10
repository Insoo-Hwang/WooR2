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
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

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
        registerForContextMenu(binding.boardrecycleView)

        binding.AddButton.setOnClickListener{
            startActivity(
                Intent(activity, AddBoardActivity::class.java)
            )
        }

        binding.RefreshButton.setOnClickListener{
            adapter.notifyDataSetChanged()
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
        val boardsRef = db.collection("boards")
        val user = Firebase.auth.currentUser?.uid
        when(item.itemId){
            R.id.delete -> {
                if(viewModel.items[viewModel.itemLongClick].writer.equals(user)) {
                    Toast.makeText(context, "삭제되었습니다.", Toast.LENGTH_LONG).show()
                    boardsRef.document(viewModel.items[viewModel.itemLongClick].id).delete()
                    viewModel.deleteItem(viewModel.itemLongClick)
                    adapter.notifyDataSetChanged()
                }
                else{
                    Toast.makeText(context, "사용자가 일치하지 않습니다.", Toast.LENGTH_LONG).show()
                }
            }
            else -> return false
        }
        return true
    }
}