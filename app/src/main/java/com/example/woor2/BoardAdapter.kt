package com.example.woor2

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.woor2.databinding.BoardLayoutBinding
import com.example.woor2.databinding.PlanLayoutBinding

class BoardAdapter(private val viewModel: BoardViewModel) :RecyclerView.Adapter<BoardAdapter.ViewHolder>(){
    inner class ViewHolder(private val binding: BoardLayoutBinding) : RecyclerView.ViewHolder(binding.root){
        fun setContents(pos: Int){
            with(viewModel.items[pos]){
                binding.BoardTitle.text = title
                binding.BoardWriter.text = writer
                binding.BoardDate.text = date
            }
        }
    }
    override fun onCreateViewHolder(parent : ViewGroup, viewType : Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = BoardLayoutBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int){
        holder.setContents(position)
    }

    override fun getItemCount(): Int {
        return viewModel.items.size
    }
}