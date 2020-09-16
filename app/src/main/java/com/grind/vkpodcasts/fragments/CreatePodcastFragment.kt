package com.grind.vkpodcasts.fragments

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.grind.vkpodcasts.R
import com.grind.vkpodcasts.models.Podcast

class CreatePodcastFragment : Fragment() {
    private lateinit var backButton: ImageView
    private lateinit var logo: ImageView
    private lateinit var podcastName: EditText
    private lateinit var podcastDesc: EditText
    private lateinit var uploadPodcastFileButton: TextView
    private lateinit var checkBox1: CheckBox
    private lateinit var checkBox2: CheckBox
    private lateinit var checkBox3: CheckBox
    private lateinit var nextButton: TextView

    private val mPodcast = Podcast()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = View.inflate(context, R.layout.fragment_create_podcast, null)
        backButton = v.findViewById(R.id.imv_back_button)
        logo = v.findViewById(R.id.imv_main_image)
        podcastName = v.findViewById(R.id.et_name)
        podcastDesc = v.findViewById(R.id.et_desc)
        uploadPodcastFileButton = v.findViewById(R.id.tv_edit_button)
        checkBox1 = v.findViewById(R.id.cb_1)
        checkBox2 = v.findViewById(R.id.cb_2)
        checkBox3 = v.findViewById(R.id.cb_3)
        nextButton = v.findViewById(R.id.tv_add_podcast)

        initListeners()
        return v
    }

    private fun initListeners() {
        backButton.setOnClickListener { fragmentManager?.popBackStack() }
        logo.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            startActivityForResult(Intent.createChooser(intent, "Выбор изображения"), 101)
        }

        uploadPodcastFileButton.setOnClickListener {
            showPodcastFile()
            nextButton.background =
                ContextCompat.getDrawable(context!!, R.drawable.button_primary_bg)
            nextButton.isClickable = true
        }


        nextButton.setOnClickListener {
            if(mPodcast.logoUri == null) Toast.makeText(context, "Выберите обложку", Toast.LENGTH_SHORT).show()
            if(podcastName.text.isNullOrBlank()) Toast.makeText(context, "Укажите название подкаста", Toast.LENGTH_SHORT).show()
            if(podcastDesc.text.isNullOrBlank()) Toast.makeText(context, "", Toast.LENGTH_SHORT).show()
            //TODO next fragment
        }
    }

    private fun showPodcastFile() {
        val title = view!!.findViewById<TextView>(R.id.tv_title)
        val desc = view!!.findViewById<TextView>(R.id.tv_desc)
        ObjectAnimator.ofFloat(title, "alpha", 0f).apply { duration = 300 }.start()
        ObjectAnimator.ofFloat(desc, "alpha", 0f).apply { duration = 300 }.start()
        ObjectAnimator.ofFloat(uploadPodcastFileButton, "alpha", 0f).apply { duration = 300 }
            .start()

        val item = View.inflate(context, R.layout.item_loaded_podcast_file, null)
        val fileName = item.findViewById<TextView>(R.id.tv_track_name)
        val editButton = item.findViewById<TextView>(R.id.tv_edit_button)
        fileName.text = "My_Podcast.mp3"
        mPodcast.fileName = fileName.text.toString()
        editButton.setOnClickListener {
            //soundEditorFragment
        }

        val params = ConstraintLayout.LayoutParams(
            0,
            ConstraintLayout.LayoutParams.WRAP_CONTENT
        ) as ConstraintLayout.LayoutParams
        params.leftToLeft = podcastDesc.id
        params.rightToRight = podcastDesc.id
        params.topToBottom = podcastDesc.id
        params.bottomToBottom = R.id.line_1
        item.alpha = 0f

        (uploadPodcastFileButton.parent as ConstraintLayout).addView(item, params)

        ObjectAnimator.ofFloat(item, "alpha", 1f).apply {
            duration = 500
            startDelay = 300
        }.start()

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 101 && resultCode == Activity.RESULT_OK) {
            mPodcast.logoUri = data?.data.toString()
            logo.setPadding(0, 0, 0, 0)
            logo.setImageURI(data!!.data)
        }
    }
}