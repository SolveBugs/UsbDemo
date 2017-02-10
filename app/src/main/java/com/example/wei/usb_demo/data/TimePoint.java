package com.example.wei.usb_demo.data;

import android.content.Context;

import com.example.wei.pl2303_test.R;


public enum TimePoint {
    Time_None("None", 0, R.string.data_trend_all_day),
    Time_Breakfast_Before("Breakfast_Before", 1, R.string.breakfast_before),
    Time_Breakfast_After("Breakfast_After", 2, R.string.breakfast_after),
    Time_Lunch_Before("Lunch_Before", 3, R.string.lunch_before),
    Time_Lunch_After("Lunch_After", 4, R.string.lunch_after),
    Time_Supper_Before("Supper_Before", 5, R.string.supper_before),
    Time_Supper_After("Supper_After", 6, R.string.supper_after),
    Time_Night("Night", 7, R.string.night),
    Time_Dawn("Dawn", 8, R.string.dawn),
    Time_Random("Random", 9, R.string.random), ;

    private String name;
    private int pointId;
    private int resId;

    private TimePoint(String name, int pointId, int resId) {
        this.name = name;
        this.pointId = pointId;
        this.resId = resId;
    }

    public static TimePoint getTimePointById(int id) {
        if (Time_Breakfast_Before.getPointId() == id) {
            return Time_Breakfast_Before;
        } else if (Time_Breakfast_After.getPointId() == id) {
            return Time_Breakfast_After;
        } else if (Time_Lunch_Before.getPointId() == id) {
            return Time_Lunch_Before;
        } else if (Time_Lunch_After.getPointId() == id) {
            return Time_Lunch_After;
        } else if (Time_Supper_Before.getPointId() == id) {
            return Time_Supper_Before;
        } else if (Time_Supper_After.getPointId() == id) {
            return Time_Supper_After;
        } else if (Time_Night.getPointId() == id) {
            return Time_Night;
        } else if (Time_Dawn.getPointId() == id) {
            return Time_Dawn;
        } else if (Time_Random.getPointId() == id) {
            return Time_Random;
        }
        return Time_None;
    }

    public static TimePoint getTimePointByName(String name) {
        if (Time_Breakfast_Before.getName().equals(name)) {
            return Time_Breakfast_Before;
        } else if (Time_Breakfast_After.getName().equals(name)) {
            return Time_Breakfast_After;
        } else if (Time_Lunch_Before.getName().equals(name)) {
            return Time_Lunch_Before;
        } else if (Time_Lunch_After.getName().equals(name)) {
            return Time_Lunch_After;
        } else if (Time_Supper_Before.getName().equals(name)) {
            return Time_Supper_Before;
        } else if (Time_Supper_After.getName().equals(name)) {
            return Time_Supper_After;
        } else if (Time_Night.getName().equals(name)) {
            return Time_Night;
        } else if (Time_Dawn.getName().equals(name)) {
            return Time_Dawn;
        } else if (Time_Random.getName().equals(name)) {
            return Time_Random;
        }
        return Time_None;
    }

    public String getName() {
        return name;
    }

    public int getPointId() {
        return pointId;
    }

    public int getResId() {
        return resId;
    }

    public String getResString(Context context) {
        if (resId == 0)
            return "";
        return context.getResources().getString(resId);
    }


    public static TimePoint getTimePointByResName(String name, Context context) {
        if (Time_Breakfast_Before.getResString(context).equals(name)) {
            return Time_Breakfast_Before;
        } else if (Time_Breakfast_After.getResString(context).equals(name)) {
            return Time_Breakfast_After;
        } else if (Time_Lunch_Before.getResString(context).equals(name)) {
            return Time_Lunch_Before;
        } else if (Time_Lunch_After.getResString(context).equals(name)) {
            return Time_Lunch_After;
        } else if (Time_Supper_Before.getResString(context).equals(name)) {
            return Time_Supper_Before;
        } else if (Time_Supper_After.getResString(context).equals(name)) {
            return Time_Supper_After;
        } else if (Time_Night.getResString(context).equals(name)) {
            return Time_Night;
        } else if (Time_Dawn.getResString(context).equals(name)) {
            return Time_Dawn;
        } else if (Time_Random.getResString(context).equals(name)) {
            return Time_Random;
        }
        return Time_None;
    }
}
