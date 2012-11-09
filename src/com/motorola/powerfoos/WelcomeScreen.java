package com.motorola.powerfoos;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@TargetApi(11)
public class WelcomeScreen extends Activity implements LoaderManager.LoaderCallbacks<Cursor> {
    public static final String TAG =
        WelcomeScreen.class.getSimpleName();
    static String mSelectedEmail;
    private Bitmap userPhoto = null;
    private String userName = null;
    private long mContactID;
    private long mPhotoID;
    private static final String ACCOUNT_TYPE_GMAIL	= "com.google";
    public static final String FOOS_EMAIL_ID = "foos_email_id";
    public static final String FOOS_CONTACT_ID = "foos_contact_id";
    public static final String FOOS_PHOTO_ID = "foos_photo_id";
    private Handler mServerHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle data = msg.getData();
            String result = data.getString("result");
            Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
        }
    };
    private ServerRequest sr;


    @TargetApi(11)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_screen);

        sr = new ServerRequest(getApplicationContext(), mServerHandler);

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
                            ed.putLong(FOOS_CONTACT_ID, mContactID);
                            ed.putLong(FOOS_PHOTO_ID, mPhotoID);
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
        getLoaderManager().initLoader(0, null, this);
    }

    @TargetApi(14)
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle arguments) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(
                        ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY),
                ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE + " = ?",
                new String[]{ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<String>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            final String name = cursor.getString(ProfileQuery.DISPLAY_NAME);
            mPhotoID = cursor.getLong(ProfileQuery.PHOTO_ID);
            final String email = cursor.getString(ProfileQuery.ADDRESS);
            final int isPrimary = cursor.getInt(ProfileQuery.IS_PRIMARY);
            mContactID = cursor.getLong(ProfileQuery._ID);

            Log.d(TAG, "MAJAX: -------------------------------");
            Log.d(TAG, "MAJAX: name = " + name);
            Log.d(TAG, "MAJAX: photoId = " + mPhotoID);
            Log.d(TAG, "MAJAX: email = " + email);
            Log.d(TAG, "MAJAX: isPrimary = " + isPrimary);
            Log.d(TAG, "MAJAX: id = " + mContactID);
            if (mSelectedEmail != null && mSelectedEmail.equals(email)) {
                Log.d(TAG, "MAJAX: isSelected = YES");
                userPhoto = loadContactPhoto(getContentResolver(), mContactID, mPhotoID);
                userName = name;

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TextView nam = (TextView)findViewById(R.id.contactName);
                        nam.setText("Name: " + name);
                        ImageView image = (ImageView)findViewById(R.id.contactPhoto);
                        image.setImageBitmap(userPhoto);
                    }
                });
            }

            emails.add(email);
            // Potentially filter on ProfileQuery.IS_PRIMARY
            cursor.moveToNext();
        }
    }

    @TargetApi(11)
    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
    }

    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
                ContactsContract.Profile.DISPLAY_NAME,
                ContactsContract.Contacts.PHOTO_ID,
                ContactsContract.Contacts._ID
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
        int DISPLAY_NAME = 2;
        int PHOTO_ID = 3;
        int _ID = 4;
    }

    public static Bitmap loadContactPhoto(ContentResolver cr, long  id,long photo_id) {

        Uri uri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, id);
        InputStream input = ContactsContract.Contacts.openContactPhotoInputStream(cr, uri);
        if (input != null) {
            return BitmapFactory.decodeStream(input);
        } else {
            Log.d("PHOTO","first try failed to load photo");
        }

        byte[] photoBytes = null;

        Uri photoUri = ContentUris.withAppendedId(ContactsContract.Data.CONTENT_URI, photo_id);

        Cursor c = cr.query(photoUri, new String[] {ContactsContract.CommonDataKinds.Photo.PHOTO}, null, null, null);

        try
        {
            if (c.moveToFirst())
                photoBytes = c.getBlob(0);

        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();

        } finally {

            c.close();
        }

        if (photoBytes != null)
            return BitmapFactory.decodeByteArray(photoBytes,0,photoBytes.length);
        else
            Log.d("PHOTO","second try also failed");
        return null;
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
        sr.createPlayer(mSelectedEmail, userName);
        Intent intent = new Intent (this,GameActivity.class);
        startActivity(intent);
    }

}
