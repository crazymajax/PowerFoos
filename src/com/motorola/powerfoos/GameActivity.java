package com.motorola.powerfoos;

import android.os.Bundle;
import android.annotation.TargetApi;
import android.app.Activity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.TextView;
import android.support.v4.app.NavUtils;

@TargetApi(14)
public class GameActivity extends Activity {
    int team1score = 0;
    int team2score = 0;
    boolean gameIsPaused = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        View container = findViewById(R.id.container);
        container.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                TextView text = (TextView)findViewById(R.id.text);

                if (event.getButtonState() == MotionEvent.BUTTON_PRIMARY) {
                    team1score++;
                } else if (event.getButtonState() == MotionEvent.BUTTON_SECONDARY) {
                    team2score++;
                } else if (!gameIsPaused) {
                    gameIsPaused = true;
                    text.setText("Game Paused");
                    Animation anim = new AlphaAnimation(0.0f, 1.0f);
                    anim.setDuration(500);
                    anim.setStartOffset(20);
                    anim.setRepeatMode(Animation.REVERSE);
                    anim.setRepeatCount(Animation.INFINITE);
                    text.startAnimation(anim);
                    return false;
                }
                gameIsPaused = false;
                text.getAnimation().cancel();

                text.setText(team1score + " - " + team2score);
                return false;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_game, menu);
        return true;
    }


}
