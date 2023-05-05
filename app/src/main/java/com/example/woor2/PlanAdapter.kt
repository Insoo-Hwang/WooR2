package com.example.woor2

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.woor2.databinding.PlanLayoutBinding

class PlanAdapter(private val viewModel: PlanViewModel, private val activity: Activity) :RecyclerView.Adapter<PlanAdapter.ViewHolder>(){
    inner class ViewHolder(private val binding: PlanLayoutBinding) : RecyclerView.ViewHolder(binding.root){
        fun setContents(pos: Int){
            with(viewModel.items[pos]){
                binding.PlanTitle.text = title
                binding.PlanDate.text = date
                binding.qrButton.setOnClickListener {
                    val intent = Intent(activity, QrActivity::class.java)
                    intent.putExtra("key", viewModel.items[pos].id)
                    activity.startActivity(intent)
                }
                binding.shareButton.setOnClickListener {
                    share(viewModel.items[pos].id)
                }
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
        val binding = PlanLayoutBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int){
        holder.setContents(position)
    }

    override fun getItemCount(): Int {
        return viewModel.items.size
    }

    fun share(code : String) {
        try {
            val sendIntent = Intent()
            sendIntent.action = Intent.ACTION_SEND
            sendIntent.putExtra(Intent.EXTRA_TEXT, code)
            sendIntent.type = "text/plain"
            activity?.startActivity(Intent.createChooser(sendIntent, "Share"))
        } catch (ignored: ActivityNotFoundException) {

        }
    }
}