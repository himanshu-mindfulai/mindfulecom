package com.mindfulai.Utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;
import android.widget.TextView;

import com.mindfulai.ministore.R;

import java.util.Objects;

public class CustomProgressDialog {

    private Dialog dialog;
    private Context mContext;

    public CustomProgressDialog(Context context, String titleText) {

        dialog = new Dialog(context);
        Objects.requireNonNull(dialog.getWindow()).requestFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        mContext = context;

        dialog.getWindow().setContentView(R.layout.custom_progress_dialog);
        TextView title = dialog.findViewById(R.id.textView);
        title.setText(titleText);

        dialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(Color.TRANSPARENT));


    }

    public void show() {

        try {

            if (((Activity) mContext).isFinishing()) {
                return;
            }
            if (!dialog.isShowing() && dialog != null) {
                dialog.show();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    void dismiss() {

        if (((Activity) mContext).isFinishing()) {
            return;
        }
        if (dialog.isShowing() && dialog != null) {
            dialog.dismiss();
        }
    }

    void setCancelable() {
        dialog.setCancelable(false);
    }


    boolean isShowing() {

        return dialog.isShowing();
    }


}
