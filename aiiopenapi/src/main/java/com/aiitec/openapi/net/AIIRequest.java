package com.aiitec.openapi.net;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.SparseArray;

import com.aiitec.openapi.cache.AIIPacketCacheManager;
import com.aiitec.openapi.constant.AIIConstant;
import com.aiitec.openapi.constant.CommonKey;
import com.aiitec.openapi.enums.CacheMode;
import com.aiitec.openapi.enums.VerifyType;
import com.aiitec.openapi.json.JSON;
import com.aiitec.openapi.json.enums.AIIAction;
import com.aiitec.openapi.json.enums.CombinationType;
import com.aiitec.openapi.model.NoSessionRequest;
import com.aiitec.openapi.model.RequestQuery;
import com.aiitec.openapi.model.ResponseQuery;
import com.aiitec.openapi.model.SessionRequestQuery;
import com.aiitec.openapi.net.AIIRequestCallBack.NoSessionListener;
import com.aiitec.openapi.net.ProgressResponseBody.ProgressListener;
import com.aiitec.openapi.packet.DefaultRequest;
import com.aiitec.openapi.packet.Request;
import com.aiitec.openapi.utils.AiiUtil;
import com.aiitec.openapi.utils.LogUtil;
import com.aiitec.openapi.utils.PacketUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.FormBody.Builder;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AIIRequest {

    public final static int CONNECT_TIMEOUT = 10;
    public final static int READ_TIMEOUT = 30;
    public final static int WRITE_TIMEOUT = 30;
    Handler hanlder = new Handler(Looper.getMainLooper());
    protected String url;
    private Context context;

    private AIIAction sessionAction = AIIAction.ONE;

    public void setSessionAction(AIIAction sessionAction) {
        this.sessionAction = sessionAction;
    }


    /**
     * 待请求协议列表
     */
    private static List<NoSessionRequest<?>> noRequestDatas = new ArrayList<NoSessionRequest<?>>();
    /**
     * 请求控制列表， 可以在退出Activity或程序把没有请求完成的请求都cancel掉
     */
    private ArrayList<Call> cancelables = new ArrayList<Call>();

    public static final String KEY_JSON = "json";

    private OkHttpClient client;
    /**
     * 存储请求列表的Map，为了防止操作太快，请求成功后会等1秒再删除内容，失败则立即删除
     */
    public static SparseArray<String> requestSparse = new SparseArray<String>();

    /**
     * 请求太快限制
     */
    public static boolean requestTooFastRestricted = true;

    public void setUrl(String url) {
        this.url = url;
    }

    public AIIRequest(Context context) {
        this(context, null);
    }

    public AIIRequest(Context context, String url) {
        this.context = context;
        this.url = url;
        AIIConstant.FILTER_ACTION_LOGIN = context.getPackageName() + ".login";
        AIIConstant.FILTER_ACTION_LOGIN_ON_OTHER = context.getPackageName() + ".login_on_other";

        client = new OkHttpClient.Builder().readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)// 设置读取超时时间
                .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)// 设置写的超时时间
                .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)// 设置连接超时时间
                .build();
    }

    /**
     * 取消所有协议请求
     */
    public void cancelHttpRequest() {
        for (Call cancelable : cancelables) {
            if (cancelable != null && !cancelable.isCanceled()) {
                cancelable.cancel();
            }
        }
    }

    /**
     * 请求session协议
     *
     * @param <T>
     * @param context
     * @param aiiResponse 回调
     * @param index       请求回调指引
     */
    public <T> Call requestSession(Context context, AIIResponse<T> aiiResponse, int index) {
        SessionRequestQuery query = new SessionRequestQuery(context, sessionAction);
//        query.setNamespace("Session");
//        if (query.getDeviceType() == 32) {
//            index = -1;
//        }
        if (query.getDeviceType() == 0) {
            index = -1;
        }
        return send(query, aiiResponse, JSON.combinationType, index);
    }

    /**
     * GET 请求
     *
     * @param query       请求参数 ，只识别query层的数据，其它数据一律不管， 也就是session 也不管， query下的对象页不管
     * @param aiiResponse 回调
     * @return
     */
    public <T> Call get(RequestQuery query, final AIIResponse<T> aiiResponse) {
        return get(query, aiiResponse, null, JSON.combinationType);
    }

    public <T> Call get(RequestQuery query, final AIIResponse<T> aiiResponse, CombinationType combinationType) {
        return get(query, aiiResponse, null, combinationType);
    }

    public <T> Call get(RequestQuery query, final AIIResponse<T> aiiResponse, List<String> childClassNames) {
        return get(query, aiiResponse, childClassNames, 0);
    }

    public <T> Call get(RequestQuery query, final AIIResponse<T> aiiResponse, List<String> childClassNames, CombinationType combinationType) {
        return get(query, aiiResponse, childClassNames, combinationType, 0);
    }

    /**
     * GET 请求
     *
     * @param query       请求参数
     * @param aiiResponse 回调
     * @param index       回调 区分索引
     * @return
     */
    public <T> Call get(RequestQuery query, final AIIResponse<T> aiiResponse, final int index) {
        return get(query, aiiResponse, null, JSON.combinationType, index);
    }

    public <T> Call get(RequestQuery query, final AIIResponse<T> aiiResponse, CombinationType combinationType, final int index) {
        return get(query, aiiResponse, null, combinationType, index);
    }

    /**
     * GET 请求
     *
     * @param query           请求参数
     * @param aiiResponse     回调
     * @param childClassNames 拼接的子类， 默认子类只接受table和where, 其他类也需要拼接则在此参数中添加，
     *                        写类名即可如goods,不使用全名，不区分大小写，
     * @param index           回调 区分索引
     * @return
     */
    public <T> Call get(RequestQuery query, final AIIResponse<T> aiiResponse, List<String> childClassNames, final int index) {
        return get(query, aiiResponse, childClassNames, JSON.combinationType, index);
    }

    public <T> Call get(RequestQuery query, final AIIResponse<T> aiiResponse, List<String> childClassNames, CombinationType combinationType, final int index) {
        okhttp3.Request.Builder requestBuilder = new okhttp3.Request.Builder();
        String newUrl = HttpUtils.combinationGet(url, query, childClassNames);
        if (query.isGzip()) {
            requestBuilder.header("Accept-Encoding", "gzip");
        }
        final Call call = client.newCall(requestBuilder.url(newUrl).build());
        startRequest(aiiResponse, call, CacheMode.NONE, -1, null, combinationType, index);
        cancelables.add(call);
        return call;
    }

    /**
     * POST 请求
     *
     * @param query
     * @param aiiResponse
     * @return
     */
    public <T> Call send(RequestQuery query, AIIResponse<T> aiiResponse) {
        return send(query, aiiResponse, JSON.combinationType, 0);
    }

    public <T> Call send(RequestQuery query, AIIResponse<T> aiiResponse, CombinationType combinationType) {
        return send(query, aiiResponse, combinationType, 0);
    }

    public <T> Call send(RequestQuery query, AIIResponse<T> aiiResponse, int index) {
        return send(query, aiiResponse, JSON.combinationType, index);
    }

    /***
     * POST 请求
     * @param query
     * @param aiiResponse
     * @param index
     * @return
     */
    public <T> Call send(RequestQuery query, AIIResponse<T> aiiResponse, CombinationType combinationType, int index) {

        if (query == null) {
            return null;
        }

        boolean noSession = false;
        String namespace = query.getNamespace();
        if (TextUtils.isEmpty(namespace)) {
            String className = query.getClass().getSimpleName();
            if (className.length() > 12) {
                namespace = className.substring(0, className.length() - 12);
            }
            query.setNamespace(namespace);
        }

        // 为了开始能快速访问和缓存， 这几个协议可以不要session
        if (!namespace.equalsIgnoreCase("Session")) {
            noSession = TextUtils.isEmpty(PacketUtil.session_id);
        }
        if (noSession) {// 没有session的协议就用noRequestDatas保存起来，等有session再请求
            NoSessionRequest<T> noSessionRequest = new NoSessionRequest<T>();
            noSessionRequest.setQuery(query);
            noSessionRequest.setAiiResponse(aiiResponse);
            noSessionRequest.setIndex(index);
            noSessionRequest.setCombinationType(combinationType);
            noRequestDatas.add(noSessionRequest);
            LogUtil.w("noSession");
            if (context != null) {
                LogUtil.d("requestSession");
                requestSession(context, new AIIResponse<ResponseQuery>(context, false) {
                    @Override
                    public void onSuccess(ResponseQuery response, int index) {
                        super.onSuccess(response, index);
                        LogUtil.d("requestSession--onSuccess");
                        requestOldData();
                    }
                }, index);
            }
            aiiResponse.onFinish(index);
            return null;

        }
        return send(query, null, aiiResponse, combinationType, index);
    }

    public <T> Call sendOthers(String url, LinkedHashMap<String, String> stringQuerys,
                               final AIIResponse<T> aiiResponse, final int index) {
        return sendOthers(url, stringQuerys, aiiResponse, JSON.combinationType, index);
    }

    /**
     * 非正常协议的请求，就是不用管session，登录，传参数与我们公司标准格式不一致的请求，比如检查版本更新
     *
     * @param <T>
     * @param url          请求Url
     * @param stringQuerys 请求参数
     * @param aiiResponse  回调
     * @param index        标记
     * @return 请求HttpHandler
     */
    public <T> Call sendOthers(String url, LinkedHashMap<String, String> stringQuerys,
                               final AIIResponse<T> aiiResponse, CombinationType combinationType, final int index) {
        StringBuffer sb = new StringBuffer();
        sb.append(url).append("?");
        Builder formBody = new FormBody.Builder();

        if (stringQuerys != null) {
            Iterator<Entry<String, String>> it = stringQuerys.entrySet().iterator();
            while (it.hasNext()) {
                Entry<String, String> entry = (Entry<String, String>) it.next();
                String key = entry.getKey();
                String value = entry.getValue();
                formBody.add(key, value);
                sb.append(key + "=" + value + "&");
            }
            if (sb.toString().endsWith("&")) {
                sb.deleteCharAt(sb.length() - 1);
            }
        }
        LogUtil.w(sb.toString());
        final int sparseKey = requestSparse.size();// key不能重复，所以要等于requestSparse.size()以上

        RequestBody requestBody = formBody.build();
        okhttp3.Request okRequest = new okhttp3.Request.Builder()
                .url(url).post(requestBody).build();
        final Call call = client.newCall(okRequest);
        AIIRequestCallBack<T> aiiRequestCallBack = new AIIRequestCallBack<T>(context, index, sparseKey);
        aiiRequestCallBack.setNoSessionListener(new MNoSessionListener());
        aiiRequestCallBack.setAiiResponse(aiiResponse);
        call.enqueue(aiiRequestCallBack);
        cancelables.add(call);
        return call;
    }

    public Call download(String url, final File destFile, final ProgressListener progressListener) {

        LogUtil.w(url);
        OkHttpClient client = new OkHttpClient.Builder().readTimeout(READ_TIMEOUT * 4, TimeUnit.SECONDS)// 设置读取超时时间
                .writeTimeout(WRITE_TIMEOUT * 4, TimeUnit.SECONDS)// 设置写的超时时间
                .connectTimeout(CONNECT_TIMEOUT * 2, TimeUnit.SECONDS)// 设置连接超时时间
                .addNetworkInterceptor(new Interceptor() {

                    public Response intercept(Chain chain) throws IOException {
                        Response originalResponse = chain.proceed(chain.request());
                        return originalResponse.newBuilder()
                                .body(new ProgressResponseBody(originalResponse.body(), progressListener)).build();
                    }
                }).build();
        okhttp3.Request okRequest = new okhttp3.Request.Builder().url(url).build();

        final Call call = client.newCall(okRequest);
        if (Looper.getMainLooper().getThread() == Thread.currentThread()) {
            progressListener.onStart();
        } else {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    progressListener.onStart();
                }
            });
        }
        call.enqueue(new AIIDownloadCallBack(destFile, progressListener));
        cancelables.add(call);
        return call;
    }

    public <T> Call sendFiles(LinkedHashMap<String, Object> files, AIIResponse<T> aiiResponse) {
        return sendFiles(files, aiiResponse, 0);
    }

    public <T> Call sendFiles(LinkedHashMap<String, Object> files, AIIResponse<T> aiiResponse, CombinationType combinationType) {
        return sendFiles(files, aiiResponse, combinationType, 0);
    }

    public <T> Call sendFiles(LinkedHashMap<String, Object> files, AIIResponse<T> aiiResponse, int index) {
        return sendFiles(files, aiiResponse, JSON.combinationType, index);
    }

    public <T> Call sendFiles(LinkedHashMap<String, Object> files, AIIResponse<T> aiiResponse, CombinationType combinationType, int index) {
        RequestQuery request = new RequestQuery();
        request.setNamespace("UploadFiles");
        return sendFiles(request, files, aiiResponse, combinationType, index);
    }

    public <T> Call sendFiles(RequestQuery query, LinkedHashMap<String, Object> files, AIIResponse<T> aiiResponse) {
        return sendFiles(query, files, aiiResponse, 0);
    }

    public <T> Call sendFiles(RequestQuery query, LinkedHashMap<String, Object> files, AIIResponse<T> aiiResponse, CombinationType combinationType) {
        return sendFiles(query, files, aiiResponse, combinationType, 0);
    }

    public <T> Call sendFiles(RequestQuery query, LinkedHashMap<String, Object> files, AIIResponse<T> aiiResponse, int index) {
        return sendFiles(query, files, aiiResponse, JSON.combinationType, index);
    }

    public <T> Call sendFiles(RequestQuery query, LinkedHashMap<String, Object> files, AIIResponse<T> aiiResponse,
                              CombinationType combinationType, int index) {
        return send(query, HttpUtils.combinationFileParems(url, files), aiiResponse, combinationType, index);
    }

    public <T> Call sendFileForCos(String url, File file, AIIResponse<T> aiiResponse) {
        return sendFileForCos(url, file, aiiResponse, 0);
    }

    public <T> Call sendFileForCos(String url, File file, AIIResponse<T> aiiResponse, int index) {
        return sendFileForCos(url, file, aiiResponse, null, index);
    }

    public <T> Call sendFileForCos(String url, File file, AIIResponse<T> aiiResponse,
                                   CombinationType combinationType, int index) {
        return sendCosFile(url, file, aiiResponse, combinationType, index);
    }

