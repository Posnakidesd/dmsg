package com.demetrisp.dmsg;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;


public class SmsArrayAdapter extends ArrayAdapter<SmsMessage> {
    private final Context context;
    private final ArrayList<SmsMessage> smsList;

    public SmsArrayAdapter(Context context, int textViewResourceId, ArrayList<SmsMessage> smsList) {
        super(context, textViewResourceId, smsList);
        this.context = context;
        this.smsList = smsList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.contact_list, parent, false);
        TextView tvName = (TextView) rowView.findViewById(R.id.tvName);
        TextView tvbody = (TextView) rowView.findViewById(R.id.tvBody);
        ImageView contactIcon = (ImageView) rowView.findViewById(R.id.contactIcon);
        if(smsList.get(position).senderName!=null)
        tvName.setText(smsList.get(position).senderName);
        else tvName.setText(smsList.get(position).senderNumber);
        tvbody.setText(smsList.get(position).body);
        if(smsList.get(position).image_uri!=null) {
            contactIcon.setImageURI(Uri.parse(smsList.get(position).image_uri));
        }
        else if(smsList.get(position).photo!=null){
            contactIcon.setImageBitmap(smsList.get(position).photo);
        }
        else contactIcon.setImageResource(R.drawable.defaultcontact);
        return  rowView;

    }
}