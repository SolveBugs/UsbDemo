/*
 * Copyright (c) 2014 Beijing Dnurse Technology Ltd. All rights reserved.
 */

package com.example.wei.usb_demo.common.module;

/**
 * Created by ygc on 14-10-20.
 */
public class UriMatcherInfo {
    private String path;
    private int code;

    public UriMatcherInfo(String path, int code) {
        this.path = path;
        this.code = code;
    }

    public String getPath() {
        return path;
    }

    public int getCode() {
        return code;
    }
}

