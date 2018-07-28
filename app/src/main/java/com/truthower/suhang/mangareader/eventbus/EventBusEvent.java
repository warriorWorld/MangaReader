package com.truthower.suhang.mangareader.eventbus;/**
 * Created by Administrator on 2016/11/1.
 */

/**
 * 作者：苏航 on 2016/11/1 14:01
 * 邮箱：772192594@qq.com
 */
public class EventBusEvent {
    public static final int DOWNLOAD_PAGE_FINISH_EVENT = 16;
    public static final int DOWNLOAD_FINISH_EVENT = 17;
    public static final int DOWNLOAD_CHAPTER_FINISH_EVENT = 18;
    public static final int DOWNLOAD_CHAPTER_START_EVENT = 21;
    public static final int TAG_CLICK_EVENT = 19;
    public static final int JUMP_EVENT = 20;
    public static final int COPY_BOARD_EVENT=21;

    private String msg;
    private int intMsg;
    private int eventType;

    public EventBusEvent(int eventType) {
        this.eventType = eventType;
    }

    public EventBusEvent(String msg) {
        this.msg = msg;
    }

    public EventBusEvent(String msg, int eventType) {
        this.msg = msg;
        this.eventType = eventType;
    }

    public EventBusEvent(String msg, int intMsg, int eventType) {
        this.msg = msg;
        this.intMsg = intMsg;
        this.eventType = eventType;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getEventType() {
        return eventType;
    }

    public void setEventType(int eventType) {
        this.eventType = eventType;
    }

    public int getIntMsg() {
        return intMsg;
    }

    public void setIntMsg(int intMsg) {
        this.intMsg = intMsg;
    }
}
