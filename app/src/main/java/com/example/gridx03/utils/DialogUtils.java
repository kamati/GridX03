package com.example.gridx03.utils;

import android.app.Dialog;
import android.view.ViewGroup;

import androidx.core.content.ContextCompat;
import androidx.viewbinding.ViewBinding;

public class DialogUtils {

    public static Dialog createDialog(ViewBinding layoutResViewBinding, int drawableId, Boolean cancellable) {
        Dialog dialog = new Dialog(layoutResViewBinding.getRoot().getContext());
        dialog.setContentView(layoutResViewBinding.getRoot());
        dialog.setCancelable(cancellable);
        dialog.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(layoutResViewBinding.getRoot().getContext(), drawableId));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        return dialog;
    }

}
