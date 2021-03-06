package com.demetrisp.dmsg.com.demetrisp.dmsg.browser;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.demetrisp.dmsg.R;

import java.io.File;
import java.sql.Date;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FileChooser extends ListActivity {

    private File currentDir;
    private FileArrayAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }

        currentDir = new File(Environment.getExternalStorageDirectory().getPath());
        fill(currentDir);
    }

    private void fill(File f) {
        File[] dirs = f.listFiles();
        this.setTitle(f.getAbsolutePath());
        //Log.d("dpmessage", f.getName());
        List<Item> dir = new ArrayList<Item>();
        List<Item> fls = new ArrayList<Item>();
        try {
            for (File ff : dirs) {
                Date lastModDate = new Date(ff.lastModified());
                DateFormat formater = DateFormat.getDateTimeInstance();
                String date_modify = formater.format(lastModDate);
                if (ff.isDirectory()) {

                    File[] folderItemsNumber = ff.listFiles();
                    int counter = 0;
                    if(folderItemsNumber != null){
                       for(File item: folderItemsNumber){
                           String extension = item.getPath();
                           if(extension.endsWith(".txt")||item.isDirectory())
                               counter++;
                       }
                    }
                    String num_item = String.valueOf(counter);
                    if (counter == 1) num_item = num_item + " item";
                    else num_item = num_item + " items";

                    //String formated = lastModDate.toString();
                    dir.add(new Item(ff.getName(), num_item, date_modify, ff.getAbsolutePath(), "folder_icon"));
                } else {
                    String filePath = ff.getPath();
                    if(filePath.endsWith(".txt")) {
                        fls.add(new Item(ff.getName(), ff.length() + " Bytes", date_modify, ff.getAbsolutePath(), "file_icon"));
                    }
                }
            }
        } catch (Exception e) {


        }
        Collections.sort(dir);
        Collections.sort(fls);
        dir.addAll(fls);
        if (!f.getAbsolutePath().equalsIgnoreCase(Environment.getExternalStorageDirectory().getAbsolutePath())) {
            dir.add(0, new Item("..", "Go Back", "", f.getParent(), "back_icon"));
        }

        adapter = new FileArrayAdapter(FileChooser.this, R.layout.file_view, dir);
        this.setListAdapter(adapter);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Item o = adapter.getItem(position);
        if (o.getImage().equalsIgnoreCase("folder_icon") || o.getImage().equalsIgnoreCase("back_icon")) {
            currentDir = new File(o.getPath());
            fill(currentDir);
        } else {
            onFileClick(o);
        }
    }

    private void onFileClick(Item o) {
        //Toast.makeText(this, "Folder Clicked: "+ currentDir, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent();
        intent.putExtra("filePath", currentDir.toString());
        intent.putExtra("fileName", o.getName());
        setResult(RESULT_OK, intent);
        finish();
    }
}
