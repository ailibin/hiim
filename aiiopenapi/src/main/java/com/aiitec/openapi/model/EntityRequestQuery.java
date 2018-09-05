package com.aiitec.openapi.model;

import com.aiitec.openapi.json.annotation.JSONField;


public class EntityRequestQuery extends RequestQuery {
	
	
	/**
	 * 用户对象
	 */
	@JSONField(name="entity")
	private Entity entity;

	public Entity getEntity() {
		return entity;
	}

	public void setEntity(Entity entity) {
		this.entity = entity;
	}


}
