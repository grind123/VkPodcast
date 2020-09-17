package com.grind.vkpodcasts.fragments

import android.net.Uri
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.grind.vkpodcasts.R
import com.grind.vkpodcasts.models.Podcast

class PodcastPreviewFragment(val mPodcast: Podcast): Fragment() {
    private lateinit var backButton: ImageView
    private lateinit var podcastLogo: ImageView
    private lateinit var podcastName: TextView
    private lateinit var communityName: TextView
    private lateinit var podcastDuration: TextView
    private lateinit var podcastDesc: TextView
    private lateinit var timeCodesTitle: TextView
    private lateinit var timeCodesContainer: LinearLayout
    private lateinit var postButton: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = View.inflate(context, R.layout.fragment_podcast_preview, null)
        backButton = v.findViewById(R.id.imv_back_button)
        podcastLogo = v.findViewById(R.id.imv_avatar)
        podcastName = v.findViewById(R.id.tv_podcast_name)
        communityName = v.findViewById(R.id.tv_community)
        podcastDuration = v.findViewById(R.id.tv_duration)
        podcastDesc = v.findViewById(R.id.tv_podcast_desc)
        timeCodesTitle = v.findViewById(R.id.time_codes_title)
        timeCodesContainer = v.findViewById(R.id.ll_timecodes_container)
        postButton = v.findViewById(R.id.tv_post_podcast_button)

        setData()
        initListeners()
        showTimeCodeList(timeCodesContainer)
        return v
    }


    private fun setData(){
        podcastLogo.setImageURI(Uri.parse(mPodcast.logoUri))
        podcastName.text = mPodcast.name
        podcastDesc.text = mPodcast.desc

        if(mPodcast.timeCodesList.isNullOrEmpty()) timeCodesTitle.visibility = View.GONE
    }

    private fun initListeners(){
        postButton.setOnClickListener {
            fragmentManager?.popBackStackImmediate(CreatePodcastFragment::class.java.simpleName, FragmentManager.POP_BACK_STACK_INCLUSIVE)
            replaceFragment(LastFragment(), false)
        }
        backButton.setOnClickListener {
            fragmentManager?.popBackStack()
        }
    }

    private fun showTimeCodeList(container: LinearLayout){
        mPodcast.timeCodesList?.forEach {
            val item = View.inflate(context, R.layout.item_preview_timecode, null)
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            val timeCodeText = item.findViewById<TextView>(R.id.tv_preview_timecode)
            val time = SpannableString("${it.timeString}")
                time.setSpan(ForegroundColorSpan(ContextCompat.getColor(context!!, R.color.colorAccent)),
            0, it.timeString!!.length, 0)
            val name = SpannableString(" — ${it.name}")
            timeCodeText.append(time)
            timeCodeText.append(name)

            container.addView(item, params)
        }
    }
}