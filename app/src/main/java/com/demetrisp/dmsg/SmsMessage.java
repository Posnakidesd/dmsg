package com.demetrisp.dmsg;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;

import java.io.IOException;
import java.io.InputStream;


/**
 * Created by dima on 8/12/14.
 */
public class SmsMessage {
    String senderName;
    String senderNumber;
    String body;
    String image_uri = null;
    Context context;
    String thread_id;
    Long contact_id;
    Bitmap photo = null;

    public SmsMessage(String senderNumber, String body, String thread_id, Context context) {
        this.senderNumber = senderNumber;
        this.body = body;
        this.context = context;
        this.thread_id = thread_id;
        initialize();

    }

    public void initialize() {
        ContentResolver cr = context.getContentResolver();
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(senderNumber));
            Cursor cursor = cr.query(uri, new String[]{ContactsContract.PhoneLookup._ID, ContactsContract.PhoneLookup.DISPLAY_NAME, ContactsContract.PhoneLookup.PHOTO_THUMBNAIL_URI}, null, null, null);
            if (cursor.moveToFirst()) {
                senderName = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
                contact_id = cursor.getLong(cursor.getColumnIndex(ContactsContract.PhoneLookup._ID));
                image_uri = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.PHOTO_THUMBNAIL_URI));
            }
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        } else {
            Uri contact_uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(senderNumber));
            Cursor cursor = cr.query(contact_uri, new String[]{ContactsContract.PhoneLookup._ID, ContactsContract.PhoneLookup.DISPLAY_NAME}, null, null, null);
            if (cursor.moveToFirst()) {
                senderName = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
                contact_id = cursor.getLong(cursor.getColumnIndex(ContactsContract.PhoneLookup._ID));

            }
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }

            try {
                Uri uri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contact_id);
                InputStream stream = ContactsContract.Contacts.openContactPhotoInputStream(
                        context.getContentResolver(), uri);
                photo = BitmapFactory.decodeStream(stream);
                stream.close();

            } catch (NullPointerException e) {
                //e.printStackTrace();
            } catch (IllegalArgumentException e) {
               // e.printStackTrace();
            } catch (IOException e) {
                //e.printStackTrace();
            }


        }


    }


}
