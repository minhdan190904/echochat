package com.example.echochat.ui.chat

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.echochat.databinding.ItemResponseBinding

class ListItemAllOptionGenerate(
    private val listResponse: List<String>,
    private val eventClickListener: (String) -> Unit,
): RecyclerView.Adapter<ListItemAllOptionGenerate.MyViewHolderAllOption>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolderAllOption {
        val bind = ItemResponseBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolderAllOption(bind)
    }

    override fun getItemCount(): Int {
        return listResponse.size
    }

    override fun onBindViewHolder(holder: MyViewHolderAllOption, position: Int) {
        holder.bind(listResponse[position], eventClickListener, position + 1)
    }

    inner class MyViewHolderAllOption(private val binding: ItemResponseBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(response: String,  eventClickListener: (String) -> Unit, index: Int){
            binding.tvCount.text = index.toString()
            binding.tvOutputText.text = response
            binding.root.setOnClickListener {
                eventClickListener(binding.tvOutputText.text.toString())
            }
        }
    }

}
