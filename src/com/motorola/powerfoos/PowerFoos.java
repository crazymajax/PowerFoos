package com.motorola.powerfoos;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class PowerFoos extends Activity {

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
                    Toast.makeText(getApplicationContext(), "User pressed start", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(PowerFoos.this, GameActivity.class));
                }
            });
        }

        if (settings != null) {
            settings.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getApplicationContext(), "User pressed settings", Toast.LENGTH_SHORT).show();
                }
            });
        }

        if (credits != null) {
            credits.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getApplicationContext(), "User pressed credits", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(PowerFoos.this, CreditsActivity.class));
                }
            });
        }

        if (help != null) {
            help.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getApplicationContext(), "User pressed help", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.activity_power_foos, menu);
//        return true;
//    }


}
