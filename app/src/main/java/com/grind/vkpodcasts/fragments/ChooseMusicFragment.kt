package com.grind.vkpodcasts.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.OrientationEventListener
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.grind.vkpodcasts.R
import com.grind.vkpodcasts.adapters.SoundTracksAdapter
import com.grind.vkpodcasts.models.Podcast
import com.grind.vkpodcasts.models.SoundTrack

class ChooseMusicFragment(val mPodcast: Podcast, private val listener: SoundTracksAdapter.OnItemClickListener): Fragment() {
    private lateinit var backButton: ImageView
    private lateinit var etSearch: EditText
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = View.inflate(context, R.layout.fragment_choose_music, null)
        backButton = v.findViewById(R.id.imv_back_button)
        etSearch = v.findViewById(R.id.et_search)
        recyclerView = v.findViewById(R.id.rv_track_list)

        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = SoundTracksAdapter(context!!, makeDataList(), object : SoundTracksAdapter.OnItemClickListener{
            override fun onItemClick(soundTrack: SoundTrack) {
                mPodcast.backgroundTrack = soundTrack
                listener.onItemClick(soundTrack)
                fragmentManager?.popBackStack()
            }

        })


        return v
    }

    private fun makeDataList(): List<SoundTrack>{
        val dataList = mutableListOf<SoundTrack>()
        dataList.add(SoundTrack(R.drawable.album_1,"Conversations","Juice WRLD", "5:09"))
        dataList.add(SoundTrack(R.drawable.album_2,"Bleed","A Boogie Wit da Hoodie", "4:35"))
        dataList.add(SoundTrack(R.drawable.album_3,"Man Listen","Belly", "4:57"))
        dataList.add(SoundTrack(R.drawable.album_4,"Размажет..","Allj(Элджей)", "3:07"))
        dataList.add(SoundTrack(R.drawable.album_5,"PAID MY DUES","NF", "3:26"))
        dataList.add(SoundTrack(R.drawable.album_1,"Conversations","Juice WRLD", "5:09"))
        dataList.add(SoundTrack(R.drawable.album_2,"Bleed","A Boogie Wit da Hoodie", "4:35"))
        dataList.add(SoundTrack(R.drawable.album_3,"Man Listen","Belly", "4:57"))
        dataList.add(SoundTrack(R.drawable.album_4,"Размажет..","Allj(Элджей)", "3:07"))
        dataList.add(SoundTrack(R.drawable.album_5,"PAID MY DUES","NF", "3:26"))
        dataList.add(SoundTrack(R.drawable.album_1,"Conversations","Juice WRLD", "5:09"))
        dataList.add(SoundTrack(R.drawable.album_2,"Bleed","A Boogie Wit da Hoodie", "4:35"))
        dataList.add(SoundTrack(R.drawable.album_3,"Man Listen","Belly", "4:57"))
        dataList.add(SoundTrack(R.drawable.album_4,"Размажет..","Allj(Элджей)", "3:07"))
        dataList.add(SoundTrack(R.drawable.album_5,"PAID MY DUES","NF", "3:26"))
        return dataList
    }
}