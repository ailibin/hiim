package com.aiitec.openapi.model;


import com.aiitec.openapi.json.enums.CombinationType;
import com.aiitec.openapi.net.AIIResponse;

public class NoSessionRequest<T> {

    private RequestQuery query;
    private AIIResponse<T> aiiResponse;
    private int index = -1;
    private CombinationType combinationType;

    public RequestQuery getQuery() {
        return query;
    }

    public void setQuery(RequestQuery query) {
        this.query = query;
    }

    public AIIResponse<T> getAiiResponse() {
        return aiiResponse;
    }

    public void setAiiResponse(AIIResponse<T> aiiResponse) {
        this.aiiResponse = aiiResponse;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

	public CombinationType getCombinationType() {
		return combinationType;
	}

	public void setCombinationType(CombinationType combinationType) {
		this.combinationType = combinationType;
	}


}
