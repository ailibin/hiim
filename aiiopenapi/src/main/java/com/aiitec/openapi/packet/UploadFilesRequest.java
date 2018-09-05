package com.aiitec.openapi.packet;

import com.aiitec.openapi.json.annotation.JSONField;
import com.aiitec.openapi.model.UploadFilesRequestQuery;


/**
 * 上传图片请求类
 * 
 * @author Anthony
 *
 */
public class UploadFilesRequest extends Request {

    @JSONField(name="q")
    private UploadFilesRequestQuery query = new UploadFilesRequestQuery();

    public UploadFilesRequestQuery getQuery() {
        return query;
    }

    public void setQuery(UploadFilesRequestQuery query) {
        this.query = query;
    }

    public UploadFilesRequest() {
    }

}
