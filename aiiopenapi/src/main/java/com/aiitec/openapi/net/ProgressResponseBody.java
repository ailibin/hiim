package com.aiitec.openapi.net;

import java.io.File;
import java.io.IOException;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;

public class ProgressResponseBody extends ResponseBody {

    public interface ProgressListener {
        void onPreExecute(long contentLength);
        void update(long totalBytes);
        void onSuccess(File file);
        void onStart();
        void onFailure();
    }

	protected static final int PRE_EXECUTE = 0;
	protected static final int UPDATE = 1;

    private final ResponseBody responseBody;
    private final ProgressListener progressListener;
    private BufferedSource bufferedSource;

    public ProgressResponseBody(ResponseBody responseBody, ProgressListener progressListener) {
        this.responseBody = responseBody;
        this.progressListener = progressListener;
        handler.sendEmptyMessage(PRE_EXECUTE);
        
    }

    // @Override
    public MediaType contentType() {
        return responseBody.contentType();
    }

    // @Override
    public long contentLength() {
        return responseBody.contentLength();
    }

    // @Override
    public BufferedSource source() {
        if (bufferedSource == null) {
            bufferedSource = Okio.buffer(source(responseBody.source()));
        }
        return bufferedSource;
    }

    private Source source(Source source) {
        return new ForwardingSource(source) {
            long totalBytes = 0L;

            // @Override
            public long read(Buffer sink, long byteCount) throws IOException {
                long bytesRead = super.read(sink, byteCount);
                totalBytes += bytesRead != -1 ? bytesRead : 0;
                if (null != progressListener) {
                    if (bytesRead != -1) {
                    	Message message = handler.obtainMessage(UPDATE);
                    	message.obj = totalBytes;
                    	handler.sendMessage(message);
                    }
                }
                return bytesRead;
            }
        };
    }
    Handler handler = new Handler(Looper.getMainLooper(), new Handler.Callback() {
		
		@Override
		public boolean handleMessage(Message msg) {
			switch(msg.what){
			case UPDATE:
				long totalBytes = (long) msg.obj;
				if (progressListener != null) {
					progressListener.update(totalBytes);
				}
				
				break;
			case PRE_EXECUTE:
				if (progressListener != null) {
		            progressListener.onPreExecute(contentLength());
		        }
				break;
			}
			return false;
		}
	});
}