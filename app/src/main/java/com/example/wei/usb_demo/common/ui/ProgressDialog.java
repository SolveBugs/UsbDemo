package com.example.wei.usb_demo.common.ui;

import android.content.Context;
import android.content.DialogInterface;

import com.example.wei.usb_demo.activity.base.AppManager;


/**
 * Created by dnurse3 on 14/11/13.
 */
public class ProgressDialog {

    private static ProgressDialog instance = null;
    private ProgressHUD dialog;
    private DialogInterface.OnCancelListener onCancelListener;
    private static final int maxDelay = 5000;
    private static final int cancelableDismiss = 0;


    public static synchronized ProgressDialog getInstance() {
//        if (instance == null) {
//            instance = new ProgressDialog();
//        }
        return new ProgressDialog();
    }

    public void show(Context context, String title) {
        dialog = ProgressHUD.get(context, title);
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if (onCancelListener != null) {
                    onCancelListener.onCancel(dialog);
                }
            }
        });

        if (dialog != null && !dialog.isShowing()) {
            if (AppManager.getAppManager().isRunBackground()) {
                return;
            }
            dialog.show();
        }
    }

    public void dismiss() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    public void show(Context context, String title, boolean cancelable) {
        dialog = ProgressHUD.get(context, title);
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if (onCancelListener != null) {
                    onCancelListener.onCancel(dialog);
                }
            }
        });

        if (dialog != null) {
            dialog.setCancelable(cancelable);
            if (!dialog.isShowing()) {
                dialog.show();//TODO
//            dismissHandle.sendEmptyMessageDelayed(cancelableDismiss, maxDelay);
            }
        }
    }

    public boolean isShowing() {
        if (dialog == null) {
            return false;
        }

        return dialog.isShowing();
    }

    public void setMessage(String message) {
        if (dialog != null) {
            dialog.setMessage(message);
        }
    }

    public void setOnCancelListener(DialogInterface.OnCancelListener onCancelListener) {
        this.onCancelListener = onCancelListener;
    }


    public void showWithLimt(Context context, String title, boolean flag) {
        dialog = ProgressHUD.get(context, title);
        dialog.setCanceledOnTouchOutside(flag);
        dialog.setCancelable(flag);
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if (onCancelListener != null) {
                    onCancelListener.onCancel(dialog);
                }
            }
        });

        if (dialog != null && !dialog.isShowing()) {
            if (AppManager.getAppManager().isRunBackground()) {
                return;
            }
            dialog.show();
        }
    }
}
