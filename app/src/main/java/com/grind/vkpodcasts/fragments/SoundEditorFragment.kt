package com.grind.vkpodcasts.fragments

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.content.res.ColorStateList
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import android.view.animation.LinearInterpolator
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.grind.vkpodcasts.R
import com.grind.vkpodcasts.customviews.LockingNestedScrollView
import com.grind.vkpodcasts.models.Podcast
import com.grind.vkpodcasts.models.TimeCode
import com.grind.vkpodcasts.utils.PixelsUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

class SoundEditorFragment(private val mPodcast: Podcast) : Fragment() {
    private lateinit var backButton: ImageView
    private lateinit var nestedScroll: LockingNestedScrollView
    private lateinit var spectrogram: ImageView
    private lateinit var timePositionIndicator: ImageView
    private lateinit var playButton: ImageView
    private lateinit var cutButton: ImageView
    private lateinit var returnButton: ImageView
    private lateinit var noteButton: ImageView
    private lateinit var emergencyButton: ImageView
    private lateinit var extinctionButton: ImageView
    private lateinit var addTimeCodeContainer: LinearLayout
    private lateinit var timeCodesContainer: LinearLayout

    private var isPlaying = false
    private var isSpectrogramFocused = false
    private var isMusicAdded = false
    private var isEmergencyAdded = false
    private var isExtinctionAdded = false

    private var isBackButtonWasClicked = false

    private lateinit var timePositionTranslation: ObjectAnimator

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = View.inflate(context, R.layout.fragment_sound_editor, null)
        backButton = v.findViewById(R.id.imv_back_button)
        spectrogram = v.findViewById(R.id.imv_spectrogram)
        nestedScroll = v.findViewById(R.id.nsv)
        timePositionIndicator = v.findViewById(R.id.imv_time_position)
        playButton = v.findViewById(R.id.imv_play)
        cutButton = v.findViewById(R.id.imv_cut)
        returnButton = v.findViewById(R.id.imv_arrow_return)
        noteButton = v.findViewById(R.id.imv_note)
        emergencyButton = v.findViewById(R.id.imv_sound_up)
        extinctionButton = v.findViewById(R.id.imv_sound_down)
        addTimeCodeContainer = v.findViewById(R.id.ll_add_timecode)
        timeCodesContainer = v.findViewById(R.id.ll_time_codes_container)

        initListeners()

