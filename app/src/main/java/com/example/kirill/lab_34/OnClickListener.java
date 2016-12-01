package com.example.kirill.lab_34;

import android.view.MotionEvent;
import android.view.View;

public abstract class OnClickListener implements View.OnTouchListener {
    private static final float MAX_OFFSET = 25;
    private float originClickX;
    private float originClickY;

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                originClickX = event.getX();
                originClickY = event.getY();
                break;
            case MotionEvent.ACTION_UP:
                if (Math.abs(originClickX - event.getX()) < MAX_OFFSET &&
                        Math.abs(originClickY - event.getY()) < MAX_OFFSET) {
                    onClick();
                }
        }
        return false;
    }

    public abstract void onClick();
}
