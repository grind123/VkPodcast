package com.grind.vkpodcasts.adapters

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.grind.vkpodcasts.R
import com.grind.vkpodcasts.models.SoundTrack
import com.grind.vkpodcasts.models.TimeCode

class SoundTracksAdapter(val context: Context, val itemList: List<SoundTrack>, val listener: OnItemClickListener ): RecyclerView.Adapter<SoundTracksAdapter.SoundTrackHolder>() {

    class SoundTrackHolder(v: View): RecyclerView.ViewHolder(v){
        val logo: ImageView = v.findViewById(R.id.imv_logo)
        val artistName: TextView = v.findViewById(R.id.tv_artist_name)
        val trackName: TextView = v.findViewById(R.id.tv_track_name)
        val duration: TextView = v.findViewById(R.id.tv_time)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SoundTrackHolder {
        val inflate = View.inflate(parent.context, R.layout.item_music, null).apply {
            layoutParams = RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT)
        }
        return SoundTrackHolder(inflate)
    }

    override fun onBindViewHolder(holder: SoundTrackHolder, position: Int) {
        val soundTrack = itemList[position]
        holder.logo.setImageDrawable(ContextCompat.getDrawable(context, soundTrack.imageResourceId))
        holder.artistName.text = soundTrack.artistName
        holder.trackName.text = soundTrack.trackName
        holder.duration.text = soundTrack.duration
        holder.itemView.setOnClickListener {
            listener.onItemClick(soundTrack)
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }


    interface OnItemClickListener{
        fun onItemClick(soundTrack:SoundTrack)
    }
}