        return v
    }

    override fun onStart() {
        super.onStart()
        (spectrogram.parent as ConstraintLayout).post {
            timePositionTranslation = ObjectAnimator.ofFloat(
                timePositionIndicator, "translationX",
                ((spectrogram.parent as ConstraintLayout).width - PixelsUtils.dpToPx(6)).toFloat()
            ).apply {
                duration = 1000 * 10
                interpolator = LinearInterpolator()
                addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator?) {
                        timePositionIndicator.translationX = 0f
                        timePositionIndicator.visibility = View.INVISIBLE
                        playButton.setImageDrawable(
                            ContextCompat.getDrawable(
                                context!!,
                                R.drawable.ic_play
                            )
                        )
                        playButton.background =
                            ContextCompat.getDrawable(context!!, R.drawable.button_gray_normal)
                        playButton.imageTintList = ColorStateList.valueOf(
                            ContextCompat.getColor(
                                context!!,
                                R.color.colorAccent
                            )
                        )
                        isPlaying = false
                    }

                    override fun onAnimationStart(animation: Animator?) {
                        timePositionIndicator.visibility = View.VISIBLE
                    }
                })
            }
        }
    }

    override fun onDestroy() {
        if(!isBackButtonWasClicked){
            val list = makeTimeCodeList()
            mPodcast.timeCodesList = list
        }

        super.onDestroy()
    }

    private fun initListeners() {
        backButton.setOnClickListener {
            val list = makeTimeCodeList()
            mPodcast.timeCodesList = list
            isBackButtonWasClicked = true
            fragmentManager?.popBackStack()
        }

        spectrogram.setOnClickListener {
            if (isSpectrogramFocused) {
                removeEditFrame(spectrogram.parent as ConstraintLayout)
                cutButton.alpha = 0.5f
                returnButton.alpha = 0.5f
                isSpectrogramFocused = false
            } else {

                addEditFrame(spectrogram.parent as ConstraintLayout)
                cutButton.alpha = 1f
                returnButton.alpha = 1f
                isSpectrogramFocused = true
            }
        }
        playButton.setOnClickListener {
            if (isPlaying) {
                playButton.setImageDrawable(
                    ContextCompat.getDrawable(
                        context!!,
                        R.drawable.ic_pause
                    )
                )
                playButton.background =
                    ContextCompat.getDrawable(context!!, R.drawable.button_gray_normal)
                playButton.imageTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(
                        context!!,
                        R.color.colorAccent
                    )
                )
                timePositionTranslation.pause()
                isPlaying = false
            } else {
                playButton.setImageDrawable(
                    ContextCompat.getDrawable(
                        context!!,
                        R.drawable.ic_play
                    )
                )
                playButton.background =
                    ContextCompat.getDrawable(context!!, R.drawable.button_primary_normal)
                playButton.imageTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(
                        context!!,
                        R.color.color_white
                    )
                )
                if (timePositionTranslation.isPaused) {
                    timePositionTranslation.resume()
                } else {
                    timePositionTranslation.start()
                }
                isPlaying = true
            }
        }

        noteButton.setOnClickListener {
            if (isMusicAdded) {
                noteButton.background =
                    ContextCompat.getDrawable(context!!, R.drawable.button_gray_normal)
                noteButton.imageTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(context!!, R.color.colorAccent))
                isMusicAdded = false
            } else {
                noteButton.background =
                    ContextCompat.getDrawable(context!!, R.drawable.button_primary_normal)
                noteButton.imageTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(context!!, R.color.color_white))
                isMusicAdded = true
            }
        }

        emergencyButton.setOnClickListener {
            if (isEmergencyAdded) {
                emergencyButton.background =
                    ContextCompat.getDrawable(context!!, R.drawable.button_gray_normal)
                emergencyButton.imageTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(context!!, R.color.colorAccent))
                fadeAnimation(
                    this@SoundEditorFragment.view!!.findViewById(R.id.tv_emergency),
                    false
                )
                mPodcast.emergency = false
                isEmergencyAdded = false
            } else {
                emergencyButton.background =
                    ContextCompat.getDrawable(context!!, R.drawable.button_primary_normal)
                emergencyButton.imageTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(context!!, R.color.color_white))
                fadeAnimation(this@SoundEditorFragment.view!!.findViewById(R.id.tv_emergency), true)
                mPodcast.emergency = true
                isEmergencyAdded = true
            }
        }

        extinctionButton.setOnClickListener {
            if (isExtinctionAdded) {
                extinctionButton.background =
                    ContextCompat.getDrawable(context!!, R.drawable.button_gray_normal)
                extinctionButton.imageTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(context!!, R.color.colorAccent))
                fadeAnimation(
                    this@SoundEditorFragment.view!!.findViewById(R.id.tv_extinction),
                    false
                )
                mPodcast.extinction = false
                isExtinctionAdded = false
            } else {
                extinctionButton.background =
                    ContextCompat.getDrawable(context!!, R.drawable.button_primary_normal)
                extinctionButton.imageTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(context!!, R.color.color_white))
                fadeAnimation(
                    this@SoundEditorFragment.view!!.findViewById(R.id.tv_extinction),
                    true
                )
                mPodcast.extinction = true
                isExtinctionAdded = true
            }
        }

        addTimeCodeContainer.setOnClickListener {
            addEmptyTimeCodeToContainer(timeCodesContainer)
        }
    }

    private fun fadeAnimation(view: View, show: Boolean) {
        if (show) {
            view.alpha = 0f
            view.visibility = View.VISIBLE
            ObjectAnimator.ofFloat(view, "alpha", 1f).apply { duration = 500 }.start()
        } else {
            ObjectAnimator.ofFloat(view, "alpha", 0f).apply {
                duration = 500
                addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator?) {
                        view.visibility = View.INVISIBLE
                    }
                })
            }.start()
        }
    }

    private fun addEditFrame(container: ConstraintLayout) {
        val leftIndicator = ImageView(context)
        leftIndicator.setImageDrawable(
            ContextCompat.getDrawable(
                context!!,
                R.drawable.ic_sound_cut_inticator
            )
        )
        val paramsLeft =
            ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, 0)
        paramsLeft.leftToLeft = spectrogram.id
        paramsLeft.topToTop = spectrogram.id
        paramsLeft.bottomToBottom = spectrogram.id

        val rightIndicator = ImageView(context)
        rightIndicator.setImageDrawable(
            ContextCompat.getDrawable(
                context!!,
                R.drawable.ic_sound_cut_inticator
            )
        )
        val paramsRight =
            ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, 0)
        paramsRight.rightToRight = spectrogram.id
        paramsRight.topToTop = spectrogram.id
        paramsRight.bottomToBottom = spectrogram.id

        val topLine = View(context)
        topLine.background = ColorDrawable(ContextCompat.getColor(context!!, R.color.colorAccent))
        val paramsTopLine = ConstraintLayout.LayoutParams(0, PixelsUtils.dpToPx(1))
        paramsTopLine.rightToRight = spectrogram.id
        paramsTopLine.leftToLeft = spectrogram.id
        paramsTopLine.topToTop = spectrogram.id

        val bottomLine = View(context)
        bottomLine.background =
            ColorDrawable(ContextCompat.getColor(context!!, R.color.colorAccent))
        val paramsBottomLine = ConstraintLayout.LayoutParams(0, PixelsUtils.dpToPx(1))
        paramsBottomLine.rightToRight = spectrogram.id
        paramsBottomLine.leftToLeft = spectrogram.id
        paramsBottomLine.bottomToBottom = spectrogram.id


        container.addView(leftIndicator, paramsLeft)
        container.addView(rightIndicator, paramsRight)
        container.addView(topLine, paramsTopLine)
        container.addView(bottomLine, paramsBottomLine)
        var currMarginStart = 0
        var currMarginEnd = 0
        var containerWidth = container.width


        val touchListenerLeft = object : View.OnTouchListener {
            private val indicatorParams =
                leftIndicator.layoutParams as ConstraintLayout.LayoutParams
            private val topLineParams = topLine.layoutParams as ConstraintLayout.LayoutParams
            private val bottomLineParams = bottomLine.layoutParams as ConstraintLayout.LayoutParams
            private var lastStartMargin = 0
            private var initialX = 0
            private var initialY = 0
            private var initialTouchX = 0
            private var initialTouchY = 0

            override fun onTouch(view: View?, motionEvent: MotionEvent?): Boolean {
                when (motionEvent!!.action) {
                    MotionEvent.ACTION_DOWN -> {
                        initialTouchX = motionEvent.rawX.toInt()
                        initialTouchY = motionEvent.rawY.toInt()
                        nestedScroll.setScrollingEnabled(false)
                        Log.e("initialX", "$initialX")
                        Log.e("initialY", "$initialY")
                        Log.e("initialTouchX", "$initialTouchX")
                        Log.e("initialTouchY", "$initialTouchY")
                    }
                    MotionEvent.ACTION_UP -> {
                        lastStartMargin = indicatorParams.marginStart
                        currMarginStart = lastStartMargin
                        nestedScroll.setScrollingEnabled(true)
                    }
                    MotionEvent.ACTION_MOVE -> {
                        Log.e("move", "${motionEvent.rawX.toInt()}")
                        val currX = motionEvent.rawX.toInt()
                        val deltaX = (initialTouchX - currX).absoluteValue
                        val canMoveForward = getScreenWidth() > currMarginStart + currMarginEnd +
                                leftIndicator.width * 3 + PixelsUtils.dpToPx(16) * 2
                        if (currX < initialTouchX) {
                            if (canMoveForward) {
                                indicatorParams.marginStart = lastStartMargin - deltaX
                                topLineParams.marginStart = lastStartMargin - deltaX
                                bottomLineParams.marginStart = lastStartMargin - deltaX
                                currMarginStart = indicatorParams.marginStart
                            } else {
                                currMarginStart -= PixelsUtils.dpToPx(16)
                            }
                        } else {
                            if (canMoveForward) {
                                indicatorParams.marginStart = lastStartMargin + deltaX
                                topLineParams.marginStart = lastStartMargin + deltaX
                                bottomLineParams.marginStart = lastStartMargin + deltaX
                                currMarginStart = indicatorParams.marginStart
                            }
                        }
                        container.updateViewLayout(leftIndicator, indicatorParams)
                        container.updateViewLayout(topLine, topLineParams)
                        container.updateViewLayout(bottomLine, bottomLineParams)
                    }

                }
                return true
            }
        }

        val touchListenerRight = object : View.OnTouchListener {
            private val indicatorParams =
                rightIndicator.layoutParams as ConstraintLayout.LayoutParams
            private val topLineParams = topLine.layoutParams as ConstraintLayout.LayoutParams
            private val bottomLineParams = bottomLine.layoutParams as ConstraintLayout.LayoutParams
            private var lastEndMargin = 0
            private var initialX = 0
            private var initialY = 0
            private var initialTouchX = 0
            private var initialTouchY = 0

            override fun onTouch(view: View?, motionEvent: MotionEvent?): Boolean {
                when (motionEvent!!.action) {
                    MotionEvent.ACTION_DOWN -> {
                        initialTouchX = (getScreenWidth() - motionEvent.rawX.toInt())
                        initialTouchY = motionEvent.rawY.toInt()
                        nestedScroll.setScrollingEnabled(false)
                        Log.e("initialX", "$initialX")
                        Log.e("initialY", "$initialY")
                        Log.e("initialTouchX", "$initialTouchX")
                        Log.e("initialTouchY", "$initialTouchY")
                    }
                    MotionEvent.ACTION_UP -> {
                        lastEndMargin = indicatorParams.marginEnd
                        currMarginEnd = lastEndMargin
                        nestedScroll.setScrollingEnabled(true)
                    }
                    MotionEvent.ACTION_MOVE -> {
                        val currX = getScreenWidth() - motionEvent.rawX.toInt()
                        val deltaX = (initialTouchX - currX).absoluteValue
                        Log.e("move", "${motionEvent.rawX.toInt()}")
                        val canMoveForward = getScreenWidth() > currMarginStart + currMarginEnd +
                                leftIndicator.width * 3 + PixelsUtils.dpToPx(16) * 2
                        if (currX < initialTouchX) {
                            if (canMoveForward) {
                                indicatorParams.marginEnd = lastEndMargin - deltaX
                                topLineParams.marginEnd = lastEndMargin - deltaX
                                bottomLineParams.marginEnd = lastEndMargin - deltaX
                                currMarginEnd = indicatorParams.marginEnd
                            } else {
                                currMarginEnd -= PixelsUtils.dpToPx(16)
                            }
                        } else {
                            if (canMoveForward) {
                                indicatorParams.marginEnd = lastEndMargin + deltaX
                                topLineParams.marginEnd = lastEndMargin + deltaX
                                bottomLineParams.marginEnd = lastEndMargin + deltaX
                                currMarginEnd = indicatorParams.marginEnd
                            }
                        }
                        container.updateViewLayout(rightIndicator, indicatorParams)
                        container.updateViewLayout(topLine, topLineParams)
                        container.updateViewLayout(bottomLine, bottomLineParams)
                    }

                }
                return true
            }
        }

        leftIndicator.setOnTouchListener(touchListenerLeft)
        rightIndicator.setOnTouchListener(touchListenerRight)
    }

    private fun addEmptyTimeCodeToContainer(container: LinearLayout) {
        val item = View.inflate(context, R.layout.item_edited_timecode, null)
        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        val removeButton = item.findViewById<ImageView>(R.id.imv_remove)
        removeButton.setOnClickListener {
            fadeAnimation(item, false)
            CoroutineScope(Dispatchers.Main).launch {
                delay(500)
                container.removeView(item)
            }
        }
        item.visibility = View.INVISIBLE
        container.addView(item, params)
        fadeAnimation(item, true)
    }

    private fun makeTimeCodeList(): List<TimeCode> {
        val resultList = mutableListOf<TimeCode>()
        timeCodesContainer.children.forEach {
            val etName = ((it as ViewGroup).getChildAt(0) as EditText)
            val etTime = ((it as ViewGroup).getChildAt(1) as EditText)
            if (etName.text.isNotBlank() && etTime.text.isNotBlank()) {
                resultList.add(TimeCode().apply {
                    name = etName.text.toString()
                    timeString = etTime.text.toString()
                })
            }
        }
        return resultList
    }


    private fun getScreenWidth(): Int {
        val displayMetrics = DisplayMetrics()
        activity!!.windowManager.defaultDisplay.getMetrics(displayMetrics)
        return displayMetrics.widthPixels
    }

    private fun removeEditFrame(container: ConstraintLayout) {
        container.removeViews(container.childCount - 4, 4)
    }


}