package com.motorola.powerfoos;

import com.google.zxing.client.android.IntentIntegrator;
import com.google.zxing.client.android.IntentResult;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class ScanQRCodeActivity extends Activity {

	Button b1;
	static String contents;
	 private final static String TAG = "PowerFoos-ScanCode " ;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_qrcode);
        
        b1 = (Button) findViewById(R.id.button1);
		b1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//Intent intent = new Intent("com.google.zxing.client.android.SCAN");
				//intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
				//startActivityForResult(intent, 0);

				IntentIntegrator integrator = new IntentIntegrator(ScanQRCodeActivity.this);
	            integrator.initiateScan(IntentIntegrator.QR_CODE_TYPES);
				
			}
			
		}); 
    }
    
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
    	
    	/*
		if (requestCode == 0) {
			if (resultCode == RESULT_OK) {
				contents = intent.getStringExtra("SCAN_RESULT");
				//String format = intent.getStringExtra("SCAN_RESULT_FORMAT");
				Log.i("Barcode Result", contents);
				if(contents.contains("http:"))
				{
					//Intent i1 = new Intent(QRCodeSampleActivity.this, webclass.class);
					//startActivity(i1);
				}
				else
				{
					Toast.makeText(getApplicationContext(), contents, 
							Toast.LENGTH_LONG).show();
				}
				// Handle successful scan
			} else if (resultCode == RESULT_CANCELED) {
				// Handle cancel
				Log.i("Barcode Result","Result canceled");
			}
		}*/
	}
}
