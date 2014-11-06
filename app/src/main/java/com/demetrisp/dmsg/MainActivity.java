package com.demetrisp.dmsg;

import com.demetrisp.dmsg.com.demetrisp.dmsg.browser.FileChooser;
import com.demetrisp.dmsg.com.demetrisp.dmsg.dialogs.HelpDialogFragment;
import com.demetrisp.dmsg.com.demetrisp.dmsg.dialogs.SetPasswordDialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;


public class MainActivity extends Activity implements OnClickListener, SetPasswordDialog.SetPasswordDialogListener {

    Button enc;
    Button dec;
    Button exportSms, importSms;
    Button close_help;
    EditText inputText;
    EditText keyText;
    CheckBox checkBox;
    boolean import_preference;
    boolean export_as_sms;
    static final int GET_SMS_BODY = 1;  // The request code

    //Browser
    private static final int REQUEST_PATH = 2; //Browsing for file
    String fileName;
    String filePath;

    //help view
    View helpView,mainView;


    @Override
    protected void onResume() {
        super.onResume();
        loadPreferences();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        helpView = findViewById(R.id.top_layout);
        helpView.setVisibility(View.INVISIBLE);
        mainView = findViewById(R.id.main_layout);
        loadPreferences();

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        checkBox = (CheckBox) findViewById(R.id.checkBox);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                    // show password
                    keyText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                } else {
                    // hide password
                    keyText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
            }
        });


        enc = (Button) findViewById(R.id.bEncrypt);
        enc.setOnClickListener(this);
        dec = (Button) findViewById(R.id.bDecrypt);
        dec.setOnClickListener(this);
        importSms = (Button) findViewById(R.id.bImportSms);
        importSms.setOnClickListener(this);
        exportSms = (Button) findViewById(R.id.bExportSms);
        exportSms.setOnClickListener(this);
        inputText = (EditText) findViewById(R.id.editText2);
        inputText.requestFocus();
        keyText = (EditText) findViewById(R.id.editText1);
        // help screen
        close_help = (Button) findViewById(R.id.bclosehelp);
        close_help.setOnClickListener(this);


        //Close help view on touch anywhere
        helpView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                helpView.setVisibility(View.INVISIBLE);
                return false;
            }
        });



        PackageManager pm = this.getPackageManager();

        if (!pm.hasSystemFeature(PackageManager.FEATURE_TELEPHONY)) {
            importSms.setEnabled(false);
        }

        showSetPasswordDialog();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_exit:
                finish();
                System.exit(0);
                return true;
            case R.id.action_preferences:
                Intent i = new Intent(this, SettingsActivity.class);
                startActivity(i);
                return true;
            case R.id.action_filepicker:
                Intent browser = new Intent(this, FileChooser.class);
                startActivityForResult(browser, REQUEST_PATH);
                return true;
            case R.id.action_help:


                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
                helpView.setVisibility(View.VISIBLE);

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }



    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {

        savedInstanceState.putString("key", keyText.toString());
        savedInstanceState.putString("input", inputText.toString());

        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GET_SMS_BODY) {
            if (resultCode == RESULT_OK) {
                if (data.getExtras().containsKey("smsBody")) {
                    inputText.setText(data.getStringExtra("smsBody"));
                }
            }
        }
        if (requestCode == REQUEST_PATH) {
            if (resultCode == RESULT_OK) {

                fileName = data.getStringExtra("fileName");
                filePath = data.getStringExtra("filePath");

                //Get the text file
                File file = new File(filePath, fileName);

                StringBuilder text = new StringBuilder();

                try {
                    BufferedReader br = new BufferedReader(new FileReader(file));
                    String line;

                    while ((line = br.readLine()) != null) {
                        text.append(line);
                        text.append('\n');
                    }
                    inputText.setText(text);
                } catch (IOException e) {
                    inputText.setText("Cannot Read File");
                }


            }
        }

    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.bEncrypt) {

            String text = inputText.getText().toString();
            String key = keyText.getText().toString();

            try {
                if (key.length() < 8) {
                    throw new IllegalArgumentException("INVALID");
                } else {
                    Des des = new Des(key, text);
                    inputText.setText(des.encrypt());
                    enc.setEnabled(false);
                    dec.setEnabled(true);

                }
            } catch (IllegalArgumentException e) {
                Toast toast = Toast.makeText(MainActivity.this, "Please use key longer than 8 characters for example 'abcdefgh'", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }

        }

        if (v.getId() == R.id.bExportSms) {
            enc.setEnabled(true);
            dec.setEnabled(true);
            if (export_as_sms) {
                composeSmsMessage(inputText.getText().toString());
                Log.d("message", "Export as sms");
            } else {
                composeTextMessage(inputText.getText().toString());
                Log.d("message", "Export as text");
            }


        }
        if (v.getId() == R.id.bImportSms) {
            enc.setEnabled(true);
            dec.setEnabled(true);
            Intent intent = new Intent(MainActivity.this, Message_Box_Activity.class);
            startActivityForResult(intent, GET_SMS_BODY);
        }

        if (v.getId() == R.id.bDecrypt) {
            String text = inputText.getText().toString();
            String key = keyText.getText().toString();
            try {
                if (key.length() < 8) {
                    throw new Exception();
                } else {
                    Des des = new Des(key, text);
                    String decrypted = des.decrypt();
                    if (decrypted.equals("Error in Decryption")) {
                        Toast toast = Toast.makeText(MainActivity.this, "Text or password are incorrect", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    } else {
                        inputText.setText(decrypted);
                        enc.setEnabled(true);
                        dec.setEnabled(false);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast toast = Toast.makeText(MainActivity.this, "Please use key longer than 8 characters for example 'abcdefgh'", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
        }
        if(v.getId() == R.id.bclosehelp){
            helpView.setVisibility(View.INVISIBLE);
            mainView.setVisibility(View.VISIBLE);
        }


    }

    private void loadPreferences() {
        SharedPreferences mySharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        import_preference = mySharedPreferences.getBoolean("pref_import_sms", false);
        export_as_sms = mySharedPreferences.getBoolean("pref_export_sms", false);

    }

    public void composeTextMessage(String message) {
        Intent smsIntent = new Intent(Intent.ACTION_SEND);
        smsIntent.setType("text/plain");
        smsIntent.putExtra(Intent.EXTRA_TEXT, message);
        String title = getResources().getString(R.string.chooser_title);
        Intent chooser = Intent.createChooser(smsIntent, title);

        // Verify the intent will resolve to at least one activity
        if (smsIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(chooser);
        }
    }

    public void composeSmsMessage(String message) {
        Intent smsIntent = new Intent(Intent.ACTION_SENDTO);
        smsIntent.setData(Uri.parse("smsto:"));  // This ensures only SMS apps respond
        smsIntent.putExtra("sms_body", message);
        String title = getResources().getString(R.string.chooser_title);
        Intent chooser = Intent.createChooser(smsIntent, title);
        if (smsIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(chooser);
        }
    }

    public void showSetPasswordDialog() {
        DialogFragment newFragment = new SetPasswordDialog();
        newFragment.setCancelable(false);
        newFragment.show(getFragmentManager(), "password_dialog");
    }

    @Override
    public void onDialogPositiveClick(String pass) {
        keyText.setText(pass);

    }
}