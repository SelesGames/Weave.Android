package com.selesgames.weave.view;

import java.util.ArrayList;
import java.util.List;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.Point;
import android.graphics.RectF;
import android.util.Log;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;

public class ArcAnimator {
    
    /** Animation duration in milliseconds */
    private static final int DURATION = 5000;
    
    /** Pause between each item's animation  */
    private static final int LAG_BETWEEN_ITEMS = 20;

    private int mStartAngle;
    private int mEndAngle;
    private int mRadius;
    private Point mCenterPoint;
    private List<Item> mItems;

    private ArcAnimator(int startAngle, int endAngle, int radius, Point centerPoint, List<Item> items) {
        mStartAngle = startAngle;
        mEndAngle = endAngle;
        mRadius = radius;
        mCenterPoint = centerPoint;
        mItems = items;
    }

    private void calculateItemPositions() {
        // Create an arc that starts from startAngle and ends at endAngle
        // in an area that is as large as 4*radius^2
        RectF area = new RectF(mCenterPoint.x - mRadius, mCenterPoint.y - mRadius, mCenterPoint.x + mRadius,
                mCenterPoint.y + mRadius);

        Path orbit = new Path();
        orbit.addArc(area, mStartAngle, mEndAngle - mStartAngle);

        PathMeasure measure = new PathMeasure(orbit, false);

        // Prevent overlapping when it is a full circle
        int divisor;
        if (Math.abs(mEndAngle - mStartAngle) >= 360 || mItems.size() <= 1) {
            divisor = mItems.size();
        } else {
            divisor = mItems.size() - 1;
        }

        for (int i = 0; i < mItems.size(); i++) {
            Item item = mItems.get(i);
            float[] coords = new float[] { 0f, 0f };
            measure.getPosTan((i) * measure.getLength() / divisor, coords, null);
            item.x = (int) coords[0] - item.width / 2;
            item.y = (int) coords[1] - item.height / 2;
            
            Log.e("TEST", "Coords at " + i + ": " + item.x + ", " + item.y);
        }
    }

    public void animate() {
        // populate destination x,y coordinates of Items
        calculateItemPositions();

        int lastAnimateToX = 0;
        int lastAnimateToY = 0;
        
        for (int i = 0; i < mItems.size(); i++) {
            Item item = mItems.get(i);
            View view = item.view;
            
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) view.getLayoutParams();
            
            // Set the first item as the starting point
            if (i == 0) {
                params.setMargins(item.x, item.y, 0, 0);
                view.setLayoutParams(params);
                continue;
            }
            
            // Position the item to the location of the previous item
            Item previousItem = mItems.get(i-1);
            FrameLayout.LayoutParams previousParams = (FrameLayout.LayoutParams) previousItem.view.getLayoutParams();
            
            int deltaX = previousParams.leftMargin + lastAnimateToX;
            int deltaY = previousParams.topMargin + lastAnimateToY;
            params.setMargins(deltaX, deltaY, previousParams.rightMargin, previousParams.bottomMargin);
            view.setLayoutParams(params);
            
            lastAnimateToX = item.x - deltaX;
            lastAnimateToY = item.y - deltaY;
            
//            view.setScaleX(0);
//            view.setScaleY(0);
//            view.setAlpha(0);
            
            PropertyValuesHolder pvhX = PropertyValuesHolder.ofFloat(View.TRANSLATION_X, item.x - deltaX);
            PropertyValuesHolder pvhY = PropertyValuesHolder.ofFloat(View.TRANSLATION_Y, item.y - deltaY);
            PropertyValuesHolder pvhR = PropertyValuesHolder.ofFloat(View.ROTATION, 720);
            PropertyValuesHolder pvhsX = PropertyValuesHolder.ofFloat(View.SCALE_X, 1);
            PropertyValuesHolder pvhsY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 1);
            PropertyValuesHolder pvhA = PropertyValuesHolder.ofFloat(View.ALPHA, 1);

            ObjectAnimator animation = ObjectAnimator.ofPropertyValuesHolder(view, pvhX, pvhY, pvhR, pvhsX, pvhsY, pvhA);
            animation.setDuration(DURATION);
            animation.setInterpolator(new OvershootInterpolator(0.9f));
            //animation.addListener(new SubActionItemAnimationListener(menu.getSubActionItems().get(i), ActionType.OPENING));

            // Put a slight lag between each of the menu items to make it asymmetric
            animation.setStartDelay((i - 1) * DURATION);
            animation.start();
        }
    }
    
    private static class ItemAnimationListener implements Animator.AnimatorListener {

        @Override
        public void onAnimationCancel(Animator animator) {
            
        }

        @Override
        public void onAnimationEnd(Animator animator) {
            
        }

        @Override
        public void onAnimationRepeat(Animator animator) {
            
        }

        @Override
        public void onAnimationStart(Animator animator) {
            
        }
        
    }

    public static class Item {

        public View view;

        public int width;

        public int height;

        public int x;

        public int y;

        public Item(View view, int width, int height) {
            this.view = view;
            this.width = width;
            this.height = height;
        }
    }

    public static class Builder {

        private int mStartAngle;
        private int mEndAngle;
        private int mRadius;
        private Point mCenterPoint;
        private List<Item> mItems;

        public Builder() {
            mItems = new ArrayList<Item>();
            mRadius = 500;
            mStartAngle = 180;
            mEndAngle = 270;
        }

        public Builder setStartAngle(int startAngle) {
            mStartAngle = startAngle;
            return this;
        }

        public Builder setEndAngle(int endAngle) {
            mEndAngle = endAngle;
            return this;
        }

        public Builder setRadius(int radius) {
            mRadius = radius;
            return this;
        }

        public Builder addView(View view) {
            return this.addView(view, view.getWidth(), view.getHeight());
        }

        public Builder addView(View view, int width, int height) {
            mItems.add(new Item(view, width, height));
            return this;
        }

        public Builder centerAround(Point point) {
            mCenterPoint = point;
            return this;
        }

        public ArcAnimator build() {
            return new ArcAnimator(mStartAngle, mEndAngle, mRadius, mCenterPoint, mItems);
        }
    }
}
