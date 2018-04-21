package com.truthower.suhang.mangareader.bean;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Administrator on 2017/7/18.
 */

public class LeanBaseBean extends BaseBean {
    private String objectId;
    private Date create_at;

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public Date getCreate_at() {
        return create_at;
    }

    public void setCreate_at(Date create_at) {
        this.create_at = create_at;
    }
}
