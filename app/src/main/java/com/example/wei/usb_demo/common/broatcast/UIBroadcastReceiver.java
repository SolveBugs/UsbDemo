package com.example.wei.usb_demo.common.broatcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

/**
 * Created by ZhouYuzhen on 15/1/23.
 */
public class UIBroadcastReceiver extends BroadcastReceiver {
    public final static String ACTION_KEY = "action_key";
    public final static int BROADCAST_ACTION_NONE = 0;

    //=============给注册了广播的页面发送指令=============
    public final static int BROADCAST_ACTION_BASE = 1;

    public final static int BROADCAST_ACTION_DISCERN_TIME_OUT = BROADCAST_ACTION_BASE + 1;//识别设备超时广播

    public final static int BROADCAST_ACTION_SYNC_MAX = 1000;

    //=============只给当前显示页面发送的指令=============
    public final static int BROADCAST_ACTIVE_BASE = BROADCAST_ACTION_SYNC_MAX + 1;


    public final static int BROADCAST_ACTIVE_MAX = 2000;


    private OnActionReceive actionReceive;
    private OnActiveReceive activeReceive;

    public interface OnActionReceive {
        //只要该页面注册广播就会调用该方法
        void onActionReceive(int action, Bundle bundle);
    }

    public interface OnActiveReceive {
        //只调用当前活动页面的该方法
        void onActiveReceive(int action, Bundle bundle);
    }

    public void setOnActionReceive(OnActionReceive actionReceive) {
        this.actionReceive = actionReceive;
    }

    public void setOnActiveReceive(OnActiveReceive activeReceive) {
        this.activeReceive = activeReceive;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null) {
            int action = intent.getIntExtra(ACTION_KEY, BROADCAST_ACTION_NONE);
            if (action != BROADCAST_ACTION_NONE && actionReceive != null
                    && action > BROADCAST_ACTION_BASE && action < BROADCAST_ACTION_SYNC_MAX) {
                Bundle bundle = intent.getExtras();
                actionReceive.onActionReceive(action, bundle);
            }

            if (action != BROADCAST_ACTION_NONE && activeReceive != null
                    && action > BROADCAST_ACTIVE_BASE && action < BROADCAST_ACTIVE_MAX) {
                Bundle bundle = intent.getExtras();
                activeReceive.onActiveReceive(action, bundle);
            }
        }
    }

    public final static IntentFilter getIntentFilter(Context context) {
        IntentFilter filter = new IntentFilter();
        filter.addAction(context.getPackageName() + UIBroadcastReceiver.class.getName());
        return filter;
    }

    public final static void sendBroadcast(Context context, int action, Bundle bundle) {
        Intent intent = new Intent();
        intent.putExtra(ACTION_KEY, action);
        if (bundle != null)
            intent.putExtras(bundle);
        if (context != null) {
            intent.setAction(context.getPackageName() + UIBroadcastReceiver.class.getName());
            context.sendBroadcast(intent);
        }

    }
}