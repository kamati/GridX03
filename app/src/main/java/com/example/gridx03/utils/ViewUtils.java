package com.example.gridx03.utils;

import android.view.View;
import android.widget.TextView;


import com.example.gridx03.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import androidx.core.content.ContextCompat;

public class ViewUtils {

    public static void showSnackbar(View view, String message) {
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG);
        snackbar.setAction("OK", v -> snackbar.dismiss());
        ((TextView) snackbar.getView().findViewById(com.google.android.material.R.id.snackbar_text)).setSingleLine(false);
        snackbar.show();
    }

    public static void showErrorOnEditText(TextInputLayout textInputLayout, String errorMessage) {
        textInputLayout.setErrorEnabled(true);
        textInputLayout.setError(errorMessage);
    }

    public static void enableButton(MaterialButton materialButton) {
        materialButton.setEnabled(true);
        materialButton.setTextColor(ContextCompat.getColor(materialButton.getContext(), R.color.white));
    }

    public static void disableButton(MaterialButton materialButton) {
        materialButton.setEnabled(false);
        materialButton.setTextColor(ContextCompat.getColor(materialButton.getContext(), R.color.white_70));
    }

    public static void makeVisible(View view) {
        view.setVisibility(View.VISIBLE);
    }

    public static void makeInvisible(View view) {
        view.setVisibility(View.INVISIBLE);
    }

    public static void makeGone(View view) {
        view.setVisibility(View.GONE);
    }

}
