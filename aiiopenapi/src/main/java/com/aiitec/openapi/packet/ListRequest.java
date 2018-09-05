package com.aiitec.openapi.packet;

import com.aiitec.openapi.json.annotation.JSONField;
import com.aiitec.openapi.model.ListRequestQuery;
import com.aiitec.openapi.model.Table;

/**
 * 列表请求类 所以返回列表的请求类都继承该类
 * 
 * @author Anthony
 * 
 */
public class ListRequest extends Request {

    @JSONField(name = "q")
    protected ListRequestQuery query = new ListRequestQuery();

    public ListRequestQuery getQuery() {
        return query;
    }

    public void setQuery(ListRequestQuery query) {
        this.query = query;
    }

    public ListRequest() {
        super();
        Table ta = new Table();
        query.setTable(ta);
    }
}
