package com.hypersoft.easyviewflip

import android.animation.Animator
import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout

class EasyViewFlip @JvmOverloads constructor(
    context: Context,
    attr: AttributeSet? = null
) : FrameLayout(context, attr) {

    companion object {
        const val DEFAULT_FLIP_DURATION = 400
        const val DEFAULT_AUTO_FLIP_BACK_TIME = 1000
    }

    private val animFlipHorizontalOutId = R.animator.animation_horizontal_flip_out
    private val animFlipHorizontalInId = R.animator.animation_horizontal_flip_in
    private val animFlipHorizontalRightOutId = R.animator.animation_horizontal_right_out
    private val animFlipHorizontalRightInId = R.animator.animation_horizontal_right_in
    private val animFlipVerticalOutId = R.animator.animation_vertical_flip_out
    private val animFlipVerticalInId = R.animator.animation_vertical_flip_in
    private val animFlipVerticalFrontOutId = R.animator.animation_vertical_front_out
    private val animFlipVerticalFrontInId = R.animator.animation_vertical_flip_front_in

    enum class FlipState {
        FRONT_SIDE, BACK_SIDE
    }

    private lateinit var mSetRightOut: AnimatorSet
    private lateinit var mSetLeftIn: AnimatorSet
    private lateinit var mSetTopOut: AnimatorSet
    private lateinit var mSetBottomIn: AnimatorSet
    private var mIsBackVisible = false
    private var mCardFrontLayout: View? = null
    private var mCardBackLayout: View? = null
    private var flipType: String = "vertical"
    private var flipTypeFrom: String = "right"

    private var flipOnTouch: Boolean = false
    private var flipDuration: Int = 0
    private var flipEnabled: Boolean = false
    private var flipOnceEnabled: Boolean = false
    private var autoFlipBack: Boolean = false
    private var autoFlipBackTime: Int = 0

    private var x1: Float = 0f
    private var y1: Float = 0f

    private var mFlipState: FlipState = FlipState.FRONT_SIDE

    private var onFlipListener: OnFlipAnimationListener? = null

    private lateinit var gestureDetector: GestureDetector

    init {
        // Setting Default Values
        flipOnTouch = true
        flipDuration = DEFAULT_FLIP_DURATION
        flipEnabled = true
        flipOnceEnabled = false
        autoFlipBack = false
        autoFlipBackTime = DEFAULT_AUTO_FLIP_BACK_TIME

        // Check for the attributes
        attr?.let { attributeSet ->
            // Attribute initialization
            val attrArray =
                context.obtainStyledAttributes(attributeSet, R.styleable.easy_flip_view, 0, 0)
            try {
                flipOnTouch = attrArray.getBoolean(R.styleable.easy_flip_view_flipOnTouch, true)
                flipDuration =
                    attrArray.getInt(R.styleable.easy_flip_view_flipDuration, DEFAULT_FLIP_DURATION)
                flipEnabled = attrArray.getBoolean(R.styleable.easy_flip_view_flipEnabled, true)
                flipOnceEnabled =
                    attrArray.getBoolean(R.styleable.easy_flip_view_flipOnceEnabled, false)
                autoFlipBack = attrArray.getBoolean(R.styleable.easy_flip_view_autoFlipBack, false)
                autoFlipBackTime = attrArray.getInt(
                    R.styleable.easy_flip_view_autoFlipBackTime,
                    DEFAULT_AUTO_FLIP_BACK_TIME
                )
                flipType = attrArray.getString(R.styleable.easy_flip_view_flipType) ?: "vertical"
                flipTypeFrom = attrArray.getString(R.styleable.easy_flip_view_flipFrom) ?: "left"

                // Uncomment and use if needed
                // animFlipInId = attrArray.getResourceId(R.styleable.easy_flip_view_animFlipInId, R.animator.animation_horizontal_flip_in)
                // animFlipOutId = attrArray.getResourceId(R.styleable.easy_flip_view_animFlipOutId, R.animator.animation_horizontal_flip_out)
            } finally {
                attrArray.recycle()
            }
        }
        loadAnimations()
    }

    override fun onFinishInflate() {
        super.onFinishInflate()

        require(childCount >= 2) { " onFinishInflate -> EasyFlipView can host only two direct children!" }

        findViews()
        changeCameraDistance()
        setupInitializations()
        initGestureDetector()
    }

    override fun addView(child: View?, params: ViewGroup.LayoutParams?) {

        require(childCount < 2) { "addView -> EasyFlipView can host only two direct children!" }

        super.addView(child, params)

        Log.d("EasyViewFlip", "addView: childCount: $childCount")

        findViews()
        changeCameraDistance()
    }

    override fun removeView(view: View?) {
        super.removeView(view)

        findViews()
    }

    override fun removeAllViewsInLayout() {
        super.removeAllViewsInLayout()

        // Reset the state
        mFlipState = FlipState.FRONT_SIDE

        findViews()
    }

    private fun findViews() {
        // Invalidation since we use this also on removeView
        mCardBackLayout = null
        mCardFrontLayout = null

        val childCount = childCount
        if (childCount < 1) {
            return
        }

        if (childCount < 2) {
            // Only invalidate flip state if we have a single child
            mFlipState = FlipState.FRONT_SIDE
            mCardFrontLayout = getChildAt(0)
        } else if (childCount == 2) {
            mCardFrontLayout = getChildAt(1)
            mCardBackLayout = getChildAt(0)
        }

        if (!isFlipOnTouch()) {
            mCardFrontLayout?.visibility = View.VISIBLE
            mCardBackLayout?.visibility = View.GONE
        }
    }

    private fun setupInitializations() {
        mCardBackLayout?.visibility = View.GONE
    }

    private fun initGestureDetector() {
        gestureDetector = GestureDetector(this.context, SwipeDetector())
    }

    private fun loadAnimations() {
        if (flipType.equals("horizontal", ignoreCase = true)) {
            if (flipTypeFrom.equals("left", ignoreCase = true)) {
                mSetRightOut = AnimatorInflater.loadAnimator(this.context, animFlipHorizontalOutId) as AnimatorSet
                mSetLeftIn = AnimatorInflater.loadAnimator(this.context, animFlipHorizontalInId) as AnimatorSet
            } else {
                mSetRightOut = AnimatorInflater.loadAnimator(this.context, animFlipHorizontalRightOutId) as AnimatorSet
                mSetLeftIn = AnimatorInflater.loadAnimator(this.context, animFlipHorizontalRightInId) as AnimatorSet
            }

            if (mSetRightOut == null || mSetLeftIn == null) {
                throw RuntimeException("No Animations Found! Please set Flip in and Flip out animation Ids.")
            }

            mSetRightOut.removeAllListeners()
            mSetRightOut.addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animator: Animator) {}

                override fun onAnimationEnd(animator: Animator) {
                    if (mFlipState == FlipState.FRONT_SIDE) {
                        mCardBackLayout?.visibility = View.GONE
                        mCardFrontLayout?.visibility = View.VISIBLE
                        onFlipListener?.onViewFlipCompleted(this@EasyViewFlip, FlipState.FRONT_SIDE)
                    } else {
                        mCardBackLayout?.visibility = View.VISIBLE
                        mCardFrontLayout?.visibility = View.GONE
                        onFlipListener?.onViewFlipCompleted(this@EasyViewFlip, FlipState.BACK_SIDE)

                        // Auto Flip Back
                        if (autoFlipBack) {
                            Handler(Looper.getMainLooper()).postDelayed({
                                flipTheView()
                            }, autoFlipBackTime.toLong())
                        }
                    }
                }

                override fun onAnimationCancel(animator: Animator) {}

                override fun onAnimationRepeat(animator: Animator) {}
            })
            setFlipDuration(flipDuration)
        } else {
            if (flipTypeFrom.isNotEmpty() && flipTypeFrom.equals("front", ignoreCase = true)) {
                mSetTopOut = AnimatorInflater.loadAnimator(this.context, animFlipVerticalFrontOutId) as AnimatorSet
                mSetBottomIn = AnimatorInflater.loadAnimator(this.context, animFlipVerticalFrontInId) as AnimatorSet
            } else {
                mSetTopOut = AnimatorInflater.loadAnimator(this.context, animFlipVerticalOutId) as AnimatorSet
                mSetBottomIn = AnimatorInflater.loadAnimator(this.context, animFlipVerticalInId) as AnimatorSet
            }

            if (mSetTopOut == null || mSetBottomIn == null) {
                throw RuntimeException("No Animations Found! Please set Flip in and Flip out animation Ids.")
            }

            mSetTopOut.removeAllListeners()
            mSetTopOut.addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animator: Animator) {}

                override fun onAnimationEnd(animator: Animator) {
                    if (mFlipState == FlipState.FRONT_SIDE) {
                        mCardBackLayout?.visibility = View.GONE
                        mCardFrontLayout?.visibility = View.VISIBLE
                        onFlipListener?.onViewFlipCompleted(this@EasyViewFlip, FlipState.FRONT_SIDE)
                    } else {
                        mCardBackLayout?.visibility = View.VISIBLE
                        mCardFrontLayout?.visibility = View.GONE
                        onFlipListener?.onViewFlipCompleted(this@EasyViewFlip, FlipState.BACK_SIDE)

                        // Auto Flip Back
                        if (autoFlipBack) {
                            Handler(Looper.getMainLooper()).postDelayed({
                                flipTheView()
                            }, autoFlipBackTime.toLong())
                        }
                    }
                }

                override fun onAnimationCancel(animator: Animator) {}

                override fun onAnimationRepeat(animator: Animator) {}
            })
            setFlipDuration(flipDuration)
        }
    }

    private fun changeCameraDistance() {
        val distance = 8000
        val scale = resources.displayMetrics.density * distance

        mCardFrontLayout?.cameraDistance = scale
        mCardBackLayout?.cameraDistance = scale
    }

    /**
     * Play the animation of flipping and flip the view for one side!
     */
    fun flipTheView() {

        Log.d("EasyViewFlip", "flipTheView: started")

        if (!flipEnabled || childCount < 2) return

        Log.d("EasyViewFlip", "flipTheView: flipEnabled check is passed")

        if (flipOnceEnabled && mFlipState == FlipState.BACK_SIDE) return

        Log.d("EasyViewFlip", "flipTheView: flipOnceEnabled is passed")

        if (flipType.equals("horizontal", ignoreCase = true)) {

            Log.d("EasyViewFlip", "flipTheView: flipType equals horizontal")
            if (mSetRightOut.isRunning || mSetLeftIn.isRunning) return
            Log.d("EasyViewFlip", "flipTheView: mSetRightOut is running")

            mCardBackLayout?.visibility = View.VISIBLE
            mCardFrontLayout?.visibility = View.VISIBLE

            if (mFlipState == FlipState.FRONT_SIDE) {
                Log.d("EasyViewFlip", "flipTheView: mFlipState check passed")
                // From front to back
                mSetRightOut.setTarget(mCardFrontLayout)
                mSetLeftIn.setTarget(mCardBackLayout)
                mSetRightOut.start()
                mSetLeftIn.start()
                mIsBackVisible = true
                mFlipState = FlipState.BACK_SIDE
            } else {
                Log.d("EasyViewFlip", "flipTheView: mFlipState else check passed")
                // From back to front
                mSetRightOut.setTarget(mCardBackLayout)
                mSetLeftIn.setTarget(mCardFrontLayout)
                mSetRightOut.start()
                mSetLeftIn.start()
                mIsBackVisible = false
                mFlipState = FlipState.FRONT_SIDE
            }
        } else {
            Log.d("EasyViewFlip", "flipTheView: flipType not equals horizontal. else case")

            if (mSetTopOut.isRunning || mSetBottomIn.isRunning) return
            Log.d("EasyViewFlip", "flipTheView: mSetTopOut check is passed")

            mCardBackLayout?.visibility = View.VISIBLE
            mCardFrontLayout?.visibility = View.VISIBLE

            if (mFlipState == FlipState.FRONT_SIDE) {
                Log.d("EasyViewFlip", "flipTheView: mFlipState of else if case")
                // From front to back
                mSetTopOut.setTarget(mCardFrontLayout)
                mSetBottomIn.setTarget(mCardBackLayout)
                mSetTopOut.start()
                mSetBottomIn.start()
                mIsBackVisible = true
                mFlipState = FlipState.BACK_SIDE
            } else {
                Log.d("EasyViewFlip", "flipTheView: mFlipState of else else case")

                // From back to front
                mSetTopOut.setTarget(mCardBackLayout)
                mSetBottomIn.setTarget(mCardFrontLayout)
                mSetTopOut.start()
                mSetBottomIn.start()
                mIsBackVisible = false
                mFlipState = FlipState.FRONT_SIDE
            }
        }
    }

    /**
     * Flip the view for one side with or without animation.
     *
     * @param withAnimation true means flip view with animation otherwise without animation.
     */

    fun flipTheView(withAnimation: Boolean) {
        if (childCount < 2) return

        if (flipType.equals("horizontal", ignoreCase = true)) {
            if (!withAnimation) {
                mSetLeftIn.duration = 0
                mSetRightOut.duration = 0
                val oldFlipEnabled = flipEnabled
                flipEnabled = true

                flipTheView()

                mSetLeftIn.duration = flipDuration.toLong()
                mSetRightOut.duration = flipDuration.toLong()
                flipEnabled = oldFlipEnabled
            } else {
                flipTheView()
            }
        } else {
            if (!withAnimation) {
                mSetBottomIn.duration = 0
                mSetTopOut.duration = 0
                val oldFlipEnabled = flipEnabled
                flipEnabled = true

                flipTheView()

                mSetBottomIn.duration = flipDuration.toLong()
                mSetTopOut.duration = flipDuration.toLong()
                flipEnabled = oldFlipEnabled
            } else {
                flipTheView()
            }
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        return try {
            ev?.let { gestureDetector.onTouchEvent(it) } == true || super.dispatchTouchEvent(ev)
        } catch (throwable: Throwable) {
            throw IllegalStateException("Error in dispatchTouchEvent: ", throwable)
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return if (isEnabled && flipOnTouch) {
            event?.let { gestureDetector.onTouchEvent(it) } ?: true
        } else {
            super.onTouchEvent(event)
        }
    }

    /**
     * Whether view is set to flip on touch or not.
     *
     * @return true or false
     */
    fun isFlipOnTouch(): Boolean {
        return flipOnTouch
    }

    /**
     * Set whether view should be flipped on touch or not!
     *
     * @param flipOnTouch value (true or false)
     */
    fun setFlipOnTouch(flipOnTouch: Boolean) {
        this.flipOnTouch = flipOnTouch
    }

    /**
     * Returns duration of flip in milliseconds!
     *
     * @return duration in milliseconds
     */
    fun getFlipDuration(): Int {
        return flipDuration
    }

    /**
     * Sets the flip duration (in milliseconds)
     *
     * @param flipDuration duration in milliseconds
     */
    fun setFlipDuration(flipDuration: Int) {
        this.flipDuration = flipDuration
        if (flipType.equals("horizontal", ignoreCase = true)) {
            // mSetRightOut.setDuration(flipDuration)
            mSetRightOut.childAnimations[0].duration = flipDuration.toLong()
            mSetRightOut.childAnimations[1].startDelay = (flipDuration / 2).toLong()

            // mSetLeftIn.setDuration(flipDuration)
            mSetLeftIn.childAnimations[1].duration = flipDuration.toLong()
            mSetLeftIn.childAnimations[2].startDelay = (flipDuration / 2).toLong()
        } else {
            mSetTopOut.childAnimations[0].duration = flipDuration.toLong()
            mSetTopOut.childAnimations[1].startDelay = (flipDuration / 2).toLong()

            mSetBottomIn.childAnimations[1].duration = flipDuration.toLong()
            mSetBottomIn.childAnimations[2].startDelay = (flipDuration / 2).toLong()
        }
    }

    /**
     * Returns whether view can be flipped only once!
     *
     * @return true or false
     */
    fun isFlipOnceEnabled(): Boolean {
        return flipOnceEnabled
    }

    /**
     * Enable / Disable flip only once feature.
     *
     * @param flipOnceEnabled true or false
     */
    fun setFlipOnceEnabled(flipOnceEnabled: Boolean) {
        this.flipOnceEnabled = flipOnceEnabled
    }

    /**
     * Returns whether flip is enabled or not!
     *
     * @return true or false
     */
    fun isFlipEnabled(): Boolean {
        return flipEnabled
    }

    /**
     * Enable / Disable flip view.
     *
     * @param flipEnabled true or false
     */
    fun setFlipEnabled(flipEnabled: Boolean) {
        this.flipEnabled = flipEnabled
    }

    /**
     * Returns which flip state is currently on of the flip view.
     *
     * @return current state of flip view
     */
    fun getCurrentFlipState(): FlipState {
        return mFlipState
    }

    /**
     * Returns true if the front side of flip view is visible.
     *
     * @return true if the front side of flip view is visible.
     */
    fun isFrontSide(): Boolean {
        return (mFlipState == FlipState.FRONT_SIDE)
    }

    /**
     * Returns true if the back side of flip view is visible.
     *
     * @return true if the back side of flip view is visible.
     */
    fun isBackSide(): Boolean {
        return (mFlipState == FlipState.BACK_SIDE)
    }

    fun getOnFlipListener(): OnFlipAnimationListener? {
        return onFlipListener
    }

    fun setOnFlipListener(onFlipListener: OnFlipAnimationListener?) {
        this.onFlipListener = onFlipListener
    }

    /*
    public @AnimatorRes int getAnimFlipOutId() {
        return animFlipOutId;
    }

    public void setAnimFlipOutId(@AnimatorRes int animFlipOutId) {
        this.animFlipOutId = animFlipOutId;
        loadAnimations();
    }

    public @AnimatorRes int getAnimFlipInId() {
        return animFlipInId;
    }

    public void setAnimFlipInId(@AnimatorRes int animFlipInId) {
        this.animFlipInId = animFlipInId;
        loadAnimations();
    }
    */
    /**
     * Returns true if the Flip Type of animation is Horizontal?
     */
    fun isHorizontalType(): Boolean {
        return flipType == "horizontal"
    }

    /**
     * Returns true if the Flip Type of animation is Vertical?
     */
    fun isVerticalType(): Boolean {
        return flipType == "vertical"
    }

    /**
     * Sets the Flip Type of animation to Horizontal
     */
    fun setToHorizontalType() {
        flipType = "horizontal"
        loadAnimations()
    }

    /**
     * Sets the Flip Type of animation to Vertical
     */
    fun setToVerticalType() {
        flipType = "vertical"
        loadAnimations()
    }

    /**
     * Sets the flip type from direction to right
     */
    fun setFlipTypeFromRight() {
        flipTypeFrom = if (flipType == "horizontal") "right"
        else "front"
        loadAnimations()
    }

    /**
     * Sets the flip type from direction to left
     */
    fun setFlipTypeFromLeft() {
        flipTypeFrom = if (flipType == "horizontal") "left"
        else "back"
        loadAnimations()
    }

    /**
     * Sets the flip type from direction to front
     */
    fun setFlipTypeFromFront() {
        flipTypeFrom = if (flipType == "vertical") "front"
        else "right"
        loadAnimations()
    }

    /**
     * Sets the flip type from direction to back
     */
    fun setFlipTypeFromBack() {
        flipTypeFrom = if (flipType == "vertical") "back"
        else "left"
        loadAnimations()
    }

    /**
     * Returns the flip type from direction. For horizontal, it will be either right or left and for vertical, it will be front or back.
     */
    fun getFlipTypeFrom(): String {
        return flipTypeFrom
    }

    /**
     * Returns true if Auto Flip Back is enabled
     */
    fun isAutoFlipBack(): Boolean {
        return autoFlipBack
    }

    /**
     * Set if the card should be flipped back to original front side.
     * @param autoFlipBack true if card should be flipped back to froont side
     */
    fun setAutoFlipBack(autoFlipBack: Boolean) {
        this.autoFlipBack = autoFlipBack
    }

    /**
     * Return the time in milliseconds to auto flip back to original front side.
     * @return
     */
    fun getAutoFlipBackTime(): Int {
        return autoFlipBackTime
    }

    /**
     * Set the time in milliseconds to auto flip back the view to the original front side
     * @param autoFlipBackTime The time in milliseconds
     */
    fun setAutoFlipBackTime(autoFlipBackTime: Int) {
        this.autoFlipBackTime = autoFlipBackTime
    }

    /**
     * The Flip Animation Listener for animations and flipping complete listeners
     */
    interface OnFlipAnimationListener {
        /**
         * Called when flip animation is completed.
         *
         * @param newCurrentSide After animation, the new side of the view. Either can be
         * FlipState.FRONT_SIDE or FlipState.BACK_SIDE
         */
        fun onViewFlipCompleted(
            easyFlipView: EasyViewFlip?,
            newCurrentSide: FlipState?
        )
    }

    private inner class SwipeDetector : GestureDetector.SimpleOnGestureListener() {

        override fun onFling(
            e1: MotionEvent?,
            e2: MotionEvent,
            velocityX: Float,
            velocityY: Float
        ): Boolean {
            return false
        }

        override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
            if (isEnabled && flipOnTouch) {
                flipTheView()
            }
            return super.onSingleTapConfirmed(e)
        }

        override fun onDown(e: MotionEvent): Boolean {
            return if (isEnabled && flipOnTouch) {
                true
            } else {
                super.onDown(e)
            }
        }
    }


}