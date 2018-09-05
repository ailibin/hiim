package com.aiitec.openapi.net;

import android.content.Context;

import com.aiitec.openapi.json.enums.AIIAction;
import com.aiitec.openapi.model.File;
import com.aiitec.openapi.model.Md5;
import com.aiitec.openapi.model.UploadFilesRequestQuery;
import com.aiitec.openapi.packet.UploadFilesResponse;
import com.aiitec.openapi.utils.FileMD5Utils;
import com.aiitec.openapi.utils.LogUtil;

import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

/**
 * 上传图片类，带秒传功能
 * 
 * @author Anthony
 * @version 1.0
 * @createTime 2016-8-15
 */
public class UploadFileUtils {
    /** 是否开启秒传功能 */
    private boolean isOpenFastUpload = true;
    private AIIRequest aiiRequest;
    private UploadFilesRequestQuery query;
    private Context context;

    public void setOpenFastUpload(boolean isOpenFastUpload) {
        this.isOpenFastUpload = isOpenFastUpload;
    }

    public UploadFileUtils(Context context, AIIRequest aiiRequest, UploadFilesRequestQuery query) {
        this.aiiRequest = aiiRequest;
        this.query = query;
        this.context = context;
    }

    public UploadFileUtils(Context context, AIIRequest aiiRequest) {
        this(context, aiiRequest, new UploadFilesRequestQuery());
    }

    public <T> void requestUpload(UploadFilesRequestQuery query, LinkedHashMap<String, Object> map,
                                  AIIResponse<T> aiiResponse, int index) {
        this.query = query;
        requestUpload(map, aiiResponse, index);
    }

    public <T> void requestUpload(final LinkedHashMap<String, Object> map, final AIIResponse<T> aiiResponse,
            final int index) {

        if (!isOpenFastUpload) {
            aiiRequest.sendFiles(map, aiiResponse, index);
            // 如果不开启秒传, 就不用那么麻烦了
            return;
        }

        final AIIAction oldAction = query.getAction();
        query.setAction(AIIAction.FOUR);
        Iterator<Entry<String, Object>> it = map.entrySet().iterator();
        final List<Md5> md5s = new ArrayList<Md5>();
        while (it.hasNext()) {
            Entry<String, Object> entry = (Entry<String, Object>) it.next();

            Md5 md5 = new Md5();
            if (entry.getValue().getClass().equals(java.io.File.class)) {
                java.io.File value = (java.io.File) entry.getValue();
                String md5Data = "";
                try {
                    md5Data = FileMD5Utils.getMD5(value);
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                md5.setItem(md5Data);
                md5.setKey(entry.getKey());
            } else if (InputStream.class.isAssignableFrom(entry.getValue().getClass())) {
                InputStream value = (InputStream) entry.getValue();
                String md5Data = "";
                try {
                    md5Data = FileMD5Utils.getMD5(value);
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                md5.setItem(md5Data);
                md5.setKey(entry.getKey());
            } else if (entry.getValue().getClass().equals(byte[].class)) {
                byte[] value = (byte[]) entry.getValue();
                String md5Data = "";
                try {
                    md5Data = FileMD5Utils.getMD5(value);
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                md5.setItem(md5Data);
                md5.setKey(entry.getKey());
            }
            md5s.add(md5);
        }
        query.setMd5s(md5s);

        // 请求协议，此次不带文件，只检查服务端有没有数据
        aiiRequest.send(query, new AIIResponse<UploadFilesResponse>(context) {
            @Override
            public void onServiceError(String content, int status, int index) {
                super.onServiceError(content, status, index);
                aiiResponse.onServiceError(content, status, index);
            }

            @SuppressWarnings("unchecked")
            @Override
            public void onSuccess(UploadFilesResponse response, int index) {
                super.onSuccess(response, index);

                List<File> files = response.getQuery().getFiles();
                List<File> exitedMd5files = new ArrayList<File>();
                if (files != null && files.size() > 0) {
                    for (File file : files) {
                        if (file.getId() > 0) {
                            exitedMd5files.add((File) file.clone());
                        }
                    }
                }
                if (exitedMd5files.size() > 0) {
                    for (File md5File : exitedMd5files) {
                        for (Md5 md5 : md5s) {
                            if (md5File.getMd5().equalsIgnoreCase(md5.getItem())) {
                                map.remove(md5.getKey());
                            }
                        }
                    }
                }
                if (map.size() == 0) {// 如果都存在了，就直接返回成功
                    aiiResponse.onSuccess((T) response, index);
                    LogUtil.i("秒传成功");
                } else {// 如果还有文件需要上传

                    query.setAction(oldAction);// action
                                               // 记得改回原来的action
                    requestUploadFile(exitedMd5files, map, aiiResponse, index);

                }

            }

            @Override
            public void onFailure(String content, int index) {
                super.onFailure(content, index);
                aiiResponse.onFailure(content, index);
            }
        }, index);


    }

    /**
     * 真正的上传图片
     * 
     * @param <T>
     * 
     * @param exitedMd5files
     *            服务端已存在的图片
     * @param map
     *            待上传的文件map
     * @param aiiResponse
     *            回调
     * @param index
     */
    private <T> void requestUploadFile(final List<File> exitedMd5files, final LinkedHashMap<String, Object> map,
            final AIIResponse<T> aiiResponse, final int index) {
        query.setMd5s(null);
        if (exitedMd5files.size() == 0) {// 如果服务器一张图都不存在，那么就直接用原来的回调
            aiiRequest.sendFiles(query, map, aiiResponse, index);

        } else {// 如果服务器有几张图，而其他图又没有，那么久需要部分上传，上传完还要把存在的id加在一起

            aiiRequest.sendFiles(query, map, new AIIResponse<UploadFilesResponse>(context) {
                @Override
                public void onServiceError(String content, int status, int index) {
                    super.onServiceError(content, status, index);
                }

                @SuppressWarnings("unchecked")
                @Override
                public void onSuccess(UploadFilesResponse response, int index) {
                    super.onSuccess(response, index);
                    List<File> files = response.getQuery().getFiles();
                    if (LogUtil.showLog) {
                        StringBuffer sb = new StringBuffer();
                        sb.append("部分文件上传成功:");
                        for (File file : files) {
                            sb.append(file.getPath()).append(",");
                        }
                        if (sb.toString().endsWith(",")) {
                            sb.deleteCharAt(sb.length() - 1);
                        }

                    }
                    List<Long> ids = response.getQuery().getIds();
                    try {
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    files.addAll(exitedMd5files);
                    for (File file : exitedMd5files) {
                        ids.add(file.getId());
                    }
                    try {
                        aiiResponse.onSuccess((T) response, index);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onFailure(String content, int index) {
                    super.onFailure(content, index);
                }
            }, index);

        }
    }
}
