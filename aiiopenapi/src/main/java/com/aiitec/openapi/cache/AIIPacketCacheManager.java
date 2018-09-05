package com.aiitec.openapi.cache;

import android.content.Context;
import android.text.TextUtils;

import com.aiitec.openapi.constant.AIIConstant;
import com.aiitec.openapi.model.RequestQuery;
import com.aiitec.openapi.packet.DefaultRequest;
import com.aiitec.openapi.packet.Request;
import com.aiitec.openapi.utils.AiiUtil;
import com.aiitec.openapi.utils.PacketUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

/**
 * 协议缓存管理类
 * 
 * @author Anthony
 * @version 1.0
 * @createTime 2016-4-22
 */
public class AIIPacketCacheManager {

    // private AIIDBManager aiidbManager ;
    private AiiFileCache aiiFileCache;
    private String cacheJsonPacketPath;
    private Context context;

    public AIIPacketCacheManager(Context context) {
        this.context = context;
        // aiidbManager = AIIDBManager.getInstance(context);
        if (context.getExternalCacheDir() != null) {
            cacheJsonPacketPath = context.getExternalCacheDir().getAbsolutePath() + "/cache/";
        } else if (AiiUtil.isSDCardEnable()) {
            cacheJsonPacketPath = AiiUtil.getSDCardPath() + "/" + context.getPackageName() + "/cache/";
        } else {
            return;
        }
        if (AIIConstant.USER_ID > 0) {
            StringBuilder sb = new StringBuilder();
            sb.append(cacheJsonPacketPath);
            if (!cacheJsonPacketPath.endsWith("/")) {
                sb.append("/");
            }
            sb.append(AIIConstant.USER_ID).append("/");
            cacheJsonPacketPath = sb.toString();
        }
        File file = new File(cacheJsonPacketPath);
        if (!file.exists()) {
            file.mkdir();
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdir();
            }
        }
        File cacheDir = new File(cacheJsonPacketPath);
        if (!cacheDir.exists() && !cacheDir.mkdirs()) {
            return;
        }
        aiiFileCache = AiiFileCache.getInstance(context, cacheJsonPacketPath);

    }

    /**
     * 获取该请求参数加密后的key
     * 
     * @return
     */
    public String getCacheKey(RequestQuery query) {
        // Request request2 = (Request) request.clone();// 这句话意思相当于
        // request2是request的克隆
        Request request = new DefaultRequest();
        request.setTimestampLatest(null);
        request.setMd5(null);
        if (!TextUtils.isEmpty(PacketUtil.session_id)) {
            request.setSession(PacketUtil.session_id);
        }
        String namespace = query.getNamespace();
        if (TextUtils.isEmpty(namespace)) {
            namespace = query.getClass().getSimpleName();
            if (namespace.length() > 12) {
                namespace = namespace.substring(0, namespace.length() - 12);
            }
        }
        request.setQuery(query);
        request.setNamespace(namespace);

        String md5RemoveTimestempLasted = AiiUtil.md5(request.toString());
        return md5RemoveTimestempLasted;
    }

    /**
     * 把response 存进去， 我们一般都是存返回的数据，所以都是response
     * 
     * @param key
     * @return key
     */
    public String put(String key, String content) {
        if(aiiFileCache == null){
            return key;
        }
        String namespace = "";
        try {
			JSONObject jsonObject = new JSONObject(content);
			namespace = jsonObject.getString("n");
		} catch (JSONException e) {
			e.printStackTrace();
		}
        if(namespace == null) return null;
        
        StringBuilder sb = new StringBuilder();
        sb.append(namespace).append("_").append(key).append(".json");
       
//        LogUtil.e("putCache:"+key+"\n"+content);
        aiiFileCache.put(sb.toString(), content);
        return key;// 我需要这个key
    }

    /**
     * 通过key获取json内容
     * 
     * @param key
     * @return
     */
    public String get(String key) {
        if(aiiFileCache == null){
            return null;
        }
        if (key == null) {
            return null;
        }
      
        String namespace = AiiUtil.getString(context, key + "namespace");
        StringBuilder sb = new StringBuilder();
        sb.append(namespace).append("_").append(key).append(".json");
        String json = aiiFileCache.get(sb.toString());
//        LogUtil.e("getCache:"+key+"\n"+json);
        return json;
    }

    public void clear() {
        if(aiiFileCache == null){
            return ;
        }
        // aiidbManager.deleteAll(CacheModel.class);
        aiiFileCache.clear();
    }

//    public void clear(long userId) {
//        if(aiiFileCache == null){
//            return ;
//        }
//        // aiidbManager.delete(CacheModel.class, "userId=?", new
//        // String[]{String.valueOf(AIIConstant.USER_ID)});
//        aiiFileCache.clear();
//    }

}
