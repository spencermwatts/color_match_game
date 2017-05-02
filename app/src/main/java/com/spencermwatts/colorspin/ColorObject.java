package com.spencermwatts.colorspin;

/**
 * Created by smw on 5/1/17.
 */

public class ColorObject {

    // Initialize color to white

    int mRed;
    int mGreen;
    int mBlue;
    int step = 5;

    public ColorObject() {
        mRed = 255;
        mGreen = 255;
        mBlue = 255;
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
}
