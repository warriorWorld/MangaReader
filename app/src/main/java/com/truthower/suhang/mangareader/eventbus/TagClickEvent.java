package com.truthower.suhang.mangareader.eventbus;

/**
 * Created by Administrator on 2017/7/25.
 */

public class TagClickEvent extends EventBusEvent {
    private String selectTag;
    private String selectCode;

    public TagClickEvent(int eventType) {
        super(eventType);
    }

    public String getSelectTag() {
        return selectTag;
    }

    public void setSelectTag(String selectTag) {
        this.selectTag = selectTag;
    }

    public String getSelectCode() {
        return selectCode;
    }

    public void setSelectCode(String selectCode) {
        this.selectCode = selectCode;
    }
}
