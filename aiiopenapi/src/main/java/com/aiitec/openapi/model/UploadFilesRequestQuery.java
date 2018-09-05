package com.aiitec.openapi.model;

import com.aiitec.openapi.json.annotation.JSONField;
import com.aiitec.openapi.json.enums.AIIAction;

import java.util.List;

public class UploadFilesRequestQuery extends RequestQuery {

    /**
     * action 组包使用a， 默认空
     */
    @JSONField(name = "a")
    protected AIIAction action = AIIAction.ONE;

    public AIIAction getAction() {
        return action;
    }

    public void setAction(AIIAction action) {
        this.action = action;
    }

    private List<Md5> md5s;

    public List<Md5> getMd5s() {
        return md5s;
    }

    public void setMd5s(List<Md5> md5s) {
        this.md5s = md5s;
    }

}
