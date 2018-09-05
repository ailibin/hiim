package com.aiitec.entitylibary.response;

import com.aiitec.entitylibary.model.Result;
import com.aiitec.openapi.model.Entity;

import java.util.ArrayList;

@SuppressWarnings("serial")
public class VersionCheckResponse extends Entity {


    private ArrayList<Result> results;

    public ArrayList<Result> getResults() {
        return results;
    }

    public void setResults(ArrayList<Result> results) {
        this.results = results;
    }

}
