package com.example.woor2

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.woor2.databinding.PlanLayoutBinding

class PlanAdapter(private val viewModel: PlanViewModel) :RecyclerView.Adapter<PlanAdapter.ViewHolder>(){
    inner class ViewHolder(private val binding: PlanLayoutBinding) : RecyclerView.ViewHolder(binding.root){
        fun setContents(pos: Int){
            with(viewModel.items[pos]){
                binding.textView5.text = name
                binding.textView6.text = phone
            }
        }
    }
    override fun onCreateViewHolder(parent : ViewGroup, viewType : Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = PlanLayoutBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int){
        holder.setContents(position)
    }

    override fun getItemCount(): Int {
        return viewModel.items.size
    }
}