package com.example.emptyproject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MotionEventCompat;

public class ScrollableDrawingCanva extends DrawingCanvaLayout {

    private final String TAG = "ScrollableDrawingCanva";

    private final float MAX_SCALE_FACTOR = 5f;
    private final float MIN_SCALE_FACTOR = 0.5f;

    private Rect layoutLimitsRectangle = new Rect(-100, -100, 3000, 3000);

    private Rect screenRectangle = new Rect();

    private int activePointerId;

    private MovingLimitor movingLimitor;


    private ScaleGestureDetector scaleGestureDetector;

    private GestureDetector gestureDetector;


    private final GestureDetector.SimpleOnGestureListener gestureListener
            = new GestureDetector.SimpleOnGestureListener() {
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2,
                                float distanceX, float distanceY) {

            translate(distanceX, distanceY);
            invalidate();
            return true;
        }
    };


    public ScrollableDrawingCanva(@NonNull Context context) {
        super(context);
        init(context);
    }

    public ScrollableDrawingCanva(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ScrollableDrawingCanva(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public ScrollableDrawingCanva(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }


    private void init(Context context) {
        scaleGestureDetector = new ScaleGestureDetector(context, new ScaleListener());
        gestureDetector = new GestureDetector(context, gestureListener);
        movingLimitor = new MovingLimitor(layoutLimitsRectangle);
    }

    private void updateScreenRectangle(){
        ((Activity) getContext()).getWindowManager()
                .getDefaultDisplay()
                .getMetrics(displayMetrics);
        screenRectangle = new Rect(-(int)translateX, -(int)translateY, (int) (-translateX+displayMetrics.widthPixels/scaleFactor), (int) (-translateY+displayMetrics.heightPixels/scaleFactor));
    }

    private void translate(float distanceX, float distanceY) {

        float backupTranslateX = translateX;
        float backupTranslateY = translateY;
        translateX = translateX - distanceX / scaleFactor;
        translateY = translateY - distanceY / scaleFactor;


        updateScreenRectangle();

        boolean isCollidingLeft = movingLimitor.isColidingLeft(screenRectangle);
        boolean isCollidingRight = movingLimitor.isColidingRight(screenRectangle);
        boolean isCollidingUp = movingLimitor.isColidingUp(screenRectangle);
        boolean isCollidingBottom = movingLimitor.isColidingBottom(screenRectangle);

        if (isCollidingLeft || isCollidingRight)
            translateX = backupTranslateX;
        if (isCollidingBottom || isCollidingUp)
            translateY = backupTranslateY;

        updateScreenRectangle();
    }


    private void forceTranslate(float distanceX, float distanceY) {
        translateX = translateX - distanceX / scaleFactor;
        translateY = translateY - distanceY / scaleFactor;
        updateScreenRectangle();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        // Get the pointer ID
        activePointerId = event.getPointerId(0);

        // ... Many touch events later...

        scaleGestureDetector.onTouchEvent(event);

        // Use the pointer ID to find the index of the active pointer
        // and fetch its position
        int pointerIndex = event.findPointerIndex(activePointerId);


        int action = MotionEventCompat.getActionMasked(event);
        // Get the index of the pointer associated with the action.
        int index = MotionEventCompat.getActionIndex(event);
        int onScreenX = -1;
        int onScreenY = -1;
        /*
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                Log.d("MultyTouch", "onTouchEvent"+event.getPointerCount()+": UP");
                break;
            case MotionEvent.ACTION_POINTER_UP:
                Log.d("MultyTouch", "onTouchEvent"+event.getPointerCount()+": POINTER UP");
                break;
            case MotionEvent.ACTION_DOWN:
                Log.d("MultyTouch", "onTouchEvent"+event.getPointerCount()+": DOWN");
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                Log.d("MultyTouch", "onTouchEvent"+event.getPointerCount()+": POINTER DOWN");
                break;
            case MotionEvent.ACTION_OUTSIDE:
                Log.d("MultyTouch", "onTouchEvent"+event.getPointerCount()+": OUTSIDE");
                break;
            case MotionEvent.ACTION_CANCEL:
                Log.d("MultyTouch", "onTouchEvent"+event.getPointerCount()+": CANCEL");
                break;
        }

         */

        if (event.getPointerCount() > 1) {
            gestureDetector.onTouchEvent(event);
            // TODO: end the previouly drown line or better event handling in single touch event
            // essayer de jouer avec le nomero de pointeur


            Log.d(TAG, "onTouchEvent: multi");
            return true;

        } else {
            Log.d(TAG, "onTouchEvent: single");
            return super.onTouchEvent(event);
        }


        // Let the ScaleGestureDetector inspect all events.
        //do not return true directly but look for other events
        //return true;
        //return false;
    }

    @Override
    public void onDraw(Canvas canvas) {

        canvas.save();
        canvas.scale(scaleFactor, scaleFactor);
        canvas.translate(translateX, translateY);

        super.onDraw(canvas);
        drawLimit(canvas);

        // after draw
        canvas.restore();
    }

    private void drawLimit(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(30);
        paint.setAlpha(100);
        canvas.drawRect(layoutLimitsRectangle, paint);
    }

    private class ScaleListener
            extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        /**
         * This is the active focal point in terms of the viewport. Could be a local
         * variable but kept here to minimize per-frame allocations.
         */
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            float backupScaleFactor = scaleFactor;
            scale(detector.getScaleFactor());

            // Don't let the object get too small or too large.
            setScale(Math.max(MIN_SCALE_FACTOR, Math.min(scaleFactor, MAX_SCALE_FACTOR)));

            float focusX = scaleGestureDetector.getFocusX();
            float focusY = scaleGestureDetector.getFocusY();
            if (MIN_SCALE_FACTOR < scaleFactor && scaleFactor < MAX_SCALE_FACTOR) {
                translateFromPoint(detector, focusX, focusY);
            }

            resolveCanvaOutOfLimit();

            invalidate();
            return true;
        }

        private void translateFromPoint(ScaleGestureDetector detector, float x, float y) {
            translate(-x * (1-detector.getScaleFactor()),
                    -y * (1-detector.getScaleFactor()));
        }
    }

    private void setScale(float scaleFactor) {
        this.scaleFactor = scaleFactor;
        updateScreenRectangle();
    }

    private void scale(float scaleFactor) {
        this.scaleFactor = this.scaleFactor * scaleFactor;
        updateScreenRectangle();
    }

    private void resolveCanvaOutOfLimit() {

        boolean isTooLong = screenRectangle.height() > layoutLimitsRectangle.height();
        boolean isTooWide = screenRectangle.width() > screenRectangle.width();

        if (isTooWide) {
            scale(((float) screenRectangle.width() / (float) layoutLimitsRectangle.width()));
        }
        if (isTooLong) {
            scale(((float) screenRectangle.height() / (float) layoutLimitsRectangle.height()));
        }


        boolean isCollidingLeft = movingLimitor.isColidingLeft(screenRectangle);
        boolean isCollidingRight = movingLimitor.isColidingRight(screenRectangle);
        boolean isCollidingUp = movingLimitor.isColidingUp(screenRectangle);
        boolean isCollidingBottom = movingLimitor.isColidingBottom(screenRectangle);


        if (isCollidingLeft) {
            forceTranslate(-(screenRectangle.left - layoutLimitsRectangle.left),0);
        }
        if (isCollidingRight) {
            forceTranslate(-(screenRectangle.right - layoutLimitsRectangle.right),0);
        }
        if (isCollidingUp) {
            forceTranslate(0, -(screenRectangle.top - layoutLimitsRectangle.top));
        }
        if (isCollidingBottom) {
            forceTranslate(0, -(screenRectangle.bottom - layoutLimitsRectangle.bottom));
        }

    }
}
