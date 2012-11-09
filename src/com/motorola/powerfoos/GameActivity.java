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
import android.widget.ImageView;
import android.widget.TextView;
//import android.widget.Toast;

import com.google.zxing.client.android.IntentIntegrator;
import com.google.zxing.client.android.IntentResult;

import org.json.JSONObject;

//import java.util.ArrayList;

@TargetApi(14)
public class GameActivity extends Activity {
    private static final String TAG = GameActivity.class.getSimpleName();
    public static int SCORE_REFRESH_DELAY = 1000;
    private static final int PAUSE_LENGTH = 5000;
    private static final int DEFAULT_WINNING_SCORE = 5;
    private int team1score = 0;
    private int team2score = 0;
    private Integer mWinningScore = DEFAULT_WINNING_SCORE;
    private boolean mSoundOn = true;
    private boolean mGameIsPaused = false;
    private boolean mGameIsOver = false;
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
//            //Toast.makeText(getApplicationContext(), result, //Toast.LENGTH_LONG).show();
            Log.d(TAG, "MAJAX: Server Result: " + result);

            try {
                final JSONObject jObject = new JSONObject(result);
                String request = jObject.getString("request");
                Log.d(TAG, "MAJAX: request = " + request);

                if ("getScore".equals(request)) {
                    team1score = jObject.getInt("black");
                    team2score = jObject.getInt("yellow");
                    mGameIsOver = !jObject.getBoolean("gameActive");
                    Log.d(TAG, "MAJAX: team1score = " + team1score);
                    Log.d(TAG, "MAJAX: team2score = " + team2score);
                    Log.d(TAG, "MAJAX: mGameIsOver = " + mGameIsOver);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            TextView text = (TextView)findViewById(R.id.text);
                            TextView text2 = (TextView)findViewById(R.id.text2);
                            if (mGameIsOver) {
                                if (team1score > team2score) {
                                    text2.setText(team1score + " Winner!");
                                    text.setText(team2score + " Loser...");
                                } else {
                                    text.setText(team2score + " Winner!");
                                    text2.setText(team1score + " Loser...");
                                }
                            } else if (!mGameIsPaused) {
                                updateScore(text, text2);
                            }
                        }
                    });
                } else if ("getPlayers".equals(request)) {
                    final String n0 = jObject.has("name0") ? jObject.getString("name0") : null;
                    final String n1 = jObject.has("name1") ? jObject.getString("name1") : null;
                    final String n2 = jObject.has("name2") ? jObject.getString("name2") : null;
                    final String n3 = jObject.has("name3") ? jObject.getString("name3") : null;
                    final int position1 = jObject.has("position0") ? jObject.getInt("position0") : -1;
                    final int position2 = jObject.has("position1") ? jObject.getInt("position1") : -1;
                    final int position3 = jObject.has("position2") ? jObject.getInt("position2") : -1;
                    final int position4 = jObject.has("position3") ? jObject.getInt("position3") : -1;

                    String[] players = new String[4];
                    if (position1 > 0)
                        players[position1 - 1] = n0;
                    if (position2 > 0)
                        players[position2 - 1] = n1;
                    if (position3 > 0)
                        players[position3 - 1] = n2;
                    if (position4 > 0)
                        players[position4 - 1] = n3;

                    final String name1 = players[0];
                    final String name2 = players[1];
                    final String name3 = players[2];
                    final String name4 = players[3];

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            TextView tv = (TextView)findViewById(R.id.name1);
                            ImageView iv = (ImageView)findViewById(R.id.player1);
                            if (tv != null && iv != null) {
                                if (name1 != null) {
                                    tv.setText(name1);
                                    tv.setVisibility(View.VISIBLE);
                                    iv.setVisibility(View.VISIBLE);
                                } else {
                                    tv.setVisibility(View.GONE);
                                    iv.setVisibility(View.GONE);
                                }
                            }

                            tv = (TextView)findViewById(R.id.name2);
                            iv = (ImageView)findViewById(R.id.player2);
                            if (tv != null && iv != null) {
                                if (name2 != null) {
                                    tv.setText(name2);
                                    tv.setVisibility(View.VISIBLE);
                                    iv.setVisibility(View.VISIBLE);
                                } else {
                                    tv.setVisibility(View.GONE);
                                    iv.setVisibility(View.GONE);
                                }
                            }

                            tv = (TextView)findViewById(R.id.name3);
                            iv = (ImageView)findViewById(R.id.player3);
                            if (tv != null && iv != null) {
                                if (name3 != null) {
                                    tv.setText(name3);
                                    tv.setVisibility(View.VISIBLE);
                                    iv.setVisibility(View.VISIBLE);
                                } else {
                                    tv.setVisibility(View.GONE);
                                    iv.setVisibility(View.GONE);
                                }
                            }

                            tv = (TextView)findViewById(R.id.name4);
                            iv = (ImageView)findViewById(R.id.player4);
                            if (tv != null && iv != null) {
                                if (name4 != null) {
                                    tv.setText(name4);
                                    tv.setVisibility(View.VISIBLE);
                                    iv.setVisibility(View.VISIBLE);
                                } else {
                                    tv.setVisibility(View.GONE);
                                    iv.setVisibility(View.GONE);
                                }
                            }
                        }
                    });
                }

            } catch (Exception e) {
                Log.e(TAG, "Error: " + e.getMessage() + " Unable to parse the JSON response: " + result);
                e.printStackTrace();
            }
        }
    };
    private ServerRequest sr;

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
        mGameIsPaused = true;
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
        mGameIsPaused = false;
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

        sr = new ServerRequest(getApplicationContext(), mServerHandler);

        try {
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            mPlayerId = pref.getString(WelcomeScreen.FOOS_EMAIL_ID, null);
            mSoundOn = pref.getBoolean("sound_on", true);
            final String winScoreString = pref.getString("winning_score", null);
            if (winScoreString != null) {
                mWinningScore = Integer.valueOf(winScoreString);
            }
        } catch(Exception e) {
        }

        if (mTableId == null) {
            IntentIntegrator integrator = new IntentIntegrator(this);
            integrator.initiateScan(IntentIntegrator.QR_CODE_TYPES);
        }

        View container = findViewById(R.id.container);
        container.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                long currentTime = System.currentTimeMillis();
                TextView text = (TextView)findViewById(R.id.text);
                TextView text2 = (TextView)findViewById(R.id.text2);


                if (mGameIsOver) {
                    if (event.getButtonState() == MotionEvent.BUTTON_PRIMARY
                        && event.getButtonState() == MotionEvent.BUTTON_SECONDARY) {
                        // Ignore
                    } else {
                        mGameIsOver = false;
                        mGameIsPaused = false;
                        team1score = 0;
                        team2score = 0;
                        updateScore(text, text2);
                        sr.setScore(mTableId, String.valueOf(team1score), String.valueOf(team2score));
                    }
                    return false;
                }

                if (!mGameIsPaused) {
                    if (event.getButtonState() == MotionEvent.BUTTON_PRIMARY) {
                        team1score++;
                        playGoalMusic();
                        if (team1score >= mWinningScore) {
                            mGameIsOver = true;
                            text2.setText(team1score + " Winner!");
                            text.setText(team2score + " Loser...");
                        } else {
                            pauseGame(text, text2);
                            mHandler.postDelayed(unPause, PAUSE_LENGTH);
                        }
                        sr.setScore(mTableId, String.valueOf(team1score), String.valueOf(team2score));
                    } else if (event.getButtonState() == MotionEvent.BUTTON_SECONDARY) {
                        team2score++;
                        playGoalMusic();
                        if (team2score >= mWinningScore) {
                            mGameIsOver = true;
                            text.setText(team2score + " Winner!");
                            text2.setText(team1score + " Loser...");
                        } else {
                            pauseGame(text, text2);
                            mHandler.postDelayed(unPause, PAUSE_LENGTH);
                        }
                        sr.setScore(mTableId, String.valueOf(team1score), String.valueOf(team2score));
                    }
                    if (team1score >= mWinningScore
                            || team2score >= mWinningScore) {
                        sr.endGame(mTableId);
                    }
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
            final Context appCtx = getApplicationContext();
            //Toast.makeText(appCtx, contents, //Toast.LENGTH_LONG).show();

            if (contents == null) {
                GameActivity.this.finish();
                return;
            }

            String[] array = contents.split("::");
            if (array == null || array.length != 2) {
                //Toast.makeText(appCtx, "Invalid QR code: " + contents, //Toast.LENGTH_LONG).show();
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

        mHandler.postDelayed(refreshData, SCORE_REFRESH_DELAY);
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        if(mWakeLock!=null)
            mWakeLock.release();
        if(mp!=null)
            mp.release();

        mHandler.removeCallbacks(refreshData);
        super.onPause();
    }

    private void playGoalMusic(){
        if (mSoundOn)
            mp.start();
    }

}
