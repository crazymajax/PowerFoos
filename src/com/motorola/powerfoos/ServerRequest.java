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

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;

public class ServerRequest {

    private static final String DEFAULT_SERVER_IP = "10.75.176.65";
    protected static String sServerAddress = DEFAULT_SERVER_IP;
    private Handler mHandler;
    String mUrl;

    /**
     * @param handler handler on which the result will be sent
     */
    public ServerRequest(Context context, Handler handler) {
        mHandler = handler;
        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        preferences.getString("server_ip", DEFAULT_SERVER_IP);
    }

//    /**
//     * Set the IP address of the server
//     * @param svrIPAddress Server IP address
//     */
//    public static void setServerAddress(String svrIPAddress) {
//        sServerAddress = svrIPAddress;
//    }


    /**
     * Make a request. On receiving a response from the result will be sent via the handler.
     * The key used to get the result is "result"
     * Sample code to get the result
     * public void handleMessage(Message msg) {
     *		String result;
     *		Bundle b = msg.getData();
     *		result = b.getString("result");
     * }
     */
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

    ////////////// Server Calls /////////////////////////

    /**
     * Join a game
     * @param playerId  ID of the player
     * @param position  Position of the player on the table
     * @param tableId   Id of the table
     */
    public void joinGame(String playerId, String position, String tableId) {
        mUrl = String.format("http://%s:8080/FoosballServer/player/join?playerId=%s&position=%s&tableId=%s",
                ServerRequest.sServerAddress, playerId, position, tableId);
        makeRequest();
    }

    /**
     * leave a game
     * @param playerId  ID of the player
     * @param tableId   Id of the table
     */
    public void leaveGame(String playerId, String position, String tableId) {
        mUrl = String.format("http://%s:8080/FoosballServer/player/leave?playerId=%s&tableId=%s",
                ServerRequest.sServerAddress, playerId, tableId);
        makeRequest();
    }

    /**
     * Set the table score
     * @param tableId   ID of the table
     * @param blackScore    Score of the black team
     * @param yellowScore   score of the yellow team
     */
    public void setScore(String tableId, String blackScore, String yellowScore) {
        mUrl = String.format("http://%s:8080/FoosballServer/score/setScore?tableId=%s&black=%s&yellow=%s",
                ServerRequest.sServerAddress, tableId, blackScore, yellowScore);
        makeRequest();
    }

    /**
     * Get the score of the table
     * @param tableId ID of the table
     */
    public void getScore(String tableId) {
        mUrl = String.format("http://%s:8080/FoosballServer/score/getScore?tableId=%s",
                ServerRequest.sServerAddress, tableId);
        makeRequest();
    }

    /**
     * Get the stats of a player
     * @param playerId ID of the player you want stats for
     */
    public void getPlayerStats(String playerId) {
        mUrl = String.format("http://%s:8080/FoosballServer/player/getPlayerStats?playerId=%s",
                ServerRequest.sServerAddress, playerId);
        makeRequest();
    }

    /**
     * Get the players on the table
     * @param tableId ID of the table
     */
    public void getPlayers(String tableId) {
        mUrl = String.format("http://%s:8080/FoosballServer/player/getPlayers?tableId=%s",
                ServerRequest.sServerAddress, tableId);
        makeRequest();
    }

    /**
     * Create player.
     * @param playerId ID of the player
     * @param name Name of the player
     */
    public void createPlayer(String playerId, String name) {
        //Create a url using the server address and the parameters
        mUrl = String.format("http://%s:8080/FoosballServer/player/create?playerId=%s&name=%s",
                ServerRequest.sServerAddress, playerId, name);
        makeRequest();
    }

    /**
     * Ends a game.
     * @param tableId ID of the table
     */
    public void endGame(String tableId) {
        //Create a url using the server address and the parameters
        mUrl = String.format("http://%s:8080/FoosballServer/game/end?tableId=%s",
                ServerRequest.sServerAddress, tableId);
        makeRequest();
    }

    /**
     * start a new game.
     * @param tableId ID of the table
     */
    public void newGame(String tableId) {
        //Create a url using the server address and the parameters
        mUrl = String.format("http://%s:8080/FoosballServer/game/newGame?tableId=%s",
                ServerRequest.sServerAddress, tableId);
        makeRequest();
    }
}
