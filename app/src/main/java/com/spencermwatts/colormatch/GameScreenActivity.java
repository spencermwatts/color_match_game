package com.spencermwatts.colormatch;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.annotation.SuppressLint;
import android.graphics.Color;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.BounceInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;

import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.Stack;

import me.toptas.fancyshowcase.FancyShowCaseQueue;
import me.toptas.fancyshowcase.FancyShowCaseView;
import me.toptas.fancyshowcase.FocusShape;

import static android.view.View.GONE;
import static java.lang.Math.abs;

public class GameScreenActivity extends AppCompatActivity {
    /**
     * Declare Firebase analytics
     */
    private FirebaseAnalytics mFirebaseAnalytics;
    /**
     * Duration of game in milliseconds
     */
    private int game_time = 22000;
    /**
     * has_won == 1 when the game has been one, else null
     */
    private int has_won;
    /**
     * Create a stack to hold the history of color changes
     */
    Stack<Integer> colorHistory = new Stack<Integer>();
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

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);


        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_game_screen);
        mVisible = true;
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


        /**
         * Set up the target color with new GameInstance object & set background color accordingly
         */
        final GameInstance game = new GameInstance();
        mContentView.setBackgroundColor(ContextCompat.getColor(this, game.getTargetColor()));

        /**
         * Set up play shape color & add that color to the color history stack
         */
        final ColorObject playShapeColor = new ColorObject();
        final ImageView play_shape = (ImageView) findViewById(R.id.playshape);

        // Make a variable that holds the white value for the initial shape color
        int initial_color = Color.rgb(
                playShapeColor.getRed(),
                playShapeColor.getGreen(),
                playShapeColor.getBlue()
        );
        // Set the shape to that color
        play_shape.setBackgroundColor(initial_color);
        // Add that color to the history of the shape
        colorHistory.push(initial_color);

        /**
         * Set On Click Listeners for buttons
         */

        final Button red_button = (Button)findViewById(R.id.red_button);
        final Button yellow_button = (Button)findViewById(R.id.yellow_button);
        final Button blue_button = (Button)findViewById(R.id.blue_button);
        final FloatingActionButton undo_button = (FloatingActionButton)findViewById(R.id.undo_button);

        red_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFirebaseAnalytics.logEvent("press_red", null);
                if(has_won == 1) {
                    red_button.setOnClickListener(null);
                }  else {
                    /// Subtracts GREEN
                    int new_color = Color.rgb(
                            playShapeColor.getRed(),
                            playShapeColor.stepGreen(playShapeColor.getGreen()),
                            playShapeColor.getBlue()
                    );
                    play_shape.setBackgroundColor(new_color);
                    colorHistory.push(new_color);
                    checkIfSolved(new_color, game);
                }

            }
        });

        blue_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFirebaseAnalytics.logEvent("press_blue", null);

                if(has_won == 1) {
                    blue_button.setOnClickListener(null);
                }  else {
                    /// Subtracts RED
                    int new_color = Color.rgb(
                            playShapeColor.stepRed(playShapeColor.getRed()),
                            playShapeColor.getGreen(),
                            playShapeColor.getBlue()
                    );
                    play_shape.setBackgroundColor(new_color);
                    colorHistory.push(new_color);
                    checkIfSolved(new_color, game);
                }

            }
        });

        yellow_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFirebaseAnalytics.logEvent("press_yellow", null);
                if(has_won == 1) {
                    yellow_button.setOnClickListener(null);
                }  else {
                    /// Subtracts BLUE
                    int new_color = Color.rgb(
                            playShapeColor.getRed(),
                            playShapeColor.getGreen(),
                            playShapeColor.stepBlue(playShapeColor.getBlue())
                    );

                    play_shape.setBackgroundColor(new_color);
                    colorHistory.push(new_color);
                    checkIfSolved(new_color, game);
                }

            }
        });

        undo_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // go back X amount in hisotry and set the color of the play shape to that
                // Get size of history
                mFirebaseAnalytics.logEvent("press_undo", null);

                if(colorHistory.size() > 2){
                    mFirebaseAnalytics.logEvent("undo_successful", null);

                    final Animation animSpin = AnimationUtils.loadAnimation(GameScreenActivity.this, R.anim.spin_undo_glyph);
                    undo_button.startAnimation(animSpin);
                    Log.e("the history size is ", String.valueOf(colorHistory.size()));
                    colorHistory.pop();
                    int newUndoColor = colorHistory.pop();
                    playShapeColor.undoColor(newUndoColor);
                    play_shape.setBackgroundColor(newUndoColor);
//                    colorHistory.push(newUndoColor);

                } else {
                    mFirebaseAnalytics.logEvent("undo_unsuccessful", null);

                    final Animation animShake = AnimationUtils.loadAnimation(GameScreenActivity.this, R.anim.wobble_undo_glyph);
                    undo_button.startAnimation(animShake);

                }
            }
        });

        /**
         * Set up play shape animation
         */

        ScaleAnimation scale_animation = new ScaleAnimation(11, 0, 11, 0, Animation.RELATIVE_TO_SELF, .5F, Animation.RELATIVE_TO_SELF, .5F);
        scale_animation.setDuration(game_time);
        scale_animation.setRepeatCount(0);
        scale_animation.setStartOffset(0);
        scale_animation.setInterpolator(new AccelerateInterpolator());
        scale_animation.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                // TODO Auto-generated method stub
                animateButtonsIntoScreen();

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // TODO Auto-generated method stub
               play_shape.setVisibility(GONE);
                if(has_won != 1) {
                    finishGame(false, game);
                }

            }
        });

        play_shape.startAnimation(scale_animation);
        AnimatorSet rotation_animation = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.logo_spin);
        rotation_animation.setTarget(play_shape);
        rotation_animation.start();

        /* Start onboarding implementation
            Match the rotating white
            Press the buttons to add color to the shape
            Use undo to correct mistakes
            Put into separate thread because the activity uses a constraint layout
         */

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                final View shim = (View) findViewById(R.id.shim);

                FancyShowCaseView shape_goal =
                        new FancyShowCaseView.Builder(GameScreenActivity.this)
                                .focusOn(play_shape)
                                .focusCircleRadiusFactor(6)
                                .title("Match the white square to the background color")
                                .titleStyle(R.style.onboarding_black_text, Gravity.CENTER)
                                .showOnce("1")
                                .build();
                FancyShowCaseView buttons =
                        new FancyShowCaseView.Builder(GameScreenActivity.this)
                                .focusOn(shim)
                                .fitSystemWindows(true)
                                .title("Press the buttons to add color")
                                .titleStyle(R.style.onboarding_white_text, Gravity.CENTER)
                                .showOnce("2")
                                .focusShape(FocusShape.ROUNDED_RECTANGLE)
                                .build();
                FancyShowCaseView undo =
                        new FancyShowCaseView.Builder(GameScreenActivity.this)
                                .focusOn(undo_button)
                                .fitSystemWindows(true)
                                .focusCircleRadiusFactor(1.5)
                                .title("Remove color with the undo button")
                                .titleStyle(R.style.onboarding_white_text, Gravity.CENTER)
                                .showOnce("3")
                                .build();


                FancyShowCaseQueue onboarding = new FancyShowCaseQueue()
                        .add(shape_goal)
                        .add(buttons)
                        .add(undo);

                undo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.e("SPENCER", " LAST VIEW CLICKED");
                    }
                });

                onboarding.show();

            }
        });


    }



    public void checkIfSolved(int new_color, GameInstance game) {
        final ImageView play_shape = (ImageView) findViewById(R.id.playshape);

        int game_target_color = ContextCompat.getColor(this, game.getTargetColor());

        int r1 = Color.red(game_target_color);
        int g1 = Color.green(game_target_color);
        int b1 = Color.blue(game_target_color);

        int r2 = Color.red(new_color);
        int g2 = Color.green(new_color);
        int b2 = Color.blue(new_color);

        int threshold = 30;
        int red_diff = abs(r1-r2);
        int green_diff = abs(g1-g2);
        int blue_diff = abs(b1-b2);

        Log.e("red diff  ", String.valueOf(red_diff));
        Log.e("green diff  ", String.valueOf(green_diff));
        Log.e("blue diff  ", String.valueOf(blue_diff));

        if (red_diff <= threshold && blue_diff <= threshold && green_diff <= threshold){
            play_shape.setBackgroundColor(ContextCompat.getColor(this, game.getTargetColor()));
            finishGame(true, game);

        }
    }

    public void finishGame(boolean won, GameInstance game) {
        final Button restart_button = (Button) findViewById(R.id.restart_button);
        Bundle bundle = new Bundle();
        bundle.putString("background_color", String.valueOf(ContextCompat.getColor(this, game.getTargetColor())));

        if (won) {
            has_won = 1;
            mFirebaseAnalytics.logEvent("user_won", bundle);
            restart_button.setText(R.string.good_job);
        } else {
            mFirebaseAnalytics.logEvent("user_lost", bundle);
            restart_button.setText(R.string.try_again);
        }
        animateButtonsOffScreen();
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                restart_button.setVisibility(View.VISIBLE);
                restart_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mFirebaseAnalytics.logEvent("try_again_clicked", null);
                        restart_activity();
                    }
                });

            }
        }, 500);




    }
    public void animateButtonsIntoScreen() {
        View yellow_button = findViewById(R.id.yellow_button);
        View red_button = findViewById(R.id.red_button);
        View blue_button = findViewById(R.id.blue_button);
        View undo_button = findViewById(R.id.undo_button);

        TranslateAnimation enterScreenBlue = new TranslateAnimation(
                Animation.ABSOLUTE, 0F,
                Animation.ABSOLUTE, 0F,
                Animation.RELATIVE_TO_SELF, 2.5F,
                Animation.RELATIVE_TO_SELF, 0F

        );

        TranslateAnimation enterScreenYellow = new TranslateAnimation(
                Animation.ABSOLUTE, 0F,
                Animation.ABSOLUTE, 0F,
                Animation.RELATIVE_TO_SELF, 2.5F,
                Animation.RELATIVE_TO_SELF, 0F

        );

        TranslateAnimation enterScreenRed = new TranslateAnimation(
                Animation.ABSOLUTE, 0F,
                Animation.ABSOLUTE, 0F,
                Animation.RELATIVE_TO_SELF, 2.5F,
                Animation.RELATIVE_TO_SELF, 0F

        );


        enterScreenBlue.setStartOffset(650);
        enterScreenYellow.setStartOffset(700);
        enterScreenRed.setStartOffset(750);
        enterScreenBlue.setDuration(300);
        enterScreenYellow.setDuration(250);
        enterScreenRed.setDuration(300);
        enterScreenBlue.setInterpolator(new LinearOutSlowInInterpolator());
        enterScreenYellow.setInterpolator(new LinearOutSlowInInterpolator());
        enterScreenRed.setInterpolator(new LinearOutSlowInInterpolator());

        yellow_button.startAnimation(enterScreenYellow);
        red_button.startAnimation(enterScreenRed);
        blue_button.startAnimation(enterScreenBlue);

        ScaleAnimation undo_button_scale = new ScaleAnimation(0, 1, 0, 1, Animation.RELATIVE_TO_SELF, .5F, Animation.RELATIVE_TO_SELF, .5F);
        undo_button_scale.setDuration(600);
        undo_button_scale.setRepeatCount(0);
        undo_button_scale.setStartOffset(1000);
        undo_button_scale.setInterpolator(new BounceInterpolator());
        undo_button.startAnimation(undo_button_scale);

    }

    public void animateButtonsOffScreen() {
        View yellow_button = findViewById(R.id.yellow_button);
        View red_button = findViewById(R.id.red_button);
        View blue_button = findViewById(R.id.blue_button);
        View undo_button = findViewById(R.id.undo_button);
        View shim = findViewById(R.id.shim);


        TranslateAnimation exitScreenBlue = new TranslateAnimation(
                Animation.ABSOLUTE, 0F,
                Animation.ABSOLUTE, 0F,
                Animation.RELATIVE_TO_SELF, 0F,
                Animation.RELATIVE_TO_SELF, 2.5F

        );

        TranslateAnimation exitScreenYellow = new TranslateAnimation(
                Animation.ABSOLUTE, 0F,
                Animation.ABSOLUTE, 0F,
                Animation.RELATIVE_TO_SELF, 0F,
                Animation.RELATIVE_TO_SELF, 2.5F

        );

        TranslateAnimation exitScreenRed = new TranslateAnimation(
                Animation.ABSOLUTE, 0F,
                Animation.ABSOLUTE, 0F,
                Animation.RELATIVE_TO_SELF, 0F,
                Animation.RELATIVE_TO_SELF, 2.5F

        );


        exitScreenBlue.setStartOffset(650);
        exitScreenYellow.setStartOffset(700);
        exitScreenRed.setStartOffset(750);
        exitScreenBlue.setDuration(300);
        exitScreenYellow.setDuration(250);
        exitScreenRed.setDuration(300);
        exitScreenBlue.setInterpolator(new LinearOutSlowInInterpolator());
        exitScreenYellow.setInterpolator(new LinearOutSlowInInterpolator());
        exitScreenRed.setInterpolator(new LinearOutSlowInInterpolator());


        ScaleAnimation undo_button_scale = new ScaleAnimation(1, 0, 1, 0, Animation.RELATIVE_TO_SELF, .5F, Animation.RELATIVE_TO_SELF, .5F);
        undo_button_scale.setDuration(300);
        undo_button_scale.setRepeatCount(0);
        undo_button_scale.setInterpolator(new BounceInterpolator());

        yellow_button.setVisibility(GONE);
        red_button.setVisibility(GONE);
        blue_button.setVisibility(GONE);
        undo_button.setVisibility(GONE);
        shim.setVisibility(View.INVISIBLE);

    }

    public void restart_activity()
    {
        this.recreate();
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

