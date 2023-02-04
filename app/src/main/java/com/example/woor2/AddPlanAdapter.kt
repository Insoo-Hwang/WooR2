package com.example.woor2

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.woor2.databinding.AddplanLayoutBinding
import com.example.woor2.databinding.BoardLayoutBinding
import com.example.woor2.databinding.PlanLayoutBinding

class AddPlanAdapter(private val viewModel: AddPlanViewModel) :RecyclerView.Adapter<AddPlanAdapter.ViewHolder>(){
    inner class ViewHolder(private val binding: AddplanLayoutBinding) : RecyclerView.ViewHolder(binding.root){
        fun setContents(pos: Int){
            with(viewModel.items[pos]){
            }
        }
    }
    override fun onCreateViewHolder(parent : ViewGroup, viewType : Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = AddplanLayoutBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int){
        holder.setContents(position)
    }

    override fun getItemCount(): Int {
        return viewModel.items.size
    }
}