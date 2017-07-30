package com.truthower.suhang.mangareader.eventbus;

/**
 * Created by Administrator on 2017/7/25.
 */

public class JumpEvent extends EventBusEvent {
    private int jumpPoint;

    public JumpEvent(int eventType) {
        super(eventType);
    }

    public int getJumpPoint() {
        return jumpPoint;
    }

    public void setJumpPoint(int jumpPoint) {
        this.jumpPoint = jumpPoint;
    }
}
