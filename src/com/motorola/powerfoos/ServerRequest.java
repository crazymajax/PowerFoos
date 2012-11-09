package com.motorola.powerfoos;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class ServerRequest {
	
	private Handler mHandler; 
	String mUrl; 
	
	public ServerRequest (String url, Handler handler) {
		
		mUrl  = url; 
		mHandler = handler; 
	}
	
	public void makeRequest () {
		
		RequestTask task = new RequestTask();
	    task.execute(new String[] { mUrl });
		
	}
	
	public class RequestTask extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... arg0) {
			// TODO Auto-generated method stub
			String mUrl = arg0[0];
			String result = ""; 
			String line; 
			
			HttpClient client = new DefaultHttpClient();
	        HttpGet get = new HttpGet(mUrl);
	        
	        
	        try {
	           
	            HttpResponse response = client.execute(get);

	            
	            StatusLine statusLine = response.getStatusLine();
	            int statusCode = statusLine.getStatusCode();

	            if (statusCode == 200) { // Ok
	                
	                BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

	                
	                while ((line = rd.readLine()) != null) {
	                    result += line;
	                }
	            }
	        } catch (ClientProtocolException e) {
	            e.printStackTrace();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }

	        return result; // This value will be returned to your onPostExecute(result) method
		}

		 @Override
		    protected void onPostExecute(String result) {
			 //Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
			 Bundle b= new Bundle(); 
			 b.putString("result", result);
			 Message msg = new Message(); 
			 msg.setData(b);
			 
			 mHandler.sendMessage(msg);
		    }
	}

}
