package com.tomorrow.learn.widgets

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.ViewGroup

import android.os.Build
import android.support.annotation.RequiresApi
import android.util.AttributeSet
import android.util.Log
import android.view.View
import com.tomorrow.learn.BuildConfig
import com.tomorrow.learn.R
import java.lang.ref.WeakReference

/**
 * 自定义简单的ViewGroup
 * 需要实现一个让view在容器里面按照圆形排布，然后会自动旋转的容器
 * 实现步骤
 * 1：让child按照圆形排布
 * 2，让child动起来
 */
class SimpleViewGroup : ViewGroup {

    companion object {
        private const val DEFAULT_RADIUS = 100F
        private const val DEFAULT_PERIOD = 5_000L
        private val DEBUG = BuildConfig.DEBUG
    }

    //确定半径有多大
    var radius: Float = DEFAULT_RADIUS

    //是否自动旋转
    var autoRotate = true

    //旋转周期(ms)
    var period: Long = DEFAULT_PERIOD

    /*绘制debug线的按钮*/
    private var mPaint = Paint()

    /*旋转的runnable*/
    private var runnable: RotateRunnable

    @JvmOverloads
    constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
            super(context, attrs, defStyleAttr) {
        runnable = RotateRunnable(this)
        initView(context, attrs)
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) :
            super(context, attrs, defStyleAttr, defStyleRes) {
        runnable = RotateRunnable(this)
        initView(context, attrs)
    }

    private fun initView(context: Context, attrs: AttributeSet?) {
        mPaint.style = Paint.Style.STROKE
        mPaint.color = Color.RED
        mPaint.strokeWidth = 3F
        mPaint.isAntiAlias = true

        if (DEBUG) {
            //如果不设置背景色的话，不会调用onDraw方法
            setBackgroundColor(Color.WHITE)
            //或者设置下面这个也可以
//            setWillNotDraw(false)
        }

        if (attrs != null) {
            val typedArray = context.obtainStyledAttributes(attrs, R.styleable.SimpleViewGroup)
            autoRotate = typedArray.getBoolean(R.styleable.SimpleViewGroup_autoRotate, true)
            period = typedArray.getInteger(R.styleable.SimpleViewGroup_period, DEFAULT_PERIOD.toInt()).toLong()
            radius = typedArray.getDimension(R.styleable.SimpleViewGroup_radius, DEFAULT_RADIUS)
            typedArray.recycle()
        }
    }

    fun startCycle() {
        autoRotate = true
        requestLayout()
    }

    fun stopCycle() {
        autoRotate = false
    }

    /**
     * 自定义viewGroup的时候一把只需要实现
     * protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
     * 与
     * protected void onLayout(boolean changed, int l, int t, int r, int b)
     *
     * onMeasure的作用是测量自身view的大小以及子view的大小
     *
     * onLayout的作用是确定子view的显示位置
     */
    private var currentRadian = 0.0

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {

        //首先我们要找到原点,既这个容器的中心点

        val originX = (left + right) / 2
        val originY = (top + bottom) / 2
        //根据子view的个数算出分成多少分，一个圆的弧度是2pi
        val count = childCount
        //每个view占的弧度
        val perRadian = 2 * Math.PI / count
        //我们从上顶点开始排布
        //记录当前弧度
        var radian = if (autoRotate) {
            currentRadian + perRadian / count
        } else {
            currentRadian
        }

        Log.i("TEST", "start radian = $radian")
        for (i in 0 until count) {
            val child = getChildAt(i)
            //第一个就是(originX,y - radius)
            val x = originX + radius * Math.sin(radian)
            val y = originY - radius * Math.cos(radian)

            val w = child.measuredWidth
            val h = child.measuredHeight
            //将子view的中心点移动到这个点上
            val centerX = x.toInt() - w / 2
            val centerY = y.toInt() - h / 2

            val childRight = centerX + w
            val childBottom = centerY + h

            child.layout(centerX, centerY, childRight, childBottom)
            radian += perRadian
        }
        currentRadian = radian
        Log.i("TEST", "start currentRadian = $radian")
        if (autoRotate) {
            postDelayed(runnable, period / (count * count))
        }
    }

