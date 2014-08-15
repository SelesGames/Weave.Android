package com.selesgames.weave.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.ImageButton;

import com.selesgames.weave.R;

public class CircleButton extends ImageButton {

    private CircularDrawable mCircularDrawable;

    public CircleButton(Context context) {
        super(context);
    }

    public CircleButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public CircleButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @SuppressWarnings("deprecation")
    @SuppressLint("NewApi")
    public void init(Context context, AttributeSet attributeSet) {

        int transitionTime = 0;
        int color = Color.BLACK;

        TypedArray arr = context.obtainStyledAttributes(attributeSet, R.styleable.CircleButton);
        CharSequence colorAtr = arr.getString(R.styleable.CircleButton_color);
        if (colorAtr != null) {
            color = Color.parseColor(String.valueOf(colorAtr));
        }

        arr.recycle();

        // Crate background drawable and set color
        mCircularDrawable = new CircularDrawable(color);
        mCircularDrawable.setTransitionTime(transitionTime);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            setBackgroundDrawable(mCircularDrawable);
        } else {
            setBackground(mCircularDrawable);
        }

    }

    public void changeColor(int color) {
        mCircularDrawable.animateTo(color);
    }

}