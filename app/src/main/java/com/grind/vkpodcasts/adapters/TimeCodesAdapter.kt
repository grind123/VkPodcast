package com.grind.vkpodcasts.adapters

import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.grind.vkpodcasts.R
import com.grind.vkpodcasts.models.TimeCode

class TimeCodesAdapter(val itemList: MutableList<TimeCode>): RecyclerView.Adapter<TimeCodesAdapter.TimeCodeHolder>() {

    class TimeCodeHolder(v: View): RecyclerView.ViewHolder(v){
        val name: EditText = v.findViewById(R.id.et_name)
        val time: EditText = v.findViewById(R.id.et_time)
        val removeButton: ImageView = v.findViewById(R.id.imv_remove)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimeCodeHolder {
        val inflate = View.inflate(parent.context, R.layout.item_edited_timecode, null)
        return TimeCodeHolder(inflate)
    }

    override fun onBindViewHolder(holder: TimeCodeHolder, position: Int) {
//        val timeCode = itemList[position]
//        holder.name.setText(timeCode.name)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }
}