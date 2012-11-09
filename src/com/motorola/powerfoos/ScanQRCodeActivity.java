package com.motorola.powerfoos;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.google.zxing.client.android.IntentIntegrator;
import com.google.zxing.client.android.IntentResult;

public class ScanQRCodeActivity extends Activity {

    Button b1;
    static String contents;
     private final static String TAG = "PowerFoos-ScanCode " ;
     String result;
     MyHandler mHandler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_qrcode);

        b1 = (Button) findViewById(R.id.button1);
        b1.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                mHandler = new MyHandler();
                ServerRequest req = new ServerRequest(getApplicationContext(), mHandler);
                req.getScore("23456");
                req.makeRequest();
            }

        });
    }


    class MyHandler extends Handler {

        public void handleMessage(Message msg) {
            String result;
            Bundle b = msg.getData();
            result = b.getString("result");

            Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_scan_qrcode, menu);
        return true;
    }


    public void onActivityResult(int requestCode, int resultCode, Intent intent) {


        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);

        if (result != null) {
            String contents = result.getContents();
            Log.d(TAG, "scan result is:" + contents);
            Toast.makeText(getApplicationContext(), contents, Toast.LENGTH_LONG).show();
        }
    }
}
