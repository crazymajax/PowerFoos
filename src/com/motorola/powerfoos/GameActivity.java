package com.motorola.powerfoos;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.client.android.IntentIntegrator;
import com.google.zxing.client.android.IntentResult;

@TargetApi(14)
public class GameActivity extends Activity {
    private static final String TAG = GameActivity.class.getSimpleName();
    private int team1score = 0;
    private int team2score = 0;
    private boolean gameIsPaused = false;
    private long goalTime = System.currentTimeMillis();
    private WakeLock mWakeLock;
    private String tableId = null;
    private Integer position = 0;
    private MediaPlayer mp;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        if (tableId == null) {
            IntentIntegrator integrator = new IntentIntegrator(this);
            integrator.initiateScan(IntentIntegrator.QR_CODE_TYPES);
//            return;
        }

        View container = findViewById(R.id.container);
        container.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                long currentTime = System.currentTimeMillis();
                TextView text = (TextView)findViewById(R.id.text);
                TextView text2 = (TextView)findViewById(R.id.text2);
                if (event.getButtonState() == MotionEvent.BUTTON_PRIMARY) {
                    if((currentTime - goalTime) >= 5000){
                        team1score++;
                        goalTime = currentTime;
                        text.setText(team1score + " - ");
                        playGoalMusic();
                    }
                } else if (event.getButtonState() == MotionEvent.BUTTON_SECONDARY) {
                    if((currentTime - goalTime) >= 5000){
                        team2score++;
                        goalTime = currentTime;
                        text2.setText(team2score);
                        playGoalMusic();
                    }
                } else if (!gameIsPaused) {
                    gameIsPaused = true;
                    text.setText("Game Paused");
                    text2.setText("Game Paused");
                    Animation anim = new AlphaAnimation(0.0f, 1.0f);
                    anim.setDuration(500);
                    anim.setStartOffset(20);
                    anim.setRepeatMode(Animation.REVERSE);
                    anim.setRepeatCount(Animation.INFINITE);
                    text.startAnimation(anim);
                    text2.startAnimation(anim);
                    return false;
                }
                gameIsPaused = false;
                Animation animation = text.getAnimation();
                if (animation != null) {
                    animation.cancel();
                }
                animation = text2.getAnimation();
                if (animation != null) {
                    animation.cancel();
                }
                return false;
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);

        if (result != null) {
            String contents = result.getContents();
            Log.d(TAG, "scan result is:" + contents);
            Toast.makeText(getApplicationContext(), contents, Toast.LENGTH_LONG).show();

            String[] array = contents.split("::");
            if (array == null || array.length != 2) {
                Toast.makeText(getApplicationContext(), "Invalid QR code: " + contents, Toast.LENGTH_LONG).show();
            }
            tableId = array[0];
            position = Integer.valueOf(array[1]);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    View v = findViewById(R.id.player1);
                    if (position == 1) {
                        v.setBackgroundColor(0xFF0000);
                    } else {
                        v.setBackgroundColor(0x000000);
                    }

                    v = findViewById(R.id.player2);
                    if (position == 2) {
                        v.setBackgroundColor(0xFF0000);
                    } else {
                        v.setBackgroundColor(0x000000);
                    }

                    v = findViewById(R.id.player3);
                    if (position == 3) {
                        v.setBackgroundColor(0xFF0000);
                    } else {
                        v.setBackgroundColor(0x000000);
                    }

                    v = findViewById(R.id.player4);
                    if (position == 4) {
                        v.setBackgroundColor(0xFF0000);
                    } else {
                        v.setBackgroundColor(0x000000);
                    }
                }
            });
        }
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
        mp = MediaPlayer.create(getApplicationContext(), R.raw.beep);
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        if(mWakeLock!=null)
            mWakeLock.release();
        if(mp!=null)
            mp.release();
        super.onPause();
    }

    private void playGoalMusic(){
        mp.start();
    }

}
