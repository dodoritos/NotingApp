package com.example.emptyproject;

import android.graphics.Paint;

import android.graphics.Path;

public class ColoredPath {
    Path path;
    Paint brush;
    public ColoredPath(Path path, Paint brush) {
        this.path = path;
        this.brush = brush;
    }

    public Paint getBrush() {
        return brush;
    }

    public Path getPath() {
        return path;
    }
}
