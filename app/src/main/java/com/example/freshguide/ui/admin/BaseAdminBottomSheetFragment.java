package com.example.freshguide.ui.admin;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.example.freshguide.R;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public abstract class BaseAdminBottomSheetFragment extends BottomSheetDialogFragment {

    @Override
    public int getTheme() {
        return R.style.ThemeOverlay_FreshGuide_BottomSheet;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!(getDialog() instanceof BottomSheetDialog)) {
            return;
        }

        BottomSheetDialog dialog = (BottomSheetDialog) getDialog();
        View sheet = dialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
        if (sheet == null) {
            return;
        }

        sheet.setBackgroundResource(R.drawable.bg_bottom_sheet_surface);
        ViewGroup.LayoutParams params = sheet.getLayoutParams();
        if (params != null) {
            params.height = ViewGroup.LayoutParams.MATCH_PARENT;
            sheet.setLayoutParams(params);
        }

        BottomSheetBehavior<View> behavior = BottomSheetBehavior.from(sheet);
        behavior.setFitToContents(true);
        behavior.setSkipCollapsed(true);
        behavior.setDraggable(true);
        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }
}
