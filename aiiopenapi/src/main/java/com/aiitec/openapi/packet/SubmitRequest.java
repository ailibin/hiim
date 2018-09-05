package com.aiitec.openapi.packet;

import com.aiitec.openapi.json.annotation.JSONField;
import com.aiitec.openapi.model.SubmitRequestQuery;

public class SubmitRequest extends Request {


	/**请求对象*/
	@JSONField(name="q") 
	protected SubmitRequestQuery query;

	public SubmitRequestQuery getQuery() {
		return query;
	}
	public void setQuery(SubmitRequestQuery query) {
		this.query = query;
	}


	public SubmitRequest() {
		query = new SubmitRequestQuery();
	}

}
