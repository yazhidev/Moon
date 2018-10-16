package com.yazhi1992.moon.widget

import android.annotation.TargetApi
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewAnimationUtils
import com.yazhi1992.moon.R
import com.yazhi1992.yazhilib.widget.RelativePopupWindow
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import me.drakeet.multitype.MultiTypeAdapter
import java.util.concurrent.TimeUnit

/**
 * Created by zengyazhi on 2018/10/15.
 */
class RecyclerPopupWindow(context: Context, adapter: MultiTypeAdapter) : RelativePopupWindow(context) {

    companion object {
        val ANIM_TIME = 500L
    }

    init {
        contentView = LayoutInflater.from(context).inflate(R.layout.poput_card, null)
        val ry = contentView.findViewById<RecyclerView>(R.id.popRy)
        isFocusable = true
        isOutsideTouchable = true
        setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        // Disable default animation for circular reveal
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            animationStyle = 0
        }
        ry.adapter = adapter
        ry.layoutManager = LinearLayoutManager(context)
    }

    override fun showOnAnchor(anchor: View, vertPos: Int, horizPos: Int, x: Int, y: Int, fitInScreen: Boolean) {
        super.showOnAnchor(anchor, vertPos, horizPos, x, y, fitInScreen)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            circularReveal(anchor)
        }
    }

    var cx = 0
    var cy = 0
    var finalRadius = 0F

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private fun circularReveal(anchor: View) {
        contentView.apply {
            post {
                val anchorLocation = IntArray(2).apply { anchor.getLocationOnScreen(this) }
                measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
                val myW = measuredWidth * anchor.height / measuredHeight
                cx = anchorLocation[0] + measuredWidth + myW * 2
                cy = anchorLocation[1] - anchor.height
                val dx = Math.max(cx, measuredWidth - cx)
                val dy = Math.max(cy, measuredHeight - cy)
                finalRadius = Math.hypot(dx.toDouble(), dy.toDouble()).toFloat()
                ViewAnimationUtils.createCircularReveal(this, cx, cy, 0f, finalRadius).apply {
                    duration = ANIM_TIME
                    start()
                }
            }
        }
    }

    private var dismissing = false

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    override fun dismiss() {
        if(dismissing) return
        dismissing = true
        Observable.timer(ANIM_TIME, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    dismissing = false
                    super.dismiss()
                }
        ViewAnimationUtils.createCircularReveal(contentView, cx, cy, finalRadius, 0f).apply {
            duration = ANIM_TIME
            start()
        }
    }
}