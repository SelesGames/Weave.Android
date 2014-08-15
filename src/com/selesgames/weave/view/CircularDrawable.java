package com.selesgames.weave.view;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.ShapeDrawable;

public class CircularDrawable extends ShapeDrawable {

    private int mColorNormal;

    private int mColorPressed;

    private Paint mPaint;

    private Integer mTransitionTime = 0;

    public CircularDrawable(int color) {
        mPaint = new Paint();

        mColorNormal = color;

        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] = 1.0f - 0.8f * (1.0f - hsv[2]);
        mColorPressed = Color.HSVToColor(hsv);

        // setShape(new OvalShape());
    }

    @Override
    public int getOpacity() {
        return PixelFormat.OPAQUE;
    }

    @Override
    protected boolean onStateChange(int[] states) {
        invalidateSelf();
        return true;
    }

    @Override
    public boolean isStateful() {
        return true;
    }

    @Override
    public void draw(Canvas canvas) {
        for (int state : getState()) {
            if (state == android.R.attr.state_pressed || state == android.R.attr.state_focused) {
                mPaint.setColor(mColorPressed);
                break;
            } else {
                mPaint.setColor(mColorNormal);
            }
        }
        int halfWidth = canvas.getWidth() / 2;
        int halfHeight = canvas.getHeight() / 2;
        int radius = Math.min(halfWidth, halfHeight);
        canvas.drawCircle(halfWidth, halfHeight, radius, mPaint);
    }

    public void animateTo(int color) {
        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), mPaint.getColor(), color);
        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                mPaint.setColor((Integer) animator.getAnimatedValue());
                invalidateSelf();
            }

        });

        if (mTransitionTime != 0) {
            colorAnimation.setDuration(mTransitionTime);
        }
        colorAnimation.start();
    }

    public Integer getTransitionTime() {
        return mTransitionTime;
    }

    public void setTransitionTime(Integer transitionTime) {
        this.mTransitionTime = transitionTime;
    }
}