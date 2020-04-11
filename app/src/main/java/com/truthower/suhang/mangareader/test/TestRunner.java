package com.truthower.suhang.mangareader.test;

import com.truthower.suhang.mangareader.utils.Logger;

public class TestRunner implements Runnable {
    @Override
    public void run() {
        try {
            Thread.sleep(2000);
            Logger.d("test runner");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
