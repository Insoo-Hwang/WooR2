package com.example.woor2

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.woor2.databinding.PlanLayoutBinding
import com.example.woor2.databinding.SearchLayoutBinding

class SearchAdapter(private val viewModel: SearchViewModel) :RecyclerView.Adapter<SearchAdapter.ViewHolder>(){
    inner class ViewHolder(private val binding: SearchLayoutBinding) : RecyclerView.ViewHolder(binding.root){
        fun setContents(pos: Int){
            with(viewModel.items[pos]){
                binding.SearchTitle.text = title
            }
            binding.root.setOnClickListener {
                viewModel.itemClickEvent.value = pos
            }
            binding.root.setOnLongClickListener {
                viewModel.itemLongClick = pos
                false
            }
        }
    }
    override fun onCreateViewHolder(parent : ViewGroup, viewType : Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = SearchLayoutBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int){
        holder.setContents(position)
    }

    override fun getItemCount(): Int {
        return viewModel.items.size
    }
}