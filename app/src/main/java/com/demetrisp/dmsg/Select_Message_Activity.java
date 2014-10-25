package com.demetrisp.dmsg;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class Select_Message_Activity extends Activity implements LoaderManager.LoaderCallbacks<Cursor> {

    ListView lvMsg;
    SimpleCursorAdapter adapter;
    Uri inboxURI;
    String[] mProjection;
    String mSelection;
    String thread;
    private static final int SMS_BODY_LOADER = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_msg);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        lvMsg = (ListView) findViewById(R.id.sms_list);
        inboxURI = Uri.parse("content://sms");
        ContentResolver cr = getContentResolver();
        thread = getIntent().getExtras().getString("Thread");
        mProjection = new String[]{"_id", "address", "body"};
        mSelection = "thread_id LIKE '%" + thread + "%'";
        getLoaderManager().initLoader(SMS_BODY_LOADER, null, this);


        lvMsg.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {


                String body = ((TextView) arg1.findViewById(android.R.id.text1)).getText().toString();
                //	String phone = ((TextView) arg1.findViewById(android.R.id.text2)).getText().toString();

                Intent intent = getIntent();
                intent.putExtra("smsBody", body);
                //intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT|Intent.FLAG_ACTIVITY_NEW_TASK);
                setResult(RESULT_OK, intent);
                Select_Message_Activity.this.finish();


            }
        });

    }

    @Override
    public Loader<Cursor> onCreateLoader(int loaderID, Bundle args) {
               /*
     * Takes action based on the ID of the Loader that's being created
     */
        switch (loaderID) {
            case SMS_BODY_LOADER:
                // Returns a new CursorLoader
                Log.d("Loader", "Inside Cursor Loader");
                return new CursorLoader(
                        this,   // Parent activity context
                        inboxURI,        // Table to query
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
        adapter = new SimpleCursorAdapter(Select_Message_Activity.this, android.R.layout.simple_list_item_1, data,
                new String[]{"body"}, new int[]{
                android.R.id.text1}, 0);
        lvMsg.setAdapter(adapter);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        lvMsg.setAdapter(null);
    }
}