package com.demetrisp.dmsg.com.demetrisp.dmsg.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.demetrisp.dmsg.R;

/**
 * Created by dima on 11/3/14.
 */
public class SetPasswordDialog extends DialogFragment {
    private EditText mPassword;
    AlertDialog.Builder builder;
    LayoutInflater inflater;
    View dialogView;
    Button okButton;
    SetPasswordDialogListener activity;

    public interface SetPasswordDialogListener {
        public void onDialogPositiveClick(String pass);
    }

    //Using to customize dialog dismiss behaviour
    @Override
    public void onStart() {
        super.onStart();
        AlertDialog d = (AlertDialog) getDialog();
        if (d != null) {
            okButton = d.getButton(Dialog.BUTTON_POSITIVE);
            okButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    activity = (SetPasswordDialogListener) getActivity();
                    if (mPassword.getText().toString().length() >= 8) {
                        activity.onDialogPositiveClick(mPassword.getText().toString());
                        dismiss();
                    } else {
                        Toast toast = Toast.makeText(getActivity(), "Please use key longer than 8 characters for example 'abcdefgh'", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    }
                }
            });
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        builder = new AlertDialog.Builder(getActivity());
        inflater = getActivity().getLayoutInflater();
        dialogView = inflater.inflate(R.layout.dialog_setpassword, null);
        mPassword = (EditText) dialogView.findViewById(R.id.password_dialog);
        mPassword.requestFocus();

        builder.setView(dialogView)
                .setPositiveButton("OK", null)
                .setNegativeButton("Later", null)
                .setTitle("Set Password")
                .setIcon(R.drawable.ic_launcher);
        return builder.create();
    }
}