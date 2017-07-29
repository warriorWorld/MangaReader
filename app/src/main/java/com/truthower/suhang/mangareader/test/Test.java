package com.truthower.suhang.mangareader.test;

import org.jsoup.Jsoup;

import java.io.IOException;

public class Test {
    private String[] spiderables = {"http://topwebcomics.com/","https://nhentai.net/","https://hitomi.la/"};
    private static org.jsoup.nodes.Document doc;

    public static void main(String[] args) {

        new Thread() {
            // 在java里并不一定需要开启线程 去请求网络,这里随意
            @Override
            public void run() {
                try {
                    doc = Jsoup.connect("www.baidu.com")
                            .timeout(10000).get();

                } catch (IOException e) {
                    e.printStackTrace();
                }

                System.out.println(doc.toString());
            }
        }.start();
    }
}
