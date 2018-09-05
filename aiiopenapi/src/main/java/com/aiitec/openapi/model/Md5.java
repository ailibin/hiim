package com.aiitec.openapi.model;

import com.aiitec.openapi.json.annotation.JSONField;

public class Md5 extends Entity {

    private String item;
    
    @JSONField(notCombination=true)
    private String key;

    
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }
}
