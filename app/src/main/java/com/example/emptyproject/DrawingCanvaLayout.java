package com.example.emptyproject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;

import java.util.ArrayList;

public class DrawingCanvaLayout extends FrameLayout {

    private Path path = new Path();
    private final ArrayList<ColoredPath> pathList = new ArrayList();
    private Paint brush = new Paint();

    private ScaleGestureDetector scaleGestureDetector;
    private float scaleFactor = 1.f;
    // Viewport extremes. See mCurrentViewport for a discussion of the viewport.
    private static final float AXIS_X_MIN = -1f;
    private static final float AXIS_X_MAX = 1f;
    private static final float AXIS_Y_MIN = -1f;
    private static final float AXIS_Y_MAX = 1f;


    // The current viewport. This rectangle represents the currently visible
    // chart domain and range.
    private RectF mCurrentViewport =
            new RectF(AXIS_X_MIN, AXIS_Y_MIN, AXIS_X_MAX, AXIS_Y_MAX);

    // The current destination rectangle (in pixel coordinates) into which the
    // chart data should be drawn.
    private Rect mContentRect;


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

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        // Let the ScaleGestureDetector inspect all events.
        scaleGestureDetector.onTouchEvent(event);
        //do not return true directly but look for other events
        //return true;


        // detect drawing
        float pointX = event.getX()/ scaleFactor;
        float pointY = event.getY()/ scaleFactor;

        if (!scaleGestureDetector.isInProgress()) {
            Log.d("Draw", String.valueOf(event.getAction()));
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    path.moveTo(pointX, pointY);
                    return true;
                case MotionEvent.ACTION_MOVE:
                    path.lineTo(pointX, pointY);
                    break;
                case MotionEvent.ACTION_UP:
                    Log.d("Draw", "UP");
                    pathList.add(new ColoredPath(path, brush));
                    path = new Path();
                    brush = new Paint(brush);
                    break;
                default:
                    return false;
            }
            postInvalidate();
        }
        return false;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.save();
        canvas.scale(scaleFactor, scaleFactor);

        // draw all lines
        for (ColoredPath p : pathList) {
            canvas.drawPath(p.getPath(), p.getBrush());
        }
        // draw current line
        canvas.drawPath(path, brush);

        // after draw
        canvas.restore();

    }

    /**
     * Sets the current viewport (defined by mCurrentViewport) to the given
     * X and Y positions. Note that the Y value represents the topmost pixel position,
     * and thus the bottom of the mCurrentViewport rectangle.
     */
    private void setViewportBottomLeft(float x, float y) {
        /*
         * Constrains within the scroll range. The scroll range is simply the viewport
         * extremes (AXIS_X_MAX, etc.) minus the viewport size. For example, if the
         * extremes were 0 and 10, and the viewport size was 2, the scroll range would
         * be 0 to 8.
         */

        float curWidth = mCurrentViewport.width();
        float curHeight = mCurrentViewport.height();
        x = Math.max(AXIS_X_MIN, Math.min(x, AXIS_X_MAX - curWidth));
        y = Math.max(AXIS_Y_MIN + curHeight, Math.min(y, AXIS_Y_MAX));

        mCurrentViewport.set(x, y - curHeight, x + curWidth, y);

        // Invalidates the View to update the display.
        ViewCompat.postInvalidateOnAnimation(this);
    }


    private class ScaleListener
            extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            scaleFactor *= detector.getScaleFactor();

            // Don't let the object get too small or too large.
            scaleFactor = Math.max(0.1f, Math.min(scaleFactor, 10.0f));

            invalidate();
            return true;
        }

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            Log.d("Scale Begin", String.valueOf(scaleFactor));
            return true;
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
            Log.d("Scale End", String.valueOf(scaleFactor));
        }
    }
}