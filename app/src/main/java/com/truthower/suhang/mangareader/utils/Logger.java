package com.truthower.suhang.mangareader.utils;

import android.util.Log;

public class Logger {
	public static void d(String d) {
		Log.d("MangaReader", d);
	}
	public static void i(String i) {
		Log.i("MangaReader", i);
	}
	public static void d(Double d) {
		
		d(String.valueOf(d));
	}
	public static void d(int d) {
		d(String.valueOf(d));
	}
}
