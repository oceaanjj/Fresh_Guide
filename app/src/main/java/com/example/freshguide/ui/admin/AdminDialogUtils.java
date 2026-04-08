package com.example.freshguide.ui.admin;

import android.content.Context;
import android.widget.Button;

import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.freshguide.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public final class AdminDialogUtils {

    private AdminDialogUtils() {
    }

    public static void showDestructiveConfirmation(@NonNull Fragment fragment,
                                                   @NonNull String title,
                                                   @NonNull String message,
                                                   @NonNull String positiveLabel,
                                                   @NonNull Runnable onConfirm) {
        showConfirmation(fragment, title, message, positiveLabel, onConfirm, null, R.color.red_accent);
    }

    public static void showPrimaryConfirmation(@NonNull Fragment fragment,
                                               @NonNull String title,
                                               @NonNull String message,
                                               @NonNull String positiveLabel,
                                               @NonNull Runnable onConfirm,
                                               Runnable onCancel) {
        showConfirmation(fragment, title, message, positiveLabel, onConfirm, onCancel, R.color.green_primary);
    }

    private static void showConfirmation(@NonNull Fragment fragment,
                                         @NonNull String title,
                                         @NonNull String message,
                                         @NonNull String positiveLabel,
                                         @NonNull Runnable onConfirm,
                                         Runnable onCancel,
                                         @ColorRes int positiveColorRes) {
        Context context = fragment.requireContext();
        AlertDialog dialog = new MaterialAlertDialogBuilder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(positiveLabel, (d, w) -> onConfirm.run())
                .setNegativeButton("Cancel", (d, w) -> {
                    if (onCancel != null) {
                        onCancel.run();
                    }
                })
                .create();
        dialog.setOnShowListener(d -> tintDialogButtons(dialog, context, positiveColorRes));
        dialog.show();
    }

    private static void tintDialogButtons(@NonNull AlertDialog dialog,
                                          @NonNull Context context,
                                          @ColorRes int positiveColorRes) {
        Button positive = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        Button negative = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        if (positive != null) {
            positive.setTextColor(ContextCompat.getColor(context, positiveColorRes));
        }
        if (negative != null) {
            negative.setTextColor(ContextCompat.getColor(context, R.color.text_secondary));
        }
    }
}
