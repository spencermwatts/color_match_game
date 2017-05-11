package com.spencermwatts.colormatch;

import android.graphics.Color;

/**
 * Created by smw on 5/1/17.
 */

public class ColorObject {

    // Initialize color to white

    int mRed;
    int mGreen;
    int mBlue;
    int step = 10;

    public ColorObject() {
        mRed = 250;
        mGreen = 250;
        mBlue = 250;
    }

    public int getRed() {
        return mRed;
    }

    public int stepRed(int mRed) {
        if(mRed - step < step){
            return mRed;
        } else {
            this.mRed = mRed - step;
            return mRed;
        }
    }

    public int getGreen() {
        return mGreen;
    }

    public int stepGreen(int mGreen) {
        if(mGreen - step < step){
            return mGreen;
        } else {
            this.mGreen = mGreen - step;
            return mGreen;
        }
    }

    public int getBlue() {
        return mBlue;
    }

    public int stepBlue(int mBlue) {
        if(mBlue - step < step){
            return mBlue;
        } else {
            this.mBlue = mBlue - step;
            return mBlue;
        }
    }
    public void undoColor(int undoColor){
        mRed = Color.red(undoColor);
        mBlue = Color.blue(undoColor);
        mGreen = Color.green(undoColor);
    }
}
