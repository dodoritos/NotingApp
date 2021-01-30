package com.example.emptyproject;

import android.graphics.Rect;
import android.util.Log;

/**
 * Class containing a rectangle, detects if a smaller rectangle inside it is colliding
 */
public class MovingLimitor {

    private Rect limitRectangle;

    public MovingLimitor(Rect rect) {
        limitRectangle = rect;
    }

    public void setLimitRectangle(Rect newRectangle) {
        limitRectangle = newRectangle;
    }

    public boolean isColiding(Rect rect) {
        return this.isColidingLeft(rect);
    }

    public boolean isColidingLeft(Rect rect) {
        return limitRectangle.left >= rect.left;
    }

    public boolean isColidingRight(Rect rect) {
        return limitRectangle.right <= rect.right;
    }

    public boolean isColidingUp(Rect rect) {
        return limitRectangle.top >= rect.top;
    }

    public boolean isColidingBottom(Rect rect) {
        return limitRectangle.bottom <= rect.bottom;
    }
}
