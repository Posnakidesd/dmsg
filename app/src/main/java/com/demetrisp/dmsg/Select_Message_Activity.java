package com.demetrisp.dmsg;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class Select_Message_Activity extends ActionBarActivity {

    //  GUI Widget
    ListView lvMsg;

    // Cursor Adapter
    SimpleCursorAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_msg);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        lvMsg = (ListView) findViewById(R.id.sms_list);
        Uri inboxURI = Uri.parse("content://sms");
        ContentResolver cr = getContentResolver();
        String thread = getIntent().getExtras().getString("Thread");

        String[] reqCols = new String[]{"_id", "address", "body"};
        String sms = "thread_id LIKE '%" + thread + "%'";
        Cursor c = cr.query(inboxURI, reqCols, sms, null, null);
        adapter = new SimpleCursorAdapter(Select_Message_Activity.this, android.R.layout.simple_list_item_1, c,
                new String[]{"body"}, new int[]{
                android.R.id.text1});
        lvMsg.setAdapter(adapter);
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

}