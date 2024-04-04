package com.example.myapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.DataClass
import com.example.myapplication.R

class AdapterClass(private val dataList: List<DataClass>) : RecyclerView.Adapter<AdapterClass.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.search_user_recycler_row, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = dataList[position]
        //holder.imageView.setImageResource(currentItem.dataImage)
        holder.textView.text = currentItem.dataTitle
    }

    override fun getItemCount() = dataList.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
       // val imageView: ImageView = itemView.findViewById(R.id.profile_pic_recycler_view)
        val textView: TextView = itemView.findViewById(R.id.firstName)
    }
}