package com.yazhi1992.moon.widget.calendarview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.yazhi1992.moon.R;
import com.yazhi1992.moon.constant.TypeConstant;
import com.yazhi1992.yazhilib.utils.LibCalcUtil;

/**
 * Created by zengyazhi on 2018/4/25.
 */

public class DayView extends View {

    public int mMcType = TypeConstant.MC_NORMAL;
    private Paint mPaint;
    private Context mContext;
    private int mTextSize;//文字尺寸
    private int mTodayTextSize;//文字尺寸
    private int mTextMargin; //日期的左、上margin
    private int mTodayTextRightMargin;  //今天 文字的右margin
    private int mTodayTextBottomMargin;  //今天 文字的下margin
    private int mPaintWidth;
    private int mRadius;
    private int mMargin;
    private RectF mRect;
    private DateBean mDateBean;

    public DayView(Context context) {
        this(context, null);
    }

    public DayView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mRadius = (int) LibCalcUtil.dp2px(context, 4);
        mMargin = (int) LibCalcUtil.dp2px(context, 1);
        mTextMargin = (int) LibCalcUtil.dp2px(context, 6);
        mTodayTextRightMargin = (int) LibCalcUtil.dp2px(context, 4);
        mTodayTextBottomMargin = (int) LibCalcUtil.dp2px(context, 4);
        mTextSize = (int) LibCalcUtil.dp2px(context, 16);
        mTodayTextSize = (int) LibCalcUtil.dp2px(context, 12);
        mPaintWidth = (int) LibCalcUtil.dp2px(context, 1);
        initPaint();
    }

    private void initPaint() {
        mPaint = new Paint();
        mPaint.setStrokeWidth(mPaintWidth);
        mPaint.setAntiAlias(true);
        mPaint.setTextSize(mTextSize);
    }

    public void setDataBean(DateBean dataBean) {
        mDateBean = dataBean;
        mMcType = dataBean.getMcType();
        postInvalidate();
    }

    private void setBgPaintColor() {
        switch (mMcType) {
            case TypeConstant.MC_COME:
            case TypeConstant.MC_GO:
            case TypeConstant.MC_MIDDLE:
                mPaint.setColor(getResources().getColor(R.color.pink));
                break;
            default:
                mPaint.setColor(getResources().getColor(R.color.white));
                break;
        }
    }

    private void setPaintTextColor() {
        switch (mMcType) {
            case TypeConstant.MC_COME:
            case TypeConstant.MC_GO:
            case TypeConstant.MC_MIDDLE:
                mPaint.setColor(getResources().getColor(R.color.white));
                break;
            default:
                mPaint.setColor(getResources().getColor(R.color.mc_green));
                break;
        }
    }

    private void setClickedColor() {
        mPaint.setColor(getResources().getColor(R.color.mc_red));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getWidth();
        int height = getHeight();
        if (mRect == null) {
            mRect = new RectF();
            mRect.left = mMargin;
            mRect.right = width - mMargin;
            mRect.top = mMargin;
            mRect.bottom = height - mMargin;
        }
        //绘制背景
        mPaint.setStyle(Paint.Style.FILL);
        setBgPaintColor();
        canvas.drawRoundRect(mRect, mRadius, mRadius, mPaint);
        if (mDateBean != null) {
            //绘制日期文字
            setPaintTextColor();
            mPaint.setTextSize(mTextSize);
            String textStr = String.valueOf(mDateBean.getDate()[2]);
            float textHeight = Math.abs(mPaint.ascent());
            canvas.drawText(textStr, mTextMargin + mMargin, textHeight + mTextMargin + mMargin, mPaint);
            if (mDateBean.isToday()) {
                String todayStr = "今天";
                mPaint.setTextSize(mTodayTextSize);
                float textWidth = mPaint.measureText(todayStr);
                canvas.drawText(todayStr, width - mMargin - textWidth - mTodayTextRightMargin, height - mMargin - mTodayTextBottomMargin, mPaint);
            }
            //绘制点击状态
            if(mDateBean.isClicked()) {
                setClickedColor();
                mPaint.setStyle(Paint.Style.STROKE);
                canvas.drawRoundRect(mRect, mRadius, mRadius, mPaint);
            }
        }

    }
}
