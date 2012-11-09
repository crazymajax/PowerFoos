package com.motorola.powerfoos;

import com.google.zxing.client.android.PreferencesActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class PowerFoos extends Activity {
    private static final String FOOS_EMAIL_ID = "foos_email_id";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_power_foos);

        Button start = (Button)findViewById(R.id.start);
        Button settings = (Button)findViewById(R.id.settings);
        Button credits = (Button)findViewById(R.id.credits);
        Button help = (Button)findViewById(R.id.help);

        if (start != null) {
            start.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Toast.makeText(getApplicationContext(), "User pressed start", Toast.LENGTH_SHORT).show();
                    SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    if (pref.getString(FOOS_EMAIL_ID, null)!=null) {
                        startActivity(new Intent(PowerFoos.this, GameActivity.class));
                        //onStartClicked(v);
                    }else{
                        onStartClicked(v);
                    }
                }
            });
        }

        if (settings != null) {
            settings.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Toast.makeText(getApplicationContext(), "User pressed settings", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(PowerFoos.this, PreferencesActivity.class));
                }
            });
        }

        if (credits != null) {
            credits.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(PowerFoos.this, CreditsActivity.class));
                }
            });
        }

        if (help != null) {
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
}
