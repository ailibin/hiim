package com.aiitec.openapi.model;

import com.aiitec.openapi.enums.CacheMode;
import com.aiitec.openapi.json.annotation.JSONField;

public class ListRequestQuery extends RequestQuery {

    @JSONField(notCombination = true)
    protected CacheMode cacheMode = CacheMode.PRIORITY_COMMON;
    @JSONField(name = "ta")
    private Table table;
    private int category = -1;
    protected int positionId = -1;
    protected int type = -1;

    @Override
    public CacheMode getCacheMode() {
        return cacheMode;
    }

    @Override
    public void setCacheMode(CacheMode cacheMode) {
        this.cacheMode = cacheMode;
    }

    public ListRequestQuery(String namespace) {
        super(namespace);
        table = new Table();
    }

    public ListRequestQuery() {
        table = new Table();
    }

    public Table getTable() {
        return table;
    }

    public void setTable(Table table) {
        this.table = table;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public int getPositionId() {
        return positionId;
    }

    public void setPositionId(int positionId) {
        this.positionId = positionId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