//    public <T> Call sendFiles(String url, RequestQuery query, Map<String, Object> files, AIIResponse<T> aiiResponse,
//            int index) {
//        return send(query, HttpUtils.combinationFileParems(context, url, files), aiiResponse, index);
//    }

    public <T> Call get(String path, HashMap<String, String> params,
                        final AIIResponse<T> aiiResponse) {
        return get(path, params, aiiResponse, 0);
    }

    public <T> Call get(String path, HashMap<String, String> params,
                        final AIIResponse<T> aiiResponse, CombinationType combinationType) {
        return get(path, params, aiiResponse, combinationType, 0);
    }

    public <T> Call get(String path, HashMap<String, String> params,
                        final AIIResponse<T> aiiResponse, final int index) {
        return get(url, path, params, aiiResponse, index);
    }

    public <T> Call get(String path, HashMap<String, String> params,
                        final AIIResponse<T> aiiResponse, CombinationType combinationType, final int index) {
        return get(url, path, params, aiiResponse, combinationType, index);
    }

    public <T> Call get(String path, HashMap<String, String> params, VerifyType verifyType,
                        final AIIResponse<T> aiiResponse) {
        return get(path, params, verifyType, aiiResponse, 0);
    }

    public <T> Call get(String path, HashMap<String, String> params, VerifyType verifyType,
                        final AIIResponse<T> aiiResponse, CombinationType combinationType) {
        return get(path, params, verifyType, aiiResponse, combinationType, 0);
    }

    public <T> Call get(String path, HashMap<String, String> params, VerifyType verifyType,
                        final AIIResponse<T> aiiResponse, final int index) {
        return get(url, path, params, verifyType, aiiResponse, index);
    }

    public <T> Call get(String path, HashMap<String, String> params, VerifyType verifyType,
                        final AIIResponse<T> aiiResponse, CombinationType combinationType, final int index) {
        return get(url, path, params, verifyType, aiiResponse, combinationType, index);
    }

    public <T> Call get(String url, String path, HashMap<String, String> params,
                        final AIIResponse<T> aiiResponse, final int index) {
        return get(url, path, params, VerifyType.MILLISECOND, aiiResponse, index);
    }

    public <T> Call get(String url, String path, HashMap<String, String> params,
                        final AIIResponse<T> aiiResponse, CombinationType combinationType, final int index) {
        return get(url, path, params, VerifyType.MILLISECOND, aiiResponse, combinationType, index);
    }

    public <T> Call get(String url, String path, HashMap<String, String> params, VerifyType verifyType,
                        final AIIResponse<T> aiiResponse, final int index) {
        return get(url, path, params, verifyType, aiiResponse, JSON.combinationType, index);
    }

    public <T> Call get(String url, String path, HashMap<String, String> params, VerifyType verifyType, CombinationType combinationType,
                        final AIIResponse<T> aiiResponse) {
        return get(url, path, params, verifyType, aiiResponse, combinationType, 0);
    }

    public <T> Call get(String url, String path, HashMap<String, String> params, VerifyType verifyType,
                        final AIIResponse<T> aiiResponse, CombinationType combinationType, final int index) {
        okhttp3.Request.Builder requestBuilder = new okhttp3.Request.Builder();
        String newUrl = HttpUtils.combinationGet(url, path, params, verifyType);
        final Call call = client.newCall(requestBuilder.url(newUrl).build());
        requestBuilder.header("Accept-Encoding", "gzip");
        startRequest(aiiResponse, call, null, -1, null, combinationType, index);
        cancelables.add(call);
        return call;
    }


    /**
     * 把没有session的数据重新请求一遍
     *
     * @param <T>
     */
    private <T> void requestOldData() {
        for (int i = 0; i < noRequestDatas.size(); i++) {

            @SuppressWarnings("unchecked")
            NoSessionRequest<T> noSessionRequest = (NoSessionRequest<T>) noRequestDatas.get(i);

            RequestQuery query = noSessionRequest.getQuery();
            if (query == null) {
                continue;
            }
            AIIResponse<T> aiiResponse = noSessionRequest.getAiiResponse();
            int nIndex = (Integer) noSessionRequest.getIndex();
            send(query, null, aiiResponse, noSessionRequest.getCombinationType(), nIndex);
        }
        noRequestDatas.clear();

    }

    private <T> Call sendCosFile(final String urlStr, final File file, final AIIResponse<T> aiiResponse, CombinationType combinationType, final int index) {
//        okhttp3.Request.Builder requestBuilder = new okhttp3.Request.Builder();
//        okhttp3.Request request = requestBuilder.url(urlStr).post(body).build();
//        final Call call = client.newCall(request);
//        // key不能重复，所以要等于requestSparse.size()以上
//        final int sparseKey = requestSparse.size();
//        startRequest(aiiResponse, call, null, sparseKey, null, combinationType, index);
//        cancelables.add(call);

        HttpUtils.uploadForCosFile(context, urlStr, file, aiiResponse, index);

//
//        okhttp3.Request.Builder requestBuilder = new okhttp3.Request.Builder();
//        okhttp3.Request request = requestBuilder.url(urlStr).post(new CosFileRequestBody(file)).build();
//        final Call call = client.newCall(request);
//        // key不能重复，所以要等于requestSparse.size()以上
//        final int sparseKey = requestSparse.size();
//        startRequest(aiiResponse, call, null, sparseKey, null, combinationType, index);
//        cancelables.add(call);
        return null;
    }

    @SuppressWarnings("unchecked")
    private <T> Call send(final RequestQuery query, MultipartBody.Builder multipart, final AIIResponse<T> aiiResponse, CombinationType combinationType,
                          final int index) {
        if (combinationType == null) {
            combinationType = JSON.combinationType;
        }
        String cacheKey = "";
        AIIPacketCacheManager aiiPacketCacheManager = new AIIPacketCacheManager(context);
        if (query.getCacheMode() != null && query.getCacheMode() != CacheMode.NONE) {
            cacheKey = aiiPacketCacheManager.getCacheKey(query);
        }

        String timeStamp = AiiUtil.getString(context, cacheKey + "timestamp");
        Request request2 = new DefaultRequest();
        if (query.getCacheMode() != null && query.getCacheMode() != CacheMode.NONE && !TextUtils.isEmpty(timeStamp)) {
            request2.setTimestampLatest(timeStamp);
        } else {
            request2.setTimestampLatest(null);
        }

        String requestJson = RequestJson.init(query, sessionAction);
        okhttp3.Request.Builder requestBuilder = new okhttp3.Request.Builder();
        String url = "";
        if (!TextUtils.isEmpty(query.dir)) {
            url = this.url + "/" + query.getDir() + "/" + query.getNamespace();
        } else {
            url = this.url + "/" + query.getNamespace();
        }

        requestBuilder.url(url);
        MediaType mediaType = MediaType.parse("application/json");
        if (!TextUtils.isEmpty(requestJson)) {
            if (multipart != null) {
//                RequestBody jsonRequestBody = RequestBody.create(mediaType, requestJson);
                multipart.addFormDataPart(KEY_JSON, requestJson);
//                multipart.addPart(jsonRequestBody);
                requestBuilder.post(multipart.build());
            } else {
                //form 表单
                FormBody body = new Builder().add(KEY_JSON, requestJson).build();
                //raw post
//                RequestBody.create(mediaType, requestJson);
                requestBuilder.post(body).build();
            }

        }

        final int sparseKey = requestSparse.size();// key不能重复，所以要等于requestSparse.size()以上
        if (requestTooFastRestricted) {
            boolean isSame = false;
            for (int i = 0; i < requestSparse.size(); i++) {
                if (requestSparse.get(requestSparse.keyAt(i)).equalsIgnoreCase(requestJson)) {
                    isSame = true;// 完全相同的请求，过滤掉
                    break;
                }
            }
            if (!isSame) {
                requestSparse.append(sparseKey, requestJson);

            } else if (aiiResponse != null) {// 操作太快
                if (context != null && context instanceof FragmentActivity) {
                    if (((FragmentActivity) context).getSupportFragmentManager().isDestroyed()) {
                        return null;//页面已销毁的话，就不需要调用回调接口了
                    }
                }
                aiiResponse.onOptionFast(0, index);
                // 操作太快就获取不到数据，那么久给他缓存
                readCache(aiiPacketCacheManager, aiiResponse, cacheKey, index, combinationType);
                aiiResponse.onFinish(index);
                return null;
            }

        }
        LogUtil.w(url);
        LogUtil.w(requestJson);

        // Details协议请求时可以马上获取缓存数据，因为Details协议一般服务端不设计缓存机制，但是客户端需要有缓存
        if (query.getCacheMode() != null && (query.getCacheMode() == CacheMode.PRIORITY_OFTEN)) {
            readCache(aiiPacketCacheManager, aiiResponse, cacheKey, index, combinationType);
        }

        final Call call = client.newCall(requestBuilder.build());
        if (query.isGzip()) {
            requestBuilder.header("Accept-Encoding", "gzip");
        }
        startRequest(aiiResponse, call, query.getCacheMode(), sparseKey, cacheKey, combinationType, index);

        cancelables.add(call);

        return call;
    }

    private <T> void readCache(AIIPacketCacheManager aiiPacketCacheManager, AIIResponse<T> aiiResponse, String cacheKey, int index, CombinationType combinationType) {
        if (aiiResponse != null && context != null && cacheKey != null) {
            String cacheContent = aiiPacketCacheManager.get(cacheKey);
            if (!TextUtils.isEmpty(cacheContent)) {
                Class<?> clazz = aiiResponse.get().getClass();
                if (clazz.equals(String.class)) {
                    aiiResponse.onCache((T) cacheContent, index);
                } else {
                    if (ResponseQuery.class.isAssignableFrom(clazz)) {
                        try {
                            JSONObject obj = new JSONObject(cacheContent);
                            cacheContent = obj.optString("q");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    Object cache = JSON.parseObject(cacheContent, clazz, combinationType);
                    if (cache != null) {
                        aiiResponse.onCache((T) cache, index);
                    }
                }
            }
        }

    }

    private <T> void startRequest(final AIIResponse<T> aiiResponse, final Call call, final CacheMode cacheMode, final int sparseKey, final String cacheKey, CombinationType combinationType, final int index) {
        AIIRequestCallBack<T> aiiRequestCallBack = new AIIRequestCallBack<T>(context, index, sparseKey);
        aiiRequestCallBack.setNoSessionListener(new MNoSessionListener());
        aiiRequestCallBack.setAiiResponse(aiiResponse);
        aiiRequestCallBack.setCacheKey(cacheKey);
        aiiRequestCallBack.setCacheModel(cacheMode);
        if (combinationType != null) {
            aiiRequestCallBack.setCombinationType(combinationType);
        }
        call.enqueue(aiiRequestCallBack);
    }


    /**
     * 没有session回调
     *
     * @author Administrator
     */
    class MNoSessionListener implements NoSessionListener {

        @Override
        public void onNosession(int index) {
            requestSession(context, new AIIResponse<ResponseQuery>(context, false) {
                @Override
                public void onSuccess(ResponseQuery response, int index) {
                    super.onSuccess(response, index);
                    // 请求完session,把没有session的协议也都请求掉
                    requestOldData();
                }
            }, index);
        }
    }

    public void checkVersion() {
        // 如果版本更新了，则重新请求session，不再读取之前保存的sessionId
        String lastVersion = AiiUtil.getString(context, CommonKey.KEY_VERSION);
        String currentVersion = PacketUtil.getVersionName(context);
        if (lastVersion != null) {// 如果是lastVersion == null
            // 反而会有很多地方发起session请求，所以lastVersion == null
            // 得情况就忽略了
            if (lastVersion.equals(currentVersion)) {// 版本与之前一致
                // 获取之前保存的sessionId
                PacketUtil.session_id = AiiUtil.getString(context, CommonKey.KEY_SESSION);
            } else {
                AiiUtil.putString(context, CommonKey.KEY_VERSION, currentVersion);
                requestSession(context, new AIIResponse<ResponseQuery>(context, false), 0);
            }
        }
    }

}
