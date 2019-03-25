package com.rhondasmith.stormy;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;

public class AlertDialogFragment extends DialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Context context = getActivity();
        // Builder is a nested class inside the alert dialogue class:
        // the reasons have to do with the factory method pattern:
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        // now we need to pass in the context
        // normally would use this or this.activity
        // but now we are in a different class
        // fortunately, the DialogFragment class we are extending
        // has a useful method to get the activity where this DialogFragment
        // was created - getActivity()

        builder.setTitle(R.string.error_title)
        .setMessage(R.string.error_message)
        // we use null listener when we don't want anything specific to happen w/ button tap:
        .setPositiveButton(R.string.error_button_ok_text, null);

        return builder.create();
    }
}
