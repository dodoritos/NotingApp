package com.example.emptyproject;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

/**
 * TODO: document your custom view class.
 */
public class Paper extends FrameLayout {
    private DrawingCanvaLayout drawingCanva;
    private String mExampleString = "PaperString"; // TODO: use a default from R.string...
    private int mExampleColor = Color.RED; // TODO: use a default from R.color...
    private float mExampleDimension = 0; // TODO: use a default from R.dimen...
    private Drawable mExampleDrawable;

    private TextPaint mTextPaint;
    private TextView text;
    private float mTextWidth;
    private float mTextHeight;

    public Paper(Context context) {
        super(context);
        init(null, 0);
    }

    public Paper(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public Paper(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        if (child instanceof DrawingCanvaLayout) {
            drawingCanva = (DrawingCanvaLayout) child;
            super.addView(drawingCanva, index, params);
        }
    }

    public void setDrawingCanva(DrawingCanvaLayout drawingCanva) {
        this.drawingCanva = drawingCanva;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // TODO: consider storing these as member variables to reduce
        // allocations per draw cycle.
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int paddingRight = getPaddingRight();
        int paddingBottom = getPaddingBottom();

        int contentWidth = getWidth() - paddingLeft - paddingRight;
        int contentHeight = getHeight() - paddingTop - paddingBottom;

        // Draw the example drawable on top of the text.
        if (mExampleDrawable != null) {
            mExampleDrawable.setBounds(paddingLeft, paddingTop,
                    paddingLeft + contentWidth, paddingTop + contentHeight);
            mExampleDrawable.draw(canvas);
        }
    }

    public void setBrush(Paint brush) {
        drawingCanva.setBrush(brush);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        if (drawingCanva != null)
            drawingCanva.onTouchEvent(event);
        return true;
    }
}