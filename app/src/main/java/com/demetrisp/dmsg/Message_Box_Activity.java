package com.demetrisp.dmsg;


import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import java.util.ArrayList;

public class Message_Box_Activity extends ActionBarActivity {


    ListView lvMsg;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);

    }

    // Cursor Adapter
    SmsArrayAdapter adapter;
    Intent intent;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_sms);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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
        Uri inboxURI = Uri.parse("content://sms");
        String[] reqCols = new String[]{"_id", "address", "body", "thread_id"};
        ContentResolver cr = getContentResolver();
        Cursor c = cr.query(inboxURI, reqCols, "1=1 ) GROUP BY (thread_id", null, null);
        ArrayList<SmsMessage> smsList = new ArrayList<SmsMessage>();
        c.moveToFirst();
        while (!c.isAfterLast()) {
            SmsMessage sms = new SmsMessage(c.getString(c.getColumnIndex("address")), c.getString(c.getColumnIndex("body")), c.getString(c.getColumnIndex("thread_id")), getApplicationContext());

            smsList.add(sms);
            c.moveToNext();
        }
        c.close();
        adapter = new SmsArrayAdapter(getApplicationContext(), R.id.tvName, smsList);

        lvMsg.setAdapter(adapter);

    }
}