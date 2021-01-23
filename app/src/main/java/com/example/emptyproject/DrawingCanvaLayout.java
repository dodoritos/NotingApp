package com.example.emptyproject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MotionEventCompat;

import java.util.ArrayList;

import static android.view.MotionEvent.INVALID_POINTER_ID;

/**
 * A drawable layout with zoom and scroll features
 */
public class DrawingCanvaLayout extends FrameLayout {

    private final String TAG = "DrawingCanvaLayout";

    private final float MAX_SCALE_FACTOR = 10f;
    private final float MIN_SCALE_FACTOR = 0.1f;

    private Rect layoutLimitsRectangle = new Rect(-100, -100, 3000, 3000);
    DisplayMetrics displayMetrics = new DisplayMetrics();


    private Path path = new Path();
    private final ArrayList<ColoredPath> pathList = new ArrayList<>();
    private Paint brush = new Paint();

    private ScaleGestureDetector scaleGestureDetector;


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
    private void translate(float distanceX, float distanceY) {
        float newTranslateX = translateX - distanceX / scaleFactor;
        float newTranslateY = translateY - distanceY / scaleFactor;

        ((Activity) getContext()).getWindowManager()
                .getDefaultDisplay()
                .getMetrics(displayMetrics);

        int screenHeight = displayMetrics.heightPixels;
        int screenWidth = displayMetrics.widthPixels;

        Log.d(TAG, "translate: " + -newTranslateX +" + "+ screenWidth +" < "+ layoutLimitsRectangle.right);
        if (-newTranslateX + screenWidth/scaleFactor < layoutLimitsRectangle.right
                && -newTranslateX > layoutLimitsRectangle.left)
            translateX = newTranslateX;
        if (-newTranslateY + screenHeight/scaleFactor < layoutLimitsRectangle.bottom
                && -newTranslateY > layoutLimitsRectangle.top)
            translateY = newTranslateY;

    }

    private GestureDetector gestureDetector;


    private float scaleFactor = 1.f;
    private int activePointerId;
    private float translateX = 0;
    private float translateY = 0;


    public DrawingCanvaLayout(@NonNull Context context) {
        super(context);
        init(context);
        setupBrush();
    }

    public DrawingCanvaLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
        setupBrush();
    }

    public DrawingCanvaLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
        setupBrush();
    }

    public DrawingCanvaLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
        setupBrush();
    }

    private void init(Context context) {
        scaleGestureDetector = new ScaleGestureDetector(context, new ScaleListener());
        gestureDetector = new GestureDetector(context, gestureListener);
    }

    private void setupBrush() {
        brush.setAntiAlias(true);
        brush.setColor(Color.BLACK);
        brush.setStyle(Paint.Style.STROKE);
        brush.setStrokeWidth(8f);

    }

    public void setBrush(Paint brush) {
        this.brush = brush;
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
            path.moveTo(onScreenX/ scaleFactor, onScreenY/ scaleFactor); //moyen...
            // essayer de jouer avec le nomero de pointeur


            return true;

        } else {
            // Single touch event
            onScreenX = (int)MotionEventCompat.getX(event, index);
            onScreenY = (int)MotionEventCompat.getY(event, index);

            // detect drawing
            // float pointX = event.getX()/ scaleFactor;
            // float pointY = event.getY()/ scaleFactor;

            float onLayoutX = onScreenX/ scaleFactor - translateX;
            float onLayoutY = onScreenY/ scaleFactor - translateY;

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    path.moveTo(onLayoutX, onLayoutY);
                    return true;
                case MotionEvent.ACTION_MOVE:
                    path.lineTo(onLayoutX, onLayoutY);
                    break;
                case MotionEvent.ACTION_UP:
                    pathList.add(new ColoredPath(path, brush));
                    path = new Path();
                    brush = new Paint(brush);
                    break;
                default:
                    return false;
            }
            postInvalidate();
        }


        // Let the ScaleGestureDetector inspect all events.
        //do not return true directly but look for other events
        //return true;
        return false;
    }


    // Given an action int, returns a string description
    private static String actionToString(int action) {
        switch (action) {

            case MotionEvent.ACTION_DOWN: return "Down";
            case MotionEvent.ACTION_MOVE: return "Move";
            case MotionEvent.ACTION_POINTER_DOWN: return "Pointer Down";
            case MotionEvent.ACTION_UP: return "Up";
            case MotionEvent.ACTION_POINTER_UP: return "Pointer Up";
            case MotionEvent.ACTION_OUTSIDE: return "Outside";
            case MotionEvent.ACTION_CANCEL: return "Cancel";
        }
        return "";
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //((Activity) getContext()).getWindowManager()
        //        .getDefaultDisplay()
        //        .getMetrics(displayMetrics);
        //Log.d("height", "onDraw: "+ getHeight()*scaleFactor + " x " + getWidth()*scaleFactor);
        //Log.d("height", "onDraw: "+ displayMetrics.heightPixels + " x " + displayMetrics.widthPixels);

        canvas.save();
        canvas.scale(scaleFactor, scaleFactor);
        canvas.translate(translateX, translateY);

        // draw all lines
        for (ColoredPath p : pathList) {
            canvas.drawPath(p.getPath(), p.getBrush());
        }
        // draw current line
        canvas.drawPath(path, brush);

        // after draw
        canvas.restore();

    }




    private class ScaleListener
        extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        /**
         * This is the active focal point in terms of the viewport. Could be a local
         * variable but kept here to minimize per-frame allocations.
         */
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            scaleFactor *= detector.getScaleFactor();

            // Don't let the object get too small or too large.
            scaleFactor = Math.max(MIN_SCALE_FACTOR, Math.min(scaleFactor, MAX_SCALE_FACTOR));

            float focusX = scaleGestureDetector.getFocusX();
            float focusY = scaleGestureDetector.getFocusY();

            if (MIN_SCALE_FACTOR < scaleFactor && scaleFactor < MAX_SCALE_FACTOR) {
                scaleFromPoint(detector, focusX, focusY);
            }
            invalidate();
            return true;
        }

        private void scaleFromPoint(ScaleGestureDetector detector, float x, float y) {
            translate(-x * (1-detector.getScaleFactor()),
                    -y * (1-detector.getScaleFactor()));
        }
    }
}