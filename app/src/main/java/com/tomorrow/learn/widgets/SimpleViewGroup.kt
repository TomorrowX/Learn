package com.tomorrow.learn.widgets

import android.content.Context
import android.view.ViewGroup

import android.os.Build
import android.support.annotation.RequiresApi
import android.util.AttributeSet
import android.view.View


class SimpleViewGroup : ViewGroup {

    @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
            super(context, attrs, defStyleAttr)

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) :
            super(context, attrs, defStyleAttr, defStyleRes)

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

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val count = childCount
        var height = 0
        for (i in 0 until count) {
            val child = getChildAt(i)
            val layoutParams = child.layoutParams as ViewGroup.MarginLayoutParams

            val x = paddingLeft + layoutParams.leftMargin
            val y = height + paddingTop + layoutParams.topMargin
            val childRight = x + child.measuredWidth
            val childBottom = y + child.measuredHeight

            child.layout(x, y, childRight, childBottom)
            height += child.measuredHeight
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
        var width = 0
        var height = 0

        val cCount = childCount

        /*
         * 根据childView计算的出的宽和高，以及设置的margin计算容器的宽和高，主要用于容器是warp_content时
         */
        for (i in 0 until cCount) {
            val childView = getChildAt(i)
            val cWidth = childView.measuredWidth
            val cHeight = childView.measuredHeight
            val cParams = childView.layoutParams as ViewGroup.MarginLayoutParams
            width += cWidth + cParams.leftMargin + cParams.rightMargin

            height += cHeight + cParams.topMargin + cParams.bottomMargin
        }

        width += paddingLeft + paddingRight
        height += paddingTop + paddingBottom

        val size = Math.max(width, height)

        /*
         * 如果是wrap_content设置为我们计算的值
         * 否则：直接设置为父容器计算的值
         */
        setMeasuredDimension(if (widthMode == View.MeasureSpec.EXACTLY)
            sizeWidth
        else
            size, if (heightMode == View.MeasureSpec.EXACTLY)
            sizeHeight
        else
            size)
    }


    override fun checkLayoutParams(p: ViewGroup.LayoutParams): Boolean {
        return p is ViewGroup.MarginLayoutParams
    }


    //要支持Margin或者其他自定义布局参数的时候，必须要实现下面的三个方法
    override fun generateDefaultLayoutParams(): ViewGroup.LayoutParams {
        return ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    override fun generateLayoutParams(p: ViewGroup.LayoutParams): ViewGroup.LayoutParams {
        return ViewGroup.MarginLayoutParams(p)
    }

    override fun generateLayoutParams(attrs: AttributeSet): ViewGroup.LayoutParams {
        return ViewGroup.MarginLayoutParams(context, attrs)
    }


}
