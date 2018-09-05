package com.aiitec.openapi.net;


import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;

import com.aiitec.openapi.constant.AIIConstant;
import com.aiitec.openapi.enums.GetCombinationType;
import com.aiitec.openapi.enums.VerifyType;
import com.aiitec.openapi.json.CombinationUtil;
import com.aiitec.openapi.json.annotation.JSONField;
import com.aiitec.openapi.json.enums.AIIAction;
import com.aiitec.openapi.model.Entity;
import com.aiitec.openapi.model.RequestQuery;
import com.aiitec.openapi.utils.Encrypt;
import com.aiitec.openapi.utils.FileUtils;
import com.aiitec.openapi.utils.LogUtil;
import com.aiitec.openapi.utils.PacketUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class HttpUtils {

    public static GetCombinationType getCombinationType = GetCombinationType.NORMAL;

    public static final String combinationGet(String url, RequestQuery query) {
        return combinationGet(url, query, null);
    }

    public static final String combinationGet(String url, RequestQuery query, List<String> childClassNames) {
        String namespace = query.getNamespace();
        if (TextUtils.isEmpty(namespace)) {
            namespace = query.getClass().getSimpleName();
            if (namespace.length() > 12) {
                namespace = namespace.substring(0, namespace.length() - 12);
            }
        }
        StringBuilder sb = new StringBuilder();
        sb.append(url);
        if(!TextUtils.isEmpty(query.getDir())){
            if(!url.endsWith("/")){
                sb.append("/");
            }
            sb.append(query.getDir());
        }
        if(!TextUtils.isEmpty(query.namespace)){
            if(!url.endsWith("/")){
                sb.append("/");
            }
            sb.append(query.namespace);
        }
        sb.append("?");
        if (getCombinationType == GetCombinationType.JSON) {
            sb.append("json={").append("\"n\":\"").append(namespace).append("\",\"q\":").append(query.toString()).append("}");
        } else {
            sb.append("&");
            appendObjectValue(query, sb, childClassNames);
            if (sb.toString().endsWith("?") || sb.toString().endsWith("&")) {
                sb.deleteCharAt(sb.length() - 1);
            }
        }
        LogUtil.w(sb.toString());
        return sb.toString();
    }

    private static void appendObjectValue(Object obj, StringBuilder sb, List<String> childClassNames) {
        if (childClassNames == null) {
            childClassNames = new ArrayList<String>();
            childClassNames.add("table");
            childClassNames.add("where");
        }
        for (Field field : CombinationUtil.getAllFields(obj.getClass())) {
            field.setAccessible(true);
            Object value;
            JSONField jsonField = field.getAnnotation(JSONField.class);
            if (jsonField != null && jsonField.notCombination()) {
                continue;
            }
            try {
                value = field.get(obj);
                if (value != null && !String.valueOf(value).equals("-1")
                        && !String.valueOf(value).equals("-1.0")) {
                    // 如果是对象
                    if (Entity.class.isAssignableFrom(field.getType())) {
                        for (int i = 0; i < childClassNames.size(); i++) {
                            if (field.getName().equalsIgnoreCase(childClassNames.get(i))) {
                                appendObjectValue(value, sb, childClassNames);
                                break;
                            }
                        }
                    } else {
                        appendValue(field, value, sb);
                    }

                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }
    }

    private static void appendValue(Field field, Object value, StringBuilder sb) {
        JSONField jsonField = field.getAnnotation(JSONField.class);
        String fieldName = field.getName();
        if (jsonField != null && !TextUtils.isEmpty(jsonField.name())) {
            fieldName = jsonField.name();
        }
        sb.append(fieldName).append("=");
        if (PacketUtil.isCommonField(value.getClass())) {// 只读一级目录下的常用变量，其它的忽略
            sb.append(value);
        } else if (value.getClass() == AIIAction.class) {// 目前只有只一个枚举，先这样些，以后再改进
            AIIAction action = (AIIAction) value;
            sb.append(action.getValue());
        }
        sb.append("&");
    }

    public static final String combinationGet(String url, String path, HashMap<String, String> params, VerifyType verifyType) {
        if (params == null) {
            params = new HashMap<>();
        }


//		long time = System.currentTimeMillis();
//		if(verifyType == VerifyType.MILLISECOND){
//			params.put("aid", AIIConstant.AII_APP_ID);
//			params.put("t", String.valueOf(time));
//		} else if(verifyType == VerifyType.SECOND){
//			params.put("aid", AIIConstant.AII_APP_ID);
//			params.put("t", String.valueOf(time/1000));
//		}
        StringBuffer sb = new StringBuffer();
        sb.append(url);
        if (!TextUtils.isEmpty(path)) {
            sb.append(path);
        }
        if (params.size() > 0) {
            sb.append("?");
        }

        List<String> sortKeys = iteratorToSortList(params.keySet().iterator());
        for (String key : sortKeys) {
            sb.append(key).append("=").append(params.get(key)).append("&");
        }
        if (sb.toString().endsWith("&")) {
            sb.deleteCharAt(sb.length() - 1);
        }
        if (verifyType != VerifyType.NONE) {
            LogUtil.e(sb.toString());

            String value = sb.toString().replace("http://", "").replace("https://", "") + AIIConstant.AII_APP_KEY;
            LogUtil.e(value);
            String value2 = Encrypt.md5(value);
            LogUtil.e(value2);
            String encrypt = Encrypt.md5(value2);
            LogUtil.e(encrypt);
            String md5Value = encodeBase64(encrypt);

            LogUtil.e(md5Value);
            sb.append("&m=").append(md5Value);

        }


//		StringBuffer sb2 = new StringBuffer();
//		sb2.append(url).append(sb.toString());
        LogUtil.w(sb.toString());
        return sb.toString();
    }


    private static void sortKeys(List<String> params) {

        Collections.sort(params, new Comparator<String>() {

            @Override
            public int compare(String lhs, String rhs) {
                for (int i = 0; i < lhs.length(); i++) {
                    char ch1 = lhs.charAt(i);
                    if (rhs.length() > i) {
                        char ch2 = rhs.charAt(i);
                        if (ch1 == ch2) {//如果第一个字母相同，再比较下一个
                            continue;
                        } else {
                            return ch1 - ch2;
                        }

                    } else {
                        return 1;
                    }
                }
                //比较结果一致，再比较长度
                return lhs.length() - rhs.length();
            }
        });
    }

    public static List<String> iteratorToSortList(Iterator<String> iter) {
        List<String> copy = new ArrayList<String>();
        while (iter.hasNext()) {
            copy.add(iter.next());
        }
        sortKeys(copy);
        return copy;
    }

    // private static Headers getFileHeader(String key) {
    // return Headers.of("Content-Disposition",
    // "form-data; name=\"file[]\";filename=\"" + key + " \"");
    // }
    public static <T> MultipartBody.Builder combinationFileParems(String url, Map<String, Object> datas) {

        StringBuilder sb = new StringBuilder();
        sb.append(url).append("?");
        MultipartBody.Builder multipart = new MultipartBody.Builder();
        multipart.setType(MultipartBody.FORM);
        if (datas != null && !datas.isEmpty()) {
            Iterator<Entry<String, Object>> it = datas.entrySet().iterator();
            while (it.hasNext()) {
                Entry<String, Object> entry = (Entry<String, Object>) it.next();
//				MediaType mediaType = MediaType
//						.parse("application/octet-stream");
                MediaType mediaType = MediaType.parse("image/png");
                if (entry.getValue().getClass().equals(File.class)) {
                    File value = (File) entry.getValue();
                    LogUtil.w("length:" + value.length());
                    LogUtil.w("fileName:" + value.getName());

                    RequestBody fileBody = RequestBody.create(mediaType, value);
                    // multipart.addPart(getFileHeader(value.getName()),
                    // fileBody);
                    multipart.addFormDataPart("file[]", value.getName(),
                            fileBody);

                } else if (InputStream.class.isAssignableFrom(entry.getValue()
                        .getClass())) {
                    InputStream value = (InputStream) entry.getValue();
                    try {
                        byte[] data = PacketUtil.toByteArray(value);
                        LogUtil.w("length:" + data.length);
                        LogUtil.w("fileName:" + entry.getKey());
                        RequestBody fileBody = RequestBody.create(mediaType,
                                data);
                        // multipart.addPart(getFileHeader(entry.getKey()),
                        // fileBody);
                        multipart.addFormDataPart("file[]", entry.getKey(),
                                fileBody);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                } else if (entry.getValue().getClass().equals(byte[].class)) {
                    byte[] value = (byte[]) entry.getValue();
                    LogUtil.w("length:" + value.length);
                    LogUtil.w("fileName:" + entry.getKey());
                    RequestBody fileBody = RequestBody.create(mediaType, value);
                    // multipart.addPart(getFileHeader(entry.getKey()),
                    // fileBody);
                    multipart.addFormDataPart("file[]", entry.getKey(),
                            fileBody);

                } else if (entry.getValue().getClass().equals(String.class)) {
                    String value = (String) entry.getValue();
                    if (value != null) {
                        multipart.addFormDataPart(entry.getKey(), value);
                    }
                    sb.append(entry.getKey()).append("=").append(value)
                            .append("&");
                }

            }
        }
        if (sb.toString().endsWith("&")) {
            sb.deleteCharAt(sb.length() - 1);
        }

        if (!sb.toString().endsWith("?")) {
            LogUtil.w(sb.toString());
        }
        return multipart;
    }

    public static RequestBody combinationFileCosParems(File file) {
        String mineType = FileUtils.getMIMEType(file);
        LogUtil.w("mineType:" + mineType);
        MediaType mediaType = MediaType.parse(mineType);
        RequestBody fileBody = RequestBody.create(mediaType, file);
        return fileBody;
    }

    private static String encodeBase64(String value) {
        byte[] encode = android.util.Base64.encode(value.getBytes(), android.util.Base64.DEFAULT);
        return new String(encode);

    }

    public static void uploadForCosFile(final Context context, final String urlStr, final File file, final AIIResponse aiiResponse, final int index) {
        final Handler hanlder = new Handler(Looper.getMainLooper());
        if(aiiResponse != null){
            aiiResponse.onStart(index);
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                OutputStream out = null;
                FileInputStream inputStream = null;
                InputStream in = null;
                try {
                    URL url = new URL(urlStr);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setDoOutput(true);
                    connection.setDoInput(true);
//                    connection.setChunkedStreamingMode(0);
                    connection.setRequestMethod("PUT");
//                    connection.setRequestMethod("POST");

                    out = connection.getOutputStream();
                    inputStream = new FileInputStream(file);
                    byte[] buff = new byte[64 * 1024];
                    int length = 0;
                    int total = 0;
                    while ((length = inputStream.read(buff)) > 0) {
                        total += length;
                        out.write(buff, 0, length);
                    }
                    LogUtil.w("total  " + total+"   "+file.getAbsolutePath());
                    // 写入要上传的数据

                    final int responseCode = connection.getResponseCode();
                    LogUtil.w("Service returned response code " + responseCode);
                    Map<String, List<String>> map = connection.getHeaderFields();
                    for (Entry<String, List<String>> entry : map.entrySet()) {
                        LogUtil.print("Key : " + entry.getKey() +
                                " ,Value : " + entry.getValue());
                    }
                    String eTag = connection.getHeaderField("ETag");
                    LogUtil.print(eTag);
                    in = connection.getInputStream();

                    InputStreamReader reader = new InputStreamReader(in);
                    BufferedReader buffer = new BufferedReader(reader);
                    String inputLine = null;
                    StringBuffer sb = new StringBuffer();
                    while (((inputLine = buffer.readLine()) != null)){
                        sb.append(inputLine);
                    }
                    LogUtil.e("result:" + sb.toString());
                    hanlder.post(new Runnable() {
                        @Override
                        public void run() {
                            if (aiiResponse != null) {
                                if (context != null && context instanceof FragmentActivity) {
                                    if (((FragmentActivity) context).getSupportFragmentManager().isDestroyed()) {
                                        return;//页面已销毁的话，就不需要调用回调接口了
                                    }
                                }
                            }
                            if (responseCode == 200) {
                                aiiResponse.onSuccess("", index);
                                aiiResponse.onFinish(index);
                            } else {

                                aiiResponse.onFailure("网络异常", index);
                                aiiResponse.onFinish(index);
                            }
                        }
                    });

                } catch (ProtocolException e) {
                    e.printStackTrace();
                    LogUtil.e("httpUtils 上传文件 ProtocolException   "+e.getMessage());
                    if (aiiResponse != null) {
                        if (context != null && context instanceof FragmentActivity) {
                            if (((FragmentActivity) context).getSupportFragmentManager().isDestroyed()) {
                                return;//页面已销毁的话，就不需要调用回调接口了
                            }
                        }
                    }
                    hanlder.post(new Runnable() {
                        @Override
                        public void run() {
                            aiiResponse.onFailure("网络异常", index);
                            aiiResponse.onFinish(index);
                        }
                    });

                } catch (IOException e) {
                    e.printStackTrace();
                    LogUtil.e("httpUtils 上传文件 IOException   "+e.getMessage());
                    if (aiiResponse != null) {
                        if (context != null && context instanceof FragmentActivity) {
                            if (((FragmentActivity) context).getSupportFragmentManager().isDestroyed()) {
                                return;//页面已销毁的话，就不需要调用回调接口了
                            }
                        }
                    }
                    hanlder.post(new Runnable() {
                        @Override
                        public void run() {
                            aiiResponse.onFailure("网络异常", index);
                            aiiResponse.onFinish(index);
                        }
                    });

                } finally {
                    if (out != null) {
                        try {
                            out.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (inputStream != null) {
                        try {
                            inputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (in != null) {
                        try {
                            in.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }).start();
    }
}
