package com.example.emptyproject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MotionEventCompat;
import androidx.core.view.ScaleGestureDetectorCompat;
import androidx.core.view.ViewCompat;

import java.util.ArrayList;

import static android.view.MotionEvent.INVALID_POINTER_ID;

public class DrawingCanvaLayout extends FrameLayout {

    private Path path = new Path();
    private final ArrayList<ColoredPath> pathList = new ArrayList();
    private Paint brush = new Paint();

    private ScaleGestureDetector scaleGestureDetector;


    private float scaleFactor = 1.f;
    private int mActivePointerId;
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

    float lastTouchX = 0;
    float lastTouchY = 0;
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        // Get the pointer ID
        mActivePointerId = event.getPointerId(0);

        // ... Many touch events later...

        scaleGestureDetector.onTouchEvent(event);

        // Use the pointer ID to find the index of the active pointer
        // and fetch its position
        int pointerIndex = event.findPointerIndex(mActivePointerId);


        int action = MotionEventCompat.getActionMasked(event);
        // Get the index of the pointer associated with the action.
        int index = MotionEventCompat.getActionIndex(event);
        int contactX = -1;
        int contactY = -1;


        Log.d("Debug motion","The action is " + actionToString(action));

        if (event.getPointerCount() > 1) {
            Log.d("Debug motion","Multitouch event");
            // The coordinates of the current screen contact, relative to
            // the responding View or Activity.
            contactX = (int)MotionEventCompat.getX(event, index);
            contactY = (int)MotionEventCompat.getY(event, index);
            switch (action) {
                case MotionEvent.ACTION_DOWN: {
                    pointerIndex = MotionEventCompat.getActionIndex(event);
                    final float touch_x = MotionEventCompat.getX(event, pointerIndex);
                    final float touch_y = MotionEventCompat.getY(event, pointerIndex);

                    // Remember where we started (for dragging)
                    lastTouchX = touch_x;
                    lastTouchY = touch_y;
                    // Save the ID of this pointer (for dragging)
                    mActivePointerId = MotionEventCompat.getPointerId(event, 0);
                    break;
                }

                case MotionEvent.ACTION_MOVE: {
                    // Find the index of the active pointer and fetch its position
                    pointerIndex =
                            MotionEventCompat.findPointerIndex(event, mActivePointerId);

                    final float x = MotionEventCompat.getX(event, pointerIndex);
                    final float y = MotionEventCompat.getY(event, pointerIndex);

                    // Calculate the distance moved
                    float deltaX = x - lastTouchX;
                    float deltaY = y - lastTouchY;

                    float newDeltaX = deltaX;
                    float newDeltaY = deltaY;

                    translateX += deltaX;
                    translateY += deltaY;

                    invalidate();

                    // Remember this touch position for the next move event
                    lastTouchX = x;
                    lastTouchY = y;


                    //Log.d("Debug motion scroll", String.valueOf(x));
                    Log.d("Debug motion scroll", String.valueOf(deltaX));

                    break;
                }

                case MotionEvent.ACTION_UP: {
                    mActivePointerId = INVALID_POINTER_ID;
                    break;
                }

                case MotionEvent.ACTION_CANCEL: {
                    mActivePointerId = INVALID_POINTER_ID;
                    break;
                }

                case MotionEvent.ACTION_POINTER_UP: {

                    pointerIndex = MotionEventCompat.getActionIndex(event);
                    final int pointerId = MotionEventCompat.getPointerId(event, pointerIndex);

                    if (pointerId == mActivePointerId) {
                        // This was our active pointer going up. Choose a new
                        // active pointer and adjust accordingly.
                        final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
                        lastTouchX = MotionEventCompat.getX(event, newPointerIndex);
                        lastTouchY = MotionEventCompat.getY(event, newPointerIndex);
                        mActivePointerId = MotionEventCompat.getPointerId(event, newPointerIndex);
                    }
                    break;
                }
            }

            // TODO: end the previouly drown line or better event handling in single touch event
            path.moveTo(contactX/ scaleFactor, contactY/ scaleFactor); //moyen...
            // essayer de jouer avec le nomero de pointeur

            return true;

        } else {
            // Single touch event
            Log.d("Debug motion","Single touch event");
            contactX = (int)MotionEventCompat.getX(event, index);
            contactY = (int)MotionEventCompat.getY(event, index);

            // detect drawing
            // float pointX = event.getX()/ scaleFactor;
            // float pointY = event.getY()/ scaleFactor;

            float pointX = MotionEventCompat.getX(event, index)/ scaleFactor;
            float pointY = MotionEventCompat.getY(event, index)/ scaleFactor;

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    path.moveTo(pointX, pointY);
                    return true;
                case MotionEvent.ACTION_MOVE:
                    path.lineTo(pointX, pointY);
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
        private PointF viewportFocus = new PointF();
        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {

            return true;
        }

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            scaleFactor *= detector.getScaleFactor();

            // Don't let the object get too small or too large.
            scaleFactor = Math.max(0.1f, Math.min(scaleFactor, 10.0f));

            invalidate();
            return true;

        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
            Log.d("Scale End", String.valueOf(scaleFactor));
        }
    }
}