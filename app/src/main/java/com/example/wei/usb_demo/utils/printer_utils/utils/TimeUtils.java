package com.example.wei.usb_demo.utils.printer_utils.utils;

public class TimeUtils {

	public static void WaitMs(long ms) {
		long time = System.currentTimeMillis();
		while (System.currentTimeMillis() - time < ms)
			;
	}
}
