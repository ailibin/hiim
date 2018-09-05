package com.aiitec.openapi.net;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.aiitec.openapi.utils.FileUtils;
import com.aiitec.openapi.utils.LogUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.BufferedSink;

/**
 * @author Anthony
 * @version 1.0
 *          createTime 2018/1/24.
 */

public class CosFileRequestBody extends RequestBody {

    File file;
    public CosFileRequestBody(@NonNull File file){
        this.file = file;
    }
    @Nullable
    @Override
    public MediaType contentType() {
        return MediaType.parse(FileUtils.getMIMEType(file));
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        FileInputStream inputStream =null;
        try {
            byte[] buff = new byte[512*1024];
            int length =0;
            int total = 0;
            inputStream = new FileInputStream(file);
            while((length= inputStream.read(buff))>0){
                LogUtil.e("length"+length);
                total+=length;
                sink.write(buff,0,length);
                sink.flush();
            }
            LogUtil.e("total"+total);
        } catch (IOException e){
            e.printStackTrace();
        } finally {
            if(inputStream != null){
                inputStream.close();
            }
        }
    }
}
