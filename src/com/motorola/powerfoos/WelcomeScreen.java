package com.motorola.powerfoos;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class WelcomeScreen extends Activity {
    public static final String TAG =
        WelcomeScreen.class.getSimpleName();
    static String mSelectedEmail;
    private static final String ACCOUNT_TYPE_GMAIL	= "com.google";
    public static final String FOOS_EMAIL_ID = "foos_email_id";
    private Handler mServerHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle data = msg.getData();
            String result = data.getString("result");
            Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
        }
    };
    private ServerRequest sr = new ServerRequest(mServerHandler);


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_screen);

        Spinner accountListSpinner = (Spinner) findViewById(R.id.accounts_spinner);
        if(accountListSpinner!=null){
            //set accounts in the accounts list spinner.
            List<String> accList = new ArrayList<String>();
            AccountManager accMgr = AccountManager.get(this);

            for (Account act : accMgr.getAccountsByType(ACCOUNT_TYPE_GMAIL)) {
                accList.add(act.name);
            }
            ArrayAdapter<String> accAdapter = new ArrayAdapter <String>(this,android.R.layout.simple_spinner_item, accList);
            accAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            accountListSpinner.setAdapter(accAdapter);

            //If user had registered set the current spinner to the registered user.
            if (null != mSelectedEmail) {
                int pos=accList.indexOf(mSelectedEmail);
                accountListSpinner.setSelection(pos);
            }
            //add the selection listener for spinner
            accountListSpinner.
            setOnItemSelectedListener(
                    new OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(
                                AdapterView<?> parent, View view, int pos, long id) {
                            String email = (String) parent.getItemAtPosition(pos);
                            mSelectedEmail = email;
                            //Save email in the prefs
                            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                            SharedPreferences.Editor ed = pref.edit();
                            ed.putString(FOOS_EMAIL_ID, mSelectedEmail);
                            ed.commit();
                        }
                        @Override
                        public void onNothingSelected( AdapterView<?> arg0) {

                        }
                    });
        }

        /*Cursor cursor = getContacts();
        TextView contactView = (TextView) findViewById(R.id.contact_info);
        while (cursor.moveToNext()) {
            String displayName = cursor.getString(cursor.getColumnIndex(ContactsContract.Data.DISPLAY_NAME));
            contactView.append("Name: ");
            contactView.append(displayName);
            contactView.append("\n");
        }*/
    }


    /*private Cursor getContacts() {
        // Run query
        Uri uri = ContactsContract.Contacts.CONTENT_URI;
        String[] projection = new String[] { ContactsContract.Contacts._ID, ContactsContract.Contacts.DISPLAY_NAME };
        String selection = ContactsContract.Contacts.IN_VISIBLE_GROUP + " = '" + ("1") + "'";
        String[] selectionArgs = null;
        String sortOrder = ContactsContract.Contacts.DISPLAY_NAME+ " COLLATE LOCALIZED ASC";
        return managedQuery(uri, projection, selection, selectionArgs, sortOrder);

    }*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_power_foos, menu);
        return true;
    }

    public void onNextClicked(View v) {
        Log.d(TAG, "MAJAX: the email selected is: " + mSelectedEmail);
        //and send it to server
        sr.createPlayer(mSelectedEmail, mSelectedEmail);
        Intent intent = new Intent (this,GameActivity.class);
        startActivity(intent);
    }

}
