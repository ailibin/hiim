package com.aiitec.openapi.packet;

import com.aiitec.openapi.json.annotation.JSONField;
import com.aiitec.openapi.model.EntityRequestQuery;

/**
 * 实体请求类
 * 所有带实体请求的类都继承该类
 * @author Anthony
 *
 */
public class EntityRequest extends Request {
	
	@JSONField(name="q") 
	protected EntityRequestQuery query;
	
	public EntityRequestQuery getQuery() {
		return query;
	}

	public void setQuery(EntityRequestQuery query) {
		this.query = query;
	}

	public EntityRequest() {
		query = new EntityRequestQuery();
	}
}