    private class RotateRunnable(view: View) : Runnable {
        private val ref: WeakReference<View> = WeakReference(view)
        override fun run() {
            val view = ref.get() ?: return
            view.requestLayout()
        }
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        /*
         * 获得此ViewGroup上级容器为其推荐的宽和高，以及计算模式
         */
        val widthMode = View.MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = View.MeasureSpec.getMode(heightMeasureSpec)
        val sizeWidth = View.MeasureSpec.getSize(widthMeasureSpec)
        val sizeHeight = View.MeasureSpec.getSize(heightMeasureSpec)


        // 计算出所有的childView的宽和高，调用后，它所有的childView的宽和高的值就被确定，也即getMeasuredWidth（）有值了。
        measureChildren(widthMeasureSpec, heightMeasureSpec)
        /*
         * 记录如果是wrap_content是设置的宽和高
         */
        var width: Int = (radius * 2).toInt()
        var height: Int = (radius * 2).toInt()

        val count = childCount

        if (count > 0) {
            val childView = getChildAt(0)
            val cWidth = childView.measuredWidth
            val cHeight = childView.measuredHeight
            val cParams = childView.layoutParams as ViewGroup.MarginLayoutParams
            width += cWidth + cParams.leftMargin + cParams.rightMargin

            height += cHeight + cParams.topMargin + cParams.bottomMargin
        }
//
//        /*
//         * 根据childView计算的出的宽和高，以及设置的margin计算容器的宽和高，主要用于容器是warp_content时
//         */
//        for (i in 0 until cCount) {
//            val childView = getChildAt(i)
//            val cWidth = childView.measuredWidth
//            val cHeight = childView.measuredHeight
//            val cParams = childView.layoutParams as ViewGroup.MarginLayoutParams
//            width += cWidth + cParams.leftMargin + cParams.rightMargin
//
//            height += cHeight + cParams.topMargin + cParams.bottomMargin
//        }

        width += paddingLeft + paddingRight
        height += paddingTop + paddingBottom

        val size = Math.max(width, height)

        val myWidth = if (widthMode == View.MeasureSpec.EXACTLY) {
            sizeWidth
        } else {
            size
        }

        val myHeight = if (heightMode == View.MeasureSpec.EXACTLY) {
            sizeHeight
        } else {
            size
        }

        /*
         * 如果是wrap_content设置为我们计算的值
         * 否则：直接设置为父容器计算的值
         */
        setMeasuredDimension(myWidth, myHeight)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        val originX = (x + width) / 2F
        val originY = (y + height) / 2F
        canvas?.drawLine(0F, originY, width.toFloat(), originY, mPaint)
        canvas?.drawLine(originX, 0F, originX, height.toFloat(), mPaint)
        canvas?.drawCircle(originX, originY, radius, mPaint)
    }


    override fun checkLayoutParams(p: ViewGroup.LayoutParams): Boolean {
        return p is LayoutParams
    }


    //要支持Margin或者其他自定义布局参数的时候，必须要实现下面的三个方法
    override fun generateDefaultLayoutParams(): ViewGroup.LayoutParams {
        return LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    override fun generateLayoutParams(p: ViewGroup.LayoutParams): ViewGroup.LayoutParams {
        return LayoutParams(p)
    }

    override fun generateLayoutParams(attrs: AttributeSet): ViewGroup.LayoutParams {
        return LayoutParams(context, attrs)
    }


    class LayoutParams : MarginLayoutParams {
        @JvmOverloads
        constructor(c: Context, attrs: AttributeSet? = null) : super(c, attrs)

        constructor(width: Int, height: Int) : super(width, height)

        constructor(p: ViewGroup.LayoutParams) : super(p)
    }

}
