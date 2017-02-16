package com.example.wei.usb_demo.common.ui;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.wei.pl2303_test.R;


/**
 * Created by dnurse3 on 14/11/13.
 */
public class ProgressHUD extends Dialog {

    public ProgressHUD(Context context) {
        super(context);
    }

    public ProgressHUD(Context context, int theme) {
        super(context, theme);
    }

    public static ProgressHUD get(Context context, String titleStr) {
        ProgressHUD dialog = new ProgressHUD(context, R.style.ProgressDialog);
        dialog.setContentView(R.layout.common_progress_dialog_layout);
        ImageView animationIV = (ImageView) dialog.findViewById(R.id.progress_webview_id);
        animationIV.setImageResource(R.drawable.loading1);
        RotateAnimation rotateAnimation = (RotateAnimation) AnimationUtils.loadAnimation(context, R.anim.comm_progress);
        animationIV.startAnimation(rotateAnimation);
        TextView title = (TextView) dialog.findViewById(R.id.progress_dialog_title);
        if (!TextUtils.isEmpty(titleStr)) {
            title.setText(titleStr);
        }
        return dialog;
    }

    public void setMessage(String message) {
        TextView title = (TextView) findViewById(R.id.progress_dialog_title);
        if (!TextUtils.isEmpty(message)) {
            title.setText(message);
        }
    }
}