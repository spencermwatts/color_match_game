package com.spencermwatts.colorspin;

import android.app.ActivityOptions;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatImageButton;
import android.util.Pair;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class StartPageActivity extends AppCompatActivity {
    private int currentApiVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Make the app full screen
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

        /////////

        setContentView(R.layout.activity_start_page);



        /**
         * Set up the animation for the color buttons
         */
        //// TODO: 4/30/17 Create jiggle animation for three buttons


        final RelativeLayout button_container = (RelativeLayout)findViewById(R.id.button_container);

        AnimatorSet rotation_animation = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.logo_spin);
        rotation_animation.setTarget(button_container);
        rotation_animation.start();


        final Button start_button = (Button)findViewById(R.id.start_button);

        final Pair<View, String> p1 = Pair.create((View) findViewById(R.id.yellow_button), "yellow_button_transition");
        final Pair<View, String> p2 = Pair.create((View) findViewById(R.id.blue_button), "blue_button_transition");
        final Pair<View, String> p3 = Pair.create((View) findViewById(R.id.red_button), "red_button_transition");
//


        /**
         * End animation for the color buttons
         */


        start_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent start_game = new Intent(StartPageActivity.this, GameScreenActivity.class);

                // Comment back in the next two lines in order for the three logo orbs to transition to the next activity
//                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(StartPageActivity.this, p1, p2, p3);
//                StartPageActivity.this.startActivity(start_game, options.toBundle());
                StartPageActivity.this.startActivity(start_game);

                            }
        });
    }

}

// TODO: 5/1/17 https://github.com/dynamitechetan/Flowing-Gradient