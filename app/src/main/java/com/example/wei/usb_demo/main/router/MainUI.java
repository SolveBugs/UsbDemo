/*
 * Copyright (c) 2014 Beijing Dnurse Technology Ltd. All rights reserved.
 */

package com.example.wei.usb_demo.main.router;


import com.example.wei.usb_demo.common.module.ModuleID;

/**
 * Created by ygc on 14-10-20.
 */
public class MainUI {
    public final static int MAIN = ModuleID.Main;
    public final static int BLOOD_SUGAR = MAIN + 1;//血糖
    public final static int BLOOD_PRESS = MAIN + 2;//血压
    public final static int HEART_RATE = MAIN + 3;//心跳
    public final static int BLOOD_OXYGEN = MAIN + 4;//血氧
    public final static int READ_CARD = MAIN + 5;//读卡
    public final static int PRINTER = MAIN + 6;//打印
    public final static int BLOOD_OXYGEN_HISTORY = MAIN + 7;    //血氧历史记录
    public final static int HEART_RATE_HISTORY = MAIN + 8;    //心电历史记录
    public final static int BLOOD_OXYGEN_REVIEW = MAIN + 9;    //血氧数据回放

    private MainUI() {

    }
}
