package com.example.emptyproject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class DrawingCanvaLayout extends FrameLayout {

    private Path path = new Path();
    private final ArrayList<ColoredPath> pathList = new ArrayList();
    private Paint brush = new Paint();

    private ScaleGestureDetector mScaleDetector;
    private float mScaleFactor = 1.f;

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
        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
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
        mScaleDetector.onTouchEvent(event);
        //return true;
        float pointX = event.getX()/mScaleFactor;
        float pointY = event.getY()/mScaleFactor;

        if (!mScaleDetector.isInProgress()) {
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
        canvas.scale(mScaleFactor, mScaleFactor);

        // onDraw() code goes here
        for (ColoredPath p : pathList) {
            canvas.drawPath(p.getPath(), p.getBrush());
        }
        canvas.drawPath(path, brush);

        // after draw
        canvas.restore();

    }

    private class ScaleListener
            extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            mScaleFactor *= detector.getScaleFactor();

            // Don't let the object get too small or too large.
            mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 10.0f));

            invalidate();
            return true;
        }

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            Log.d("Scale Begin", String.valueOf(mScaleFactor));
            return true;
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
            Log.d("Scale End", String.valueOf(mScaleFactor));
        }
    }
}