package com.example.todoapp.fragments.list.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.todoapp.data.models.ToDoData
import com.example.todoapp.databinding.ItemLayoutBinding
import com.example.todoapp.fragments.list.ListFragmentDirections

class ListAdapter : RecyclerView.Adapter<ListAdapter.MyViewHolder>() {


    var dataList = emptyList<ToDoData>()


    class MyViewHolder(private val binding: ItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(toDoData: ToDoData) {
            //binding.titleTxt.text = toDoData.title
            //binding.descriptionTxt.text = toDoData.description
            binding.toDoData = toDoData
            binding.executePendingBindings()

            itemView.setOnClickListener { view ->
                val action = ListFragmentDirections.actionListFragmentToUpdateFragment(toDoData)
                view.findNavController().navigate(action)
            }
        }

        companion object {
            fun from(parent: ViewGroup): MyViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemLayoutBinding.inflate(layoutInflater, parent, false)
                return MyViewHolder(binding)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        //val binding = ItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder.from(parent)

    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = dataList[position]
        holder.bind(currentItem)
    }

    fun setData(data: List<ToDoData>) {
        val toDoDiffUtil = ToDoDiffUtil(dataList, data)
        val toDoDiffResult = DiffUtil.calculateDiff(toDoDiffUtil)
        this.dataList = data
        toDoDiffResult.dispatchUpdatesTo(this)
    }
}