package com.truthower.suhang.mangareader.eventbus;

/**
 * Created by Administrator on 2017/7/25.
 */

public class TagClickEvent extends EventBusEvent {
    private String selectTag;

    public TagClickEvent(int eventType) {
        super(eventType);
    }

    public String getSelectTag() {
        return selectTag;
    }

    public void setSelectTag(String selectTag) {
        this.selectTag = selectTag;
    }
}
