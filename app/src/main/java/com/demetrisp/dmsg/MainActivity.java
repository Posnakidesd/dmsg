package com.demetrisp.dmsg;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity implements OnClickListener {

    Button enc;
    Button dec;
    Button exportSms, importSms;
    EditText inputText;
    EditText keyText;
    CheckBox checkBox;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
        keyText = (EditText) findViewById(R.id.editText1);


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
        if (resultCode == RESULT_OK) {
            if (data.getExtras().containsKey("smsBody")) {
                inputText.setText(data.getStringExtra("smsBody"));
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
            Intent smsIntent = new Intent(Intent.ACTION_VIEW);
            smsIntent.setType("vnd.android-dir/mms-sms");
            smsIntent.putExtra("address", "");
            smsIntent.putExtra("sms_body", inputText.getText().toString());
            startActivity(smsIntent);
        }
        if (v.getId() == R.id.bImportSms) {
            enc.setEnabled(true);
            dec.setEnabled(true);
            Intent intent = new Intent(MainActivity.this, Message_Box_Activity.class);
            startActivityForResult(intent, 1);
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


    }

}