package com.hzy.platinum.media.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.animation.LinearInterpolator;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

/**
 * Created by huzongyao on 2018/7/2.
 * To Show some animation when music playing
 */

public class MusicDiskView extends AppCompatImageView {

    public static final long ONE_ROUND_DURATION = 30_000L;
    public static final int ANIMATION_AMOUNT = 1440;
    public static final float ANIMATION_DEGREE = 360f / ANIMATION_AMOUNT;
    public static final float SIN_45 = 0.7f;

    private Paint mCirclePaint;
    private ValueAnimator mValueAnimator;
    private float mHalfWidth;
    private float mCircleRadius;
    private float mCurrentDegree;

    public MusicDiskView(Context context) {
        this(context, null);
    }

    public MusicDiskView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MusicDiskView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaint();
        initAnimation();
    }

    private void initPaint() {
        mCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCirclePaint.setStyle(Paint.Style.STROKE);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int squareDim = 1000000000;
        int h = this.getMeasuredHeight();
        int w = this.getMeasuredWidth();
        int curSquareDim = Math.min(w, h);
        if (curSquareDim < squareDim) {
            squareDim = curSquareDim;
        }
        setMeasuredDimension(squareDim, squareDim);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mHalfWidth = w / 2f;
        float mHalfCircle = mHalfWidth * SIN_45;
        float mStrokeWidth = mHalfWidth - mHalfCircle;
        mCirclePaint.setStrokeWidth(mStrokeWidth);
        mCircleRadius = mHalfWidth - mStrokeWidth / 2f;
        int padding = (int) mStrokeWidth;
        setPadding(padding, padding, padding, padding);
    }

    private void initAnimation() {
        mValueAnimator = ValueAnimator.ofInt(0, ANIMATION_AMOUNT);
        mValueAnimator.setDuration(ONE_ROUND_DURATION);
        mValueAnimator.setRepeatCount(ValueAnimator.INFINITE);
        mValueAnimator.setInterpolator(new LinearInterpolator());
        mValueAnimator.addUpdateListener(animation -> {
            mCurrentDegree += ANIMATION_DEGREE;
            if (mCurrentDegree >= 360) {
                mCurrentDegree = 0;
            }
            invalidate();
        });
        mValueAnimator.start();
    }

    @Override
    public void setImageDrawable(@Nullable Drawable drawable) {
        super.setImageDrawable(drawable);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();
        canvas.rotate(mCurrentDegree, mHalfWidth, mHalfWidth);
        super.onDraw(canvas);
        canvas.drawCircle(mHalfWidth, mHalfWidth, mCircleRadius, mCirclePaint);
        canvas.restore();
    }

    public void start() {
        if (!mValueAnimator.isRunning()) {
            mValueAnimator.start();
        }
    }

    public void stop() {
        if (mValueAnimator.isRunning()) {
            mValueAnimator.cancel();
        }
    }
}
