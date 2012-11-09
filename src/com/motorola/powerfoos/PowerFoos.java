package com.motorola.powerfoos;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.google.zxing.client.android.PreferencesActivity;

public class PowerFoos extends Activity {
    private static final String FOOS_EMAIL_ID = "foos_email_id";
    private MediaPlayer mp;
    private boolean mSoundOn = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_power_foos);

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        mSoundOn = pref.getBoolean("sound_on", true);
        playIntroMusic();

        Button start = (Button)findViewById(R.id.start);
        Button settings = (Button)findViewById(R.id.settings);
        Button credits = (Button)findViewById(R.id.credits);
        Button help = (Button)findViewById(R.id.help);

//        Typeface font = Typeface.createFromAsset(getAssets(), "8-BIT WONDER.ttf");

        if (start != null) {
//            start.setTypeface(font);
            start.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Toast.makeText(getApplicationContext(), "User pressed start", Toast.LENGTH_SHORT).show();
                    SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    if (pref.getString(FOOS_EMAIL_ID, null)!=null) {
                        startActivity(new Intent(PowerFoos.this, GameActivity.class));
                    }else{
                        onStartClicked(v);
                    }
                }
            });
        }

        if (settings != null) {
//            settings.setTypeface(font);
            settings.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Toast.makeText(getApplicationContext(), "User pressed settings", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(PowerFoos.this, PreferencesActivity.class));
                }
            });
        }

        if (credits != null) {
//            credits.setTypeface(font);
            credits.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(PowerFoos.this, CreditsActivity.class));
                }
            });
        }

        if (help != null) {
//            help.setTypeface(font);
            help.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Toast.makeText(getApplicationContext(), "User pressed help", Toast.LENGTH_SHORT).show();
                    onStartClicked(v);
                }
            });
        }
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.activity_power_foos, menu);
//        return true;
//    }

    public void onStartClicked(View v) {
        Intent intent = new Intent (this,WelcomeScreen.class);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mp = MediaPlayer.create(getApplicationContext(), R.raw.intro);
    }

    @Override
    protected void onPause() {
        if (mp != null)
            mp.release();
        super.onPause();
    }

    private void playIntroMusic(){
        if (mSoundOn && mp != null)
            mp.start();
    }
}
