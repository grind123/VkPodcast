package com.grind.vkpodcasts.fragments

import android.media.Image
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.grind.vkpodcasts.R

fun Fragment.replaceFragment(fragment: Fragment, addToBackStack: Boolean){
    val transaction = fragmentManager!!.beginTransaction()
        .replace(R.id.main_container, fragment)
        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
    if(addToBackStack) transaction.addToBackStack(fragment::class.java.simpleName)
    transaction.commit()
}

class MainFragment: Fragment() {
    private lateinit var closeButton: ImageView
    private lateinit var addButton: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = View.inflate(context, R.layout.fragment_main, null)
        closeButton = v.findViewById(R.id.imv_close)
        addButton = v.findViewById(R.id.tv_add_podcast)
        initListeners()
        return v
    }

    private fun initListeners(){
        closeButton.setOnClickListener { activity?.finish() }
        addButton.setOnClickListener { replaceFragment(CreatePodcastFragment(), true) }
    }
}