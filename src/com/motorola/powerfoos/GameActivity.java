package com.motorola.powerfoos;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.preference.PreferenceManager;
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
    public static int SCORE_REFRESH_DELAY = 1000;
    private static final int PAUSE_LENGTH = 5000;
    private int team1score = 0;
    private int team2score = 0;
    private boolean gameIsPaused = false;
    private WakeLock mWakeLock;
    private String mPlayerId = null;
    private String mTableId = null;
    private Integer mPosition = 0;
    private MediaPlayer mp;
    private Handler mHandler = new Handler();
    private Handler mServerHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle data = msg.getData();
            String result = data.getString("result");
            Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
            Log.d(TAG, "MAJAX: Server Result: " + result);
        }
    };
    private ServerRequest sr = new ServerRequest(mServerHandler);

    private Runnable unPause = new Runnable() {
        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    TextView text = (TextView)findViewById(R.id.text);
                    TextView text2 = (TextView)findViewById(R.id.text2);
                    resumeGame(text, text2);
                }
            });
        }
    };

    private Runnable refreshData = new Runnable() {
        @Override
        public void run() {
            sr.getScore(mTableId);
            sr.getPlayers(mTableId);
            mHandler.postDelayed(this, SCORE_REFRESH_DELAY);
        }
    };

    private void pauseGame(TextView text, TextView text2) {
        gameIsPaused = true;
        text.setText("Game\nPaused");
        text2.setText("Game\nPaused");
        Animation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(500);
        anim.setStartOffset(20);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(Animation.INFINITE);
        text.startAnimation(anim);
        text2.startAnimation(anim);
    }

    private void resumeGame(TextView text, TextView text2) {
        gameIsPaused = false;
        Animation animation = text.getAnimation();
        if (animation != null) {
            animation.cancel();
        }
        animation = text2.getAnimation();
        if (animation != null) {
            animation.cancel();
        }
        updateScore(text, text2);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        mPlayerId = pref.getString(WelcomeScreen.FOOS_EMAIL_ID, null);

        if (mTableId == null) {
            IntentIntegrator integrator = new IntentIntegrator(this);
            integrator.initiateScan(IntentIntegrator.QR_CODE_TYPES);
        }

        mHandler.postDelayed(refreshData, SCORE_REFRESH_DELAY);

        View container = findViewById(R.id.container);
        container.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                long currentTime = System.currentTimeMillis();
                TextView text = (TextView)findViewById(R.id.text);
                TextView text2 = (TextView)findViewById(R.id.text2);

                if (!gameIsPaused) {
                    if (event.getButtonState() == MotionEvent.BUTTON_PRIMARY) {
                        team1score++;
                        playGoalMusic();
                        pauseGame(text, text2);
                        mHandler.postDelayed(unPause, PAUSE_LENGTH);
                    } else if (event.getButtonState() == MotionEvent.BUTTON_SECONDARY) {
                        team2score++;
                        playGoalMusic();
                        pauseGame(text, text2);
                        mHandler.postDelayed(unPause, PAUSE_LENGTH);
                    }
                    sr.setScore(mTableId, String.valueOf(team1score), String.valueOf(team2score));
                }
                return false;
            }
        });
    }

    private void updateScore(TextView text, TextView text2) {
        text.setText(team2score + " - ");
        text2.setText(String.valueOf(team1score));
    }

    /**
     * This gets called when the QR code is scanned
     */
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);

        if (result != null) {
            String contents = result.getContents();
            Log.d(TAG, "scan result is:" + contents);
            Toast.makeText(getApplicationContext(), contents, Toast.LENGTH_LONG).show();

            if (contents == null) {
                return;
            }

            String[] array = contents.split("::");
            if (array == null || array.length != 2) {
                Toast.makeText(getApplicationContext(), "Invalid QR code: " + contents, Toast.LENGTH_LONG).show();
            }
            mTableId = array[0];
            mPosition = Integer.valueOf(array[1]);

            sr.joinGame(mPlayerId, mPosition.toString(), mTableId);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    View v = findViewById(R.id.player1);
                    if (mPosition == 2) {
                        v = findViewById(R.id.player2);
                    } else if (mPosition == 3) {
                        v = findViewById(R.id.player3);
                    } else if (mPosition == 4) {
                        v = findViewById(R.id.player4);
                    }
                    v.setScaleX(1.5f);
                    v.setScaleY(1.5f);

                    if (mPosition < 3) {
                        View c = findViewById(R.id.container);
                        c.setRotation(180);
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
