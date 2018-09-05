package com.aiitec.openapi.packet;

import com.aiitec.openapi.json.annotation.JSONField;
import com.aiitec.openapi.model.FileListResponseQuery;

/**
 * 上传图片返回类
 * 
 * @author Anthony
 * 
 */
public class UploadFilesResponse extends ListResponse {

	@JSONField(name = "q")
	FileListResponseQuery query;

	public FileListResponseQuery getQuery() {
		return query;
	}

	public void setQuery(FileListResponseQuery query) {
		this.query = query;
	}


}
