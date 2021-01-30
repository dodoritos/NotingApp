package com.example.emptyproject;

import android.graphics.Rect;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MovingLimitorTestJUnit5 {

    private static Rect biggerRect = new Rect(0, 0, 100, 100);
    private static MovingLimitor movingLimitor = new MovingLimitor(biggerRect);
    private static Rect smallRect;
    private static Rect largeRect;
    private static Rect longRect;

    @BeforeAll
    static void setUp() {
        System.out.println("setup");

        biggerRect = new Rect(0, 0, 100, 100);
        movingLimitor = new MovingLimitor(biggerRect);
        smallRect = new Rect(10, 10, 20, 20);
        largeRect = new Rect(200, 10, 200, 10);
        longRect = new Rect(10, 200, 10, 200);

        System.out.println(smallRect.bottom);
        System.out.println(smallRect.left);
        System.out.println(smallRect.centerX());
        System.out.println(smallRect);
    }

    @AfterAll
    static void teardown() {
        System.out.println("teardown");
        biggerRect = null;
        movingLimitor = null;
        smallRect = null;
        largeRect = null;
        longRect = null;
    }

    @Test
    void isColidingLeft() {

        Rect biggerRect = new Rect(0, 0, 100, 100);
        MovingLimitor movingLimitor = new MovingLimitor(biggerRect);
        Rect smallRect = new Rect(10, 10, 20, 20);
        Rect largeRect = new Rect(200, 10, 200, 10);
        Rect longRect = new Rect(10, 200, 10, 200);
        assertTrue(movingLimitor.isColidingLeft(biggerRect));
        assertFalse(movingLimitor.isColidingLeft(smallRect));
        assertTrue(movingLimitor.isColidingLeft(largeRect));
        assertFalse(movingLimitor.isColidingLeft(longRect));
    }


    @Test
    public void testIsColidingRight() {
        assertTrue(movingLimitor.isColidingRight(biggerRect));
        assertFalse(movingLimitor.isColidingRight(smallRect));
        assertTrue(movingLimitor.isColidingRight(largeRect));
        assertFalse(movingLimitor.isColidingRight(longRect));
    }

    @Test
    public void testIsColidingUp() {
        assertTrue(movingLimitor.isColidingUp(biggerRect));
        assertFalse(movingLimitor.isColidingUp(smallRect));
        assertFalse(movingLimitor.isColidingUp(largeRect));
        assertTrue(movingLimitor.isColidingUp(longRect));
    }

    @Test
    public void testIsColidingBottom() {
        assertTrue(movingLimitor.isColidingBottom(biggerRect));
        assertFalse(movingLimitor.isColidingBottom(smallRect));
        assertFalse(movingLimitor.isColidingBottom(largeRect));
        assertTrue(movingLimitor.isColidingBottom(longRect));
    }
}