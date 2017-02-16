/*
 * Copyright (c) 2014 Beijing Dnurse Technology Ltd. All rights reserved.
 */

package com.example.wei.usb_demo.activity.base;

import android.app.Activity;
import android.os.Process;
import android.util.Log;

import com.example.wei.usb_demo.activity.MainActivity;

import java.util.List;
import java.util.Stack;

public class AppManager {

    private static Stack<Activity> activityStack;
    private static AppManager instance;

    private AppManager() {
    }

    /**
     * 单一实例
     */
    public static AppManager getAppManager() {
        if (instance == null) {
            instance = new AppManager();
        }
        return instance;
    }

    /**
     * 添加Activity到堆栈
     */
    public void addActivity(Activity activity) {
        if (activityStack == null) {
            activityStack = new Stack<Activity>();
        }
        activityStack.add(activity);
        Log.e("printStackTag", "ADD:" + activity.toString());
        printStack(activityStack);
    }

    private void printStack(Stack stack) {
        if (activityStack == null) {
            return;
        }
        for (Activity activity : activityStack) {
            Log.i("printStackTag", activityStack.size() + " - activityStack : " + activity.toString());
        }
    }

    /**
     * 获取当前Activity（堆栈中最后一个压入的）
     */
    public Activity currentActivity() {
        if (activityStack == null) {
            return null;
        }
        if (activityStack.size() == 0) {
            return null;
        }
        Activity activity = activityStack.lastElement();
        return activity;
    }

    /**
     * 获取当前MainActivity
     */
    public MainActivity getMainActivity() {
        if (activityStack == null) {
            return null;
        }
        if (activityStack.size() == 0) {
            return null;
        }

        Activity activity = null;
        for (int i = 0; i < activityStack.size(); i++) {
            Activity act = activityStack.get(i);
            if (act.getClass().equals(MainActivity.class)) {
                activity = act;
                break;
            }
        }

        return (MainActivity) activity;
    }

    /**
     * 结束当前Activity（堆栈中最后一个压入的）
     */
    public void finishActivity() {
        if (activityStack == null) {
            return;
        }
        Activity activity = activityStack.lastElement();
        finishActivity(activity);
    }

    /**
     * 结束指定的Activity
     */
    public void finishActivity(Activity activity) {
        if (activityStack == null) {
            return;
        }
        if (activity != null) {
            Log.e("printStackTag", "FINISH:" + activity.toString());
            activityStack.remove(activity);
            activity.finish();
            activity = null;
        }
        printStack(activityStack);
    }

    /**
     * 结束指定类名的Activity
     */
    public void finishActivity(Class<?> cls) {
//        for (Activity activity : activityStack) {
//            if (activity.getClass().equals(cls)) {
//                finishActivity(activity);
//            }
//        }
        if (activityStack == null) {
            return;
        }
        int length = activityStack.size();
        for (int i = 0; i < length; i++) {
            Activity activity = activityStack.get(i);
            if (activity.getClass().equals(cls)) {
                finishActivity(activity);
                i--;
                length--;
            }
        }
        Log.e("printStackTag", "finishActivity - CLS:" + cls);
        printStack(activityStack);
    }

    public void popToActivity(Class<?> cls) {
        if (activityStack == null) {
            return;
        }
        boolean bFind = false;
        for (Activity activity : activityStack) {
            if (activity.getClass().equals(cls)) {
                bFind = true;
                break;
            }
        }

        if (bFind) {
            for (int i = 0, size = activityStack.size(); i < size; i++) {
                Activity act = activityStack.peek();
                if (null != act) {
                    if (!act.getClass().equals(cls)) {
                        activityStack.pop();
                        act.finish();
                    } else {
                        break;
                    }
                }
            }
        }
        Log.e("printStackTag", "popToActivity - CLS:" + cls);
        printStack(activityStack);
    }

    /**
     * 结束所有Activity
     */
    public void finishAllActivity() {
        if (activityStack == null) {
            return;
        }
        for (int i = 0, size = activityStack.size(); i < size; i++) {
            Activity act = activityStack.pop();
            if (null != act) {
                act.finish();
            }
        }
        activityStack.clear();
        Log.e("printStackTag", "finishAllActivity");
    }

    /**
     * 退出应用程序
     */
    public void killSelf() {
        Process.killProcess(Process.myPid());
        System.exit(0);
    }

    public int getActivityCount() {
        if (activityStack == null) {
            return 0;
        }
        return activityStack.size();
    }

    public boolean containsActivity(Class cls) {
        if (activityStack == null || activityStack.isEmpty()) {
            return false;
        }

        for (Activity activity : activityStack) {
            if (activity.getClass().equals(cls)) {
                return true;
            }
        }
        return false;
    }


    public List<Activity> getActivity() {
        if (activityStack == null) {
            return null;
        }
        return activityStack.subList(0, activityStack.size());
    }

    public void popMainActivity() {
        if (activityStack == null) {
            return;
        }
        while (getActivityCount() > 1) {
            Activity activity = activityStack.get(getActivityCount() - 1);
            Log.e("popMainActivity", "activity : " + activity);//被顶掉时死循环log
            if (activity instanceof MainActivity) {
                break;
            } else {
                finishActivity(activity);
            }
        }
    }

    public boolean isRunBackground() {
        if (activityStack == null) {
            return true;
        }

        for (Activity activity : activityStack) {
            if (activity instanceof BaseActivity) {
                if (((BaseActivity) activity).isActive()) {
                    return false;
                }
            }
        }
        return true;
    }
}