package com.aiitec.openapi.model;

import com.aiitec.openapi.enums.CacheMode;
import com.aiitec.openapi.json.annotation.JSONField;
import com.aiitec.openapi.json.enums.AIIAction;

public class RequestQuery extends Entity {

    public RequestQuery(String namespace) {
        this.namespace = namespace;
    }

    /**
     * action 组包使用a， 默认空
     */
    @JSONField(name = "a")
    protected AIIAction action = AIIAction.NULL;

    @JSONField(name = "cacheMode", notCombination = true)
    protected CacheMode cacheMode = CacheMode.NONE;

    @JSONField(notCombination = true)
    public String namespace;

    @JSONField(notCombination = true)
    public String dir;
    /**
     * 是否需要Gzip压缩 默认为true
     */
    @JSONField(notCombination = true)
    protected boolean isGzip = true;

    /**
     * 是否需要session 默认为true
     */
    @JSONField(notCombination = true)
    protected boolean isNeedSession = true;


    public boolean isNeedSession() {
        return isNeedSession;
    }

    public void setNeedSession(boolean isNeedSession) {
        this.isNeedSession = isNeedSession;
    }


    public boolean isGzip() {
        return isGzip;
    }

    public void setGzip(boolean isGzip) {
        this.isGzip = isGzip;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public AIIAction getAction() {
        return action;
    }

    public void setAction(AIIAction action) {
        this.action = action;
    }

    public CacheMode getCacheMode() {
        return cacheMode;
    }

    public void setCacheMode(CacheMode cacheMode) {
        this.cacheMode = cacheMode;
    }

    public String getDir() {
        return dir;
    }

    public void setDir(String dir) {
        this.dir = dir;
    }


    public RequestQuery() {
    }
}
