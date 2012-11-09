package com.motorola.powerfoos;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.TextView;

@TargetApi(14)
public class GameActivity extends Activity {
    int team1score = 0;
    int team2score = 0;
    boolean gameIsPaused = false;
    long goalTime = System.currentTimeMillis();
    private WakeLock mWakeLock;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        View container = findViewById(R.id.container);
        container.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                long currentTime = System.currentTimeMillis();
                TextView text = (TextView)findViewById(R.id.text);
                if (event.getButtonState() == MotionEvent.BUTTON_PRIMARY) {
                    if((currentTime - goalTime) >= 5000){
                        team1score++;
                        goalTime = currentTime;
                    }
                } else if (event.getButtonState() == MotionEvent.BUTTON_SECONDARY) {
                    if((currentTime - goalTime) >= 5000){
                        team2score++;
                        goalTime = currentTime;
                    }
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
                final Animation animation = text.getAnimation();
                if (animation != null) {
                    animation.cancel();
                }

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

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE, "My Tag");
        mWakeLock.acquire();
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        if(mWakeLock!=null)
            mWakeLock.release();
        super.onPause();
    }

}
