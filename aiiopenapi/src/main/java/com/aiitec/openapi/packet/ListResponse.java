package com.aiitec.openapi.packet;

import com.aiitec.openapi.json.annotation.JSONField;
import com.aiitec.openapi.model.ListResponseQuery;





/**
 * 列表返回基类
 * 所以列表返回的都继承该类
 * @author Anthony
 *
 */
public class ListResponse extends Response{
	
	@JSONField(name="q")
	ListResponseQuery query;

	public ListResponseQuery getQuery() {
		return query;
	}

	public void setQuery(ListResponseQuery query) {
		this.query = query;
	}


}
