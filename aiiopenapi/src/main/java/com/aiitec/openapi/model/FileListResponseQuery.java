package com.aiitec.openapi.model;

import com.aiitec.openapi.json.annotation.JSONField;

import java.util.List;


public class FileListResponseQuery extends ListResponseQuery {

	@JSONField(entityName="file")
	private List<File> files;
	private List<Long> ids;

    public List<File> getFiles() {
        return files;
    }

    public void setFiles(List<File> files) {
        this.files = files;
    }

    public List<Long> getIds() {
        return ids;
    }

    public void setIds(List<Long> ids) {
        this.ids = ids;
    }



}
