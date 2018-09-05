package com.aiitec.openapi.model;

import com.aiitec.openapi.json.annotation.JSONField;


public class ResponseQuery extends Entity{

	@JSONField(name="t")
	protected String timestamp;
	
	@JSONField(name="s")
	protected int status;
	
	@JSONField(name="d")
	protected String desc;
	
	protected String expire;
	protected long id = -1;

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getExpire() {
		return expire;
	}

	public void setExpire(String expire) {
		this.expire = expire;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
}
