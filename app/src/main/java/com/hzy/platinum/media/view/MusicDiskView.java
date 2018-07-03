package com.hzy.platinum.media.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.animation.LinearInterpolator;

/**
 * Created by huzongyao on 2018/7/2.
 * To Show some animation when music playing
 */

public class MusicDiskView extends android.support.v7.widget.AppCompatImageView {

    public static final long ONE_ROUND_DURATION = 30_000L;
    public static final int ANIMATION_AMOUNT = 1440;
    public static final float ANIMATION_DEGREE = ANIMATION_AMOUNT / 360f;

    private Paint paint;
    private ValueAnimator valueAnimator;

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

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int h = this.getMeasuredHeight();
        int w = this.getMeasuredWidth();
        int curSquareDim = Math.min(w, h);
        setMeasuredDimension(curSquareDim, curSquareDim);
    }

    private void initAnimation() {
        valueAnimator = ValueAnimator.ofInt(0, ANIMATION_AMOUNT);
        valueAnimator.setDuration(ONE_ROUND_DURATION);
        valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.addUpdateListener(animation -> invalidate());
        valueAnimator.start();
    }

    private void initPaint() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setColor(Color.RED);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int i = (int) valueAnimator.getAnimatedValue();
        canvas.save();
        canvas.rotate(i / ANIMATION_DEGREE, getWidth() / 2f, getHeight() / 2f);
        super.onDraw(canvas);
        canvas.restore();
    }
}
