package com.demetrisp.dmsg;


import android.app.ActionBar;
import android.app.Activity;
import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import java.util.ArrayList;

public class Message_Box_Activity extends Activity implements LoaderManager.LoaderCallbacks<Cursor> {


    ListView lvMsg;
    SmsArrayAdapter adapter;
    Intent intent;

    //Loader
    private static final int SMS_THREADS_LOADER = 0;
    Uri mDataUrl;
    String[] mProjection;
    String mSelection;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }
        setContentView(R.layout.activity_sms);



        //Loader

        mDataUrl = Uri.parse("content://sms");
        mProjection = new String[]{"_id", "address", "body", "thread_id"};
        mSelection = "1=1 ) GROUP BY (thread_id";
        getLoaderManager().initLoader(SMS_THREADS_LOADER, null, this);




        //getActionBar().setDisplayHomeAsUpEnabled(true);
        intent = new Intent(Message_Box_Activity.this, Select_Message_Activity.class);
        lvMsg = (ListView) findViewById(R.id.sms_list);
        lvMsg.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                SmsMessage sms = (SmsMessage) arg0.getAdapter().getItem(arg2);
                intent.putExtra("Thread", sms.thread_id);
                intent.setFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
                startActivity(intent);
                Message_Box_Activity.this.finish();
            }
        });


    }

    @Override
    public Loader<Cursor> onCreateLoader(int loaderID, Bundle args) {
           /*
     * Takes action based on the ID of the Loader that's being created
     */
        switch (loaderID) {
            case SMS_THREADS_LOADER:
                // Returns a new CursorLoader
                return new CursorLoader(
                        this,   // Parent activity context
                        mDataUrl,        // Table to query
                        mProjection,     // Projection to return
                        mSelection,      // Selection clause
                        null,            // No selection arguments
                        null             // Default sort order
                );

            default:
                // An invalid id was passed in
                return null;
        }

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        ArrayList<SmsMessage> smsList = new ArrayList<SmsMessage>();
        data.moveToFirst();
        while (!data.isAfterLast()) {
            SmsMessage sms = new SmsMessage(data.getString(
                    data.getColumnIndex("address")),
                    data.getString(data.getColumnIndex("body")),
                    data.getString(data.getColumnIndex("thread_id")),
                    getApplicationContext());

            smsList.add(sms);
            data.moveToNext();
        }
        adapter = new SmsArrayAdapter(getApplicationContext(), R.id.tvName, smsList);

        lvMsg.setAdapter(adapter);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        lvMsg.setAdapter(null);
    }
}