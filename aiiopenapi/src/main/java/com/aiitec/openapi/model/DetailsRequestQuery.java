package com.aiitec.openapi.model;

import com.aiitec.openapi.enums.CacheMode;
import com.aiitec.openapi.json.annotation.JSONField;
import com.aiitec.openapi.json.enums.AIIAction;



public class DetailsRequestQuery extends Entity {

    /**
     * action 组包使用a， 默认空
     */
    @JSONField(name = "a")
    protected AIIAction action = AIIAction.NULL;

    @JSONField(name = "cacheMode", notCombination = true)
    protected CacheMode cacheMode = CacheMode.PRIORITY_OFTEN;

    @JSONField(notCombination = true)
    public String namespace;

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


}
