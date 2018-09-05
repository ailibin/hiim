package com.aiitec.openapi.net;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import okhttp3.ResponseBody;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.aiitec.openapi.net.ProgressResponseBody.ProgressListener;

/**
 * 下载回调类
 * 
 * @author Anthony
 * @version 1.0
 * @createTime 2016-4-25
 */
public class AIIDownloadCallBack implements Callback {

    private File destFile;
    private ProgressListener progressListener;

    public AIIDownloadCallBack(File destFile, ProgressListener progressListener) {
        this.destFile = destFile;
        this.progressListener = progressListener;
    }

    /** 网络异常，请求失败，但是这些回调居然在子线程中，气死我了 */
    @Override
    public void onFailure(Call arg0, IOException arg1) {
        arg1.printStackTrace();

        handler.sendEmptyMessage(2);

    }

    /** 回调成功，但是这些回调居然在子线程中，气死我了 */
    @Override
    public void onResponse(Call arg0, Response arg1) throws IOException {
    	int code = arg1.code();
		if (code == 200) {
			save(destFile, arg1, 0);
	        handler.sendEmptyMessage(3);
	        Message msg = new Message();
	        msg.what = 1;
	        msg.obj = arg1;
	        handler.sendMessage(msg);
		} else {
			handler.sendEmptyMessage(2);
		}
        
    }

    public void onFailure() {
        if (progressListener != null) {
            progressListener.onFailure();
        }
    };

    public void onSuccess(Response response) {
        if (progressListener != null) {
            progressListener.onSuccess(destFile);
        }
    };

    private void save(File destFile, Response response, long startsPoint) {
        ResponseBody body = response.body();
        InputStream in = body.byteStream();
        FileChannel channelOut = null;
        // 随机访问文件，可以指定断点续传的起始位置
        RandomAccessFile randomAccessFile = null;
        try {
            randomAccessFile = new RandomAccessFile(destFile, "rwd");
            // Chanel
            // NIO中的用法，由于RandomAccessFile没有使用缓存策略，直接使用会使得下载速度变慢，亲测缓存下载3.3秒的文件，用普通的RandomAccessFile需要20多秒。
            channelOut = randomAccessFile.getChannel();
            // 内存映射，直接使用RandomAccessFile，是用其seek方法指定下载的起始位置，使用缓存下载，在这里指定下载位置。
            MappedByteBuffer mappedBuffer = channelOut.map(FileChannel.MapMode.READ_WRITE, startsPoint,
                    body.contentLength());
            byte[] buffer = new byte[1024];
            int len;
            while ((len = in.read(buffer)) != -1) {
                mappedBuffer.put(buffer, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
                if (channelOut != null) {
                    channelOut.close();
                }
                if (randomAccessFile != null) {
                    randomAccessFile.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    Handler handler = new Handler(Looper.getMainLooper(), new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == 1) {
                onSuccess((Response) msg.obj);
            } else if (msg.what == 2) {
                onFailure();
            }
            return false;
        }
    });
}
