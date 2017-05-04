package com.spencermwatts.colorspin;

import android.content.Context;

import java.util.Random;

/**
 * Created by smw on 5/1/17.
 */

public class GameInstance {

    private int mShape; // Which shape will we use as the play shape?
    private int mTargetColorResourceID; // What is the color of the background and the color the shape needs to become?
    private int mOverlay; // Which overlay will be shown?
    private int mOverlayColor; // What is the overlay's color?

    private static final int[] COLORS = new int[] {
            R.color.dark_green,
            R.color.dark_purple,
            R.color.dark_gold,
            R.color.core_green,
            R.color.core_purple,
            R.color.core_gold,
            R.color.bright_green,
            R.color.bright_teal,
            R.color.bright_purple,
            R.color.bright_gold,
            R.color.bright_red,
            R.color.medium_green,
            R.color.medium_teal,
            R.color.medium_purple,
            R.color.medium_gold,
            R.color.medium_red
    };


    public GameInstance() {

        Random rand = new Random();
        int randomIndex = rand.nextInt(COLORS.length-1);

        mTargetColorResourceID = COLORS[randomIndex];

        mShape = R.drawable.shape;

    }

    public GameInstance(int Shape, int TargetColor, int Overlay, int OverlayColor) {
        Random rand = new Random();
        int randomIndex = rand.nextInt(19);

        mTargetColorResourceID = COLORS[randomIndex];
        mShape = Shape;
        mOverlay = Overlay;
        mOverlayColor = OverlayColor;
    }



    public int getShape() {
        return mShape;
    }

    public int getTargetColor() {
        return mTargetColorResourceID;
    }

    public int getOverlay() {
        return mOverlay;
    }

    public int getOverlayColor() {
        return mOverlayColor;
    }

}
