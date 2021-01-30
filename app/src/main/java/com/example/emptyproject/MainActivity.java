package com.example.emptyproject;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.widget.RadioGroup;

public class MainActivity extends AppCompatActivity {

    private RadioGroup radioGroup;
    private ScrollableDrawingCanva scrollableDrawingCanva;

    Paint brush1 = new Paint();
    Paint gommeBrush = new Paint();
    Paint brush3 = new Paint();
    Paint styleBrush = new Paint();


    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //DrawingCanva drawingCanva = new DrawingCanva(this);
        //setContentView(drawingCanva);
        radioGroup = findViewById(R.id.radioGroup);
        scrollableDrawingCanva = findViewById(R.id.scrollableDrawingCanva);
        //paper.setDrawingCanva(drawingCanva);
        brush1.setAntiAlias(true);
        brush1.setColor(Color.BLACK);
        brush1.setStyle(Paint.Style.STROKE);
        brush1.setStrokeWidth(8f);


        gommeBrush.setAntiAlias(true);
        gommeBrush.setColor(Color.WHITE);
        gommeBrush.setStyle(Paint.Style.STROKE);
        gommeBrush.setStrokeWidth(50f);

        brush3.setAntiAlias(true);
        brush3.setColor(Color.RED);
        brush3.setStyle(Paint.Style.STROKE);
        brush3.setStrokeWidth(8f);

        styleBrush.setAntiAlias(true);
        styleBrush.setColor(Color.MAGENTA);
        styleBrush.setStyle(Paint.Style.STROKE);
        styleBrush.setStrokeWidth(20f);
        styleBrush.setAlpha(50);
        //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        //    styleBrush.setBlendMode(BlendMode.COLOR_DODGE);
        //}

        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId){
                case R.id.penButton:
                    scrollableDrawingCanva.setBrush(brush1);
                    break;
                case R.id.gomButton:
                    scrollableDrawingCanva.setBrush(gommeBrush);
                    break;
                case R.id.redButton:
                    scrollableDrawingCanva.setBrush(brush3);
                    break;
                case R.id.styleButton:
                    scrollableDrawingCanva.setBrush(styleBrush);
                    break;
                default:
                    scrollableDrawingCanva.setBrush(brush3);
            }

        });

    }
}