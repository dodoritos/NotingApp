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

    DisplayMetrics displayMetrics = new DisplayMetrics();


    private Path path = new Path();
    private final ArrayList<ColoredPath> pathList = new ArrayList<>();
    private Paint brush = new Paint();


    protected float scaleFactor = 1.f;
    private int activePointerId;
    protected float translateX = 0;
    protected float translateY = 0;


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
        // Single touch event
        float onScreenX = event.getX();
        float onScreenY = event.getY();

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
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //((Activity) getContext()).getWindowManager()
        //        .getDefaultDisplay()
        //        .getMetrics(displayMetrics);
        //Log.d("height", "onDraw: "+ getHeight()*scaleFactor + " x " + getWidth()*scaleFactor);
        //Log.d("height", "onDraw: "+ displayMetrics.heightPixels + " x " + displayMetrics.widthPixels);


        // draw all lines
        for (ColoredPath p : pathList) {
            canvas.drawPath(p.getPath(), p.getBrush());
        }
        // draw current line
        canvas.drawPath(path, brush);
    }
}