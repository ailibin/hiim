package com.aiitec.openapi.net;

import android.text.TextUtils;

import com.aiitec.openapi.json.enums.AIIAction;
import com.aiitec.openapi.model.RequestQuery;
import com.aiitec.openapi.packet.DefaultRequest;
import com.aiitec.openapi.packet.Request;
import com.aiitec.openapi.utils.PacketUtil;

public class RequestJson {

    public static String init(RequestQuery query, AIIAction action) {

        String namespace = query.getNamespace();

        if (TextUtils.isEmpty(namespace)) {
            namespace = query.getClass().getSimpleName();
            if (namespace.length() > 12) {
                namespace = namespace.substring(0, namespace.length() - 12);
            }
        }

        Request request = new DefaultRequest();
        if(query.isNeedSession()){
        	
        	if (PacketUtil.session_id != null) {
                request.setSession(PacketUtil.session_id);
            } else {
                request.setSession("");
            }
        	
        }
        
        request.setNamespace(namespace);
        request.setQuery(query);
        String requestData = request.toString();
        // 组包json
        if (Request.isOpenMd5()) {
            // json加密
//            request.setMd5(Encrypt.encrypt(requestData));
            request.setMd5("123");
            // 重组json
            requestData = request.toString();
        }
        return request.toString();

    }
}
