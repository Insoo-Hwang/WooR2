package com.example.woor2

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.woor2.databinding.AddplanLayoutBinding
import java.util.*

class AddPlanAdapter(private val viewModel: AddPlanViewModel) :RecyclerView.Adapter<AddPlanAdapter.ViewHolder>(){
    inner class ViewHolder(private val binding: AddplanLayoutBinding) : RecyclerView.ViewHolder(binding.root){
        fun setContents(pos: Int){
            with(viewModel.items[pos]){
                binding.LocationText.text = location
            }
            binding.root.setOnClickListener {
                viewModel.itemClickEvent.value = pos
            }
            binding.root.setOnClickListener {
                viewModel.itemLongClick = pos
                false
            }
            binding.DeleteButton.setOnClickListener {
                viewModel.deleteItem(pos)
            }
            binding.UpButton.setOnClickListener{
                swap(pos, 0)
            }
            binding.DownButton.setOnClickListener {
                swap(pos, 1)
            }
            binding.LocationButton.setOnClickListener {
                if(pos!=0) {
                    val url =
                        "kakaomap://route?sp=${viewModel.getLat(pos - 1)},${viewModel.getLon(pos - 1)}&ep=${
                            viewModel.getLat(pos)
                        },${viewModel.getLon(pos)}&by=PUBLICTRANSIT"
                    viewModel.openWebPage(url)
                }
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

    fun swap(from : Int, num : Int){
        if(num == 0) { //올라가기
            if(from != 0) {
                Collections.swap(viewModel.items, from, from - 1)
                notifyDataSetChanged()
            }
        }
        else{ //내려가기
            if(from != viewModel.items.size-1) {
                Collections.swap(viewModel.items, from, from + 1)
                notifyDataSetChanged()
            }
        }
    }
}