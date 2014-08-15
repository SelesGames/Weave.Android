package com.selesgames.weave.view;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.Point;
import android.graphics.RectF;
import android.view.View;
import android.widget.FrameLayout;

public class ArcPositioner {

    private int mStartAngle;
    private int mEndAngle;
    private int mRadius;
    private Point mCenterPoint;
    private List<Item> mItems;

    private ArcPositioner(int startAngle, int endAngle, int radius, Point centerPoint, List<Item> items) {
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
        }
    }

    public void position() {
        calculateItemPositions();
        
        for (int i = 0; i < mItems.size(); i++) {
            Item item = mItems.get(i);
            View view = item.view;
            
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) view.getLayoutParams();
            params.setMargins(item.x, item.y, 0, 0);
            view.setLayoutParams(params);
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

        public ArcPositioner build() {
            return new ArcPositioner(mStartAngle, mEndAngle, mRadius, mCenterPoint, mItems);
        }
    }
}
