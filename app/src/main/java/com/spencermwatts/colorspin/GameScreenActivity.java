package com.spencermwatts.colorspin;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Color;
import android.media.Image;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.Random;

import static com.spencermwatts.colorspin.R.color.core_gold;
import static com.spencermwatts.colorspin.R.color.core_green;
import static com.spencermwatts.colorspin.R.color.core_purple;
import static com.spencermwatts.colorspin.R.color.core_red;
import static com.spencermwatts.colorspin.R.color.core_teal;
import static com.spencermwatts.colorspin.R.color.dark_green;
import static com.spencermwatts.colorspin.R.color.game_red;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class GameScreenActivity extends AppCompatActivity {

    /**
     * Initiate a integer to hold the curent API version in order to tell us if we can hide navigation or not
     */
    private int currentApiVersion;
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 0;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private View mContentView;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE);
        }
    };
    private View mControlsView;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            mControlsView.setVisibility(View.VISIBLE);
        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_game_screen);

        mVisible = true;
//        mControlsView = findViewById(R.id.fullscreen_content_controls);
        mContentView = findViewById(R.id.gamescreen);



        currentApiVersion = android.os.Build.VERSION.SDK_INT;

        final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

        // This work only for android 4.4+
        if(currentApiVersion >= android.os.Build.VERSION_CODES.KITKAT)
        {

            getWindow().getDecorView().setSystemUiVisibility(flags);

            // Code below is to handle presses of Volume up or Volume down.
            // Without this, after pressing volume buttons, the navigation bar will
            // show up and won't hide
            final View decorView = getWindow().getDecorView();
            decorView
                    .setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener()
                    {

                        @Override
                        public void onSystemUiVisibilityChange(int visibility)
                        {
                            if((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0)
                            {
                                decorView.setSystemUiVisibility(flags);
                            }
                        }
                    });
        }


        /////// Set up colors with GameInstance object ////////////////////////////////////////////

        final GameInstance game = new GameInstance();

        Log.e("BACKGROUND IS " , String.valueOf(game.getTargetColor()));
        mContentView.setBackgroundColor(getColor(game.getTargetColor()));

        /////// Set On Click Listeners for buttons ////////////////////////////////////////////////

        final ImageView play_shape = (ImageView) findViewById(R.id.playshape);

        Button red_button = (Button)findViewById(R.id.red_button);
        Button yellow_button = (Button)findViewById(R.id.yellow_button);
        Button blue_button = (Button)findViewById(R.id.blue_button);

        final ColorObject playShapeColor = new ColorObject();


        play_shape.setBackgroundColor(
                    Color.rgb(
                            playShapeColor.getRed(),
                            playShapeColor.getGreen(),
                            playShapeColor.getBlue()
                    )
        );

        red_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /// Subtracts GREEN
                play_shape.setBackgroundColor(
                        Color.rgb(
                                playShapeColor.getRed(),
                                playShapeColor.stepGreen(playShapeColor.getGreen()),
                                playShapeColor.getBlue()
                        )
                );
                Log.e("COLOR OBJ RED IS NOW " , String.valueOf(playShapeColor.getRed()));
                Log.e("COLOR OBJ BLUE IS NOW " , String.valueOf(playShapeColor.getGreen()));
                Log.e("COLOR OBJ BLUE IS NOW " , String.valueOf(playShapeColor.getBlue()));

            }
        });

        blue_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /// Subtracts RED
                play_shape.setBackgroundColor(
                        Color.rgb(
                                playShapeColor.stepRed(playShapeColor.getRed()),
                                playShapeColor.getGreen(),
                                playShapeColor.getBlue()
                        )
                );
                Log.e("COLOR OBJ RED IS NOW " , String.valueOf(playShapeColor.getRed()));
                Log.e("COLOR OBJ BLUE IS NOW " , String.valueOf(playShapeColor.getGreen()));
                Log.e("COLOR OBJ BLUE IS NOW " , String.valueOf(playShapeColor.getBlue()));

            }
        });

        yellow_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /// Subtracts BLUE
//                play_shape.setBackgroundColor(getColor(core_gold));
                play_shape.setBackgroundColor(
                        Color.rgb(
                                playShapeColor.getRed(),
                                playShapeColor.getGreen(),
                                playShapeColor.stepBlue(playShapeColor.getBlue())
                        )
                );

                Log.e("COLOR OBJ RED IS NOW " , String.valueOf(playShapeColor.getRed()));
                Log.e("COLOR OBJ BLUE IS NOW " , String.valueOf(playShapeColor.getGreen()));
                Log.e("COLOR OBJ BLUE IS NOW " , String.valueOf(playShapeColor.getBlue()));


            }
        });

        ///////////////////////////////////////////////////////////////////////////////////////////







        ScaleAnimation scale_animation = new ScaleAnimation(5, 5, 5, 5, Animation.RELATIVE_TO_SELF, .5F, Animation.RELATIVE_TO_SELF, .5F);
        scale_animation.setDuration(20000);
        scale_animation.setRepeatCount(0);
        scale_animation.setStartOffset(1000);
        play_shape.startAnimation(scale_animation);



        AnimatorSet rotation_animation = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.logo_spin);
        rotation_animation.setTarget(play_shape);
        rotation_animation.start();



    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }



    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
//        mControlsView.setVisibility(View.GONE);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }
}

// TODO: 4/30/17 Create shim for behind buttons