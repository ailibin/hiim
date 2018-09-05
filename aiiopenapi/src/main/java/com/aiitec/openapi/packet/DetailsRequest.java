package com.aiitec.openapi.packet;

import com.aiitec.openapi.enums.CacheMode;
import com.aiitec.openapi.json.annotation.JSONField;
import com.aiitec.openapi.model.RequestQuery;

public class DetailsRequest extends RequestQuery {
	
	
	@JSONField(notCombination=true)
	protected CacheMode cacheMode = CacheMode.PRIORITY_OFTEN;
	@Override
	public CacheMode getCacheMode() {
	    return cacheMode;
	}
	@Override
	public void setCacheMode(CacheMode cacheMode) {
	    this.cacheMode = cacheMode;
	}

}
