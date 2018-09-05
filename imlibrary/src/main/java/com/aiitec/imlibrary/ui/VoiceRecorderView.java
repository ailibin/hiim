package com.aiitec.imlibrary.ui;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.RectF;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.aiitec.imlibrary.R;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

/**
 * 描述：#
 * 作者：XJ on 2016/11/2 17:11
 * 邮箱：iamxiejing@yeah.net
 */
public class VoiceRecorderView extends FrameLayout {

    private Context mContext;
    private EaseVoiceRecorder voiceRecorder;
    //    protected PowerManager.WakeLock wakeLock;
    private RectF mRect;
    private EaseVoiceRecorderCallback mEaseVoiceRecorderCallback;
    private static final String TAG = "VoiceRecorderView";

    protected Handler mVoiceHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(android.os.Message msg) {
            voice_circle.getLayoutParams().width = (int) (dip2px(getContext(), 80) * ((msg.what / 7.0f) + 1));
            voice_circle.getLayoutParams().height = (int) (dip2px(getContext(), 80) * ((msg.what / 7.0f) + 1));
        }
    };
    private ImageView voice_iv_icon;
    private TextView voice_tv_normal;
    private TextView voice_tv_recorderdate;
    private View voice_circle;
    private TextView voice_tv_topcancel;
    private TextView voice_tv_buttomcancel;
    private CountDownTimer mCountDownTimer;
    private static int screenWidth;
    private static int screenHeight;
    private static float density;

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     *
     * @param context
     * @param dpValue dp值
     * @return 像素值
     */
    public static int dip2px(Context context, float dpValue) {

        return (int) (dpValue * density + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     *
     * @param context
     * @param pxValue 像素值
     * @return dp值
     */
    public static int px2dip(Context context, float pxValue) {
        return (int) (pxValue / density + 0.5f);
    }


    public VoiceRecorderView(Context context) {
        super(context);
        init(context);
    }

    public VoiceRecorderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public VoiceRecorderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        BaseUtil.init(context);
        DisplayMetrics metric = new DisplayMetrics();
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        manager.getDefaultDisplay().getMetrics(metric);
        screenWidth = metric.widthPixels;
        screenHeight = metric.heightPixels;
        density = metric.density;
        mContext = getContext().getApplicationContext();
//        wakeLock = ((PowerManager) getContext().getSystemService(Context.POWER_SERVICE)).newWakeLock(
//                PowerManager.SCREEN_DIM_WAKE_LOCK, "demo");

        voiceRecorder = new EaseVoiceRecorder(mVoiceHandler);


        //解析正常的布局
        LayoutInflater.from(getContext()).inflate(R.layout.ui_layout_voicerecorderview_normal, this);

        voice_iv_icon = (ImageView) findViewById(R.id.voice_iv_icon);

        voice_tv_normal = (TextView) findViewById(R.id.voice_tv_normal);

        voice_tv_recorderdate = (TextView) findViewById(R.id.voice_tv_recorderdate);
        voice_tv_topcancel = (TextView) findViewById(R.id.voice_tv_topcancel);
        voice_tv_buttomcancel = (TextView) findViewById(R.id.voice_tv_buttomcancel);

        voice_circle = findViewById(R.id.voice_circle);
        mCountDownTimer = new CountDownTimer(60000, 100) {

            @Override
            public void onTick(long millisUntilFinished) {
                voice_tv_normal.setText(getMillon(millisUntilFinished, "mm:ss"));
                voice_tv_recorderdate.setText(getMillon(millisUntilFinished, "mm:ss"));
                Log.i(TAG, "开始计时" + getMillon(millisUntilFinished, "mm:ss"));
            }

            /**
             * 倒计时结束调用
             */
            @Override
            public void onFinish() {
                int length = stopRecoding();
                mEaseVoiceRecorderCallback.onVoiceRecordComplete(getVoiceFilePath(), length);
            }
        };

    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (!requestAudio((Activity) getContext())) {
            return super.onTouchEvent(event);
        }
        if (mRect == null) {
            mRect = new RectF(voice_iv_icon.getLeft(), voice_iv_icon.getTop(), voice_iv_icon.getRight(), voice_iv_icon.getBottom());
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                try {
//                    if (EaseChatRowVoicePlayClickListener.isPlaying)
//                        EaseChatRowVoicePlayClickListener.currentPlayListener.stopPlayVoice();
                    if (mRect.contains(event.getX(), event.getY())) {
                        startRecording();
                        mCountDownTimer.start();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
            case MotionEvent.ACTION_MOVE:
                if (voiceRecorder.isRecording()) {
                    if (!mRect.contains(event.getX(), event.getY())) {
                        showReleaseToCancelHint();
                    } else {
                        showMoveUpToCancelHint();
                    }
                }
                return true;
            case MotionEvent.ACTION_UP:
                Log.i("", "包含" + mRect.toShortString() + "----" + event.getX() + "---" + event.getY() + "---" + event.getRawX() + "---" + event.getRawY());
                voice_circle.getLayoutParams().width = 0;
                voice_circle.getLayoutParams().height = 0;
                if (voiceRecorder.isRecording()) {
                    try {
                        int length = stopRecoding();
                        if (mRect.contains(event.getX(), event.getY())) {
                            // discard the recorded audio.
                            //discardRecording();
                            if (length > 0) {
                                if (mEaseVoiceRecorderCallback != null) {
                                    mEaseVoiceRecorderCallback.onVoiceRecordComplete(getVoiceFilePath(), length);
                                }
                            } else if (length == -1) {
                                BaseUtil.showToast("无录音权限");
                            } else {
                                BaseUtil.showToast("录音时间太短");
                            }
                        } else {
                            // stop recording and send voice file
                            if (getVoiceFilePath() != null) {
                                File file = new File(getVoiceFilePath());
                                if (file.exists()) {
                                    file.delete();
                                }
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        BaseUtil.showToast("发送失败，请检测服务器是否连接");
                    }
                }

                return true;
            default:
                discardRecording();
                return false;
        }

    }

    private void showMoveUpToCancelHint() {
        setBackgroundColor(Color.WHITE);
        voice_tv_topcancel.setVisibility(INVISIBLE);
        voice_tv_buttomcancel.setVisibility(VISIBLE);
        voice_tv_normal.setVisibility(VISIBLE);
        voice_tv_recorderdate.setVisibility(INVISIBLE);
        voice_iv_icon.setImageResource(R.drawable.common_btn_big_red_voice);
    }

    private void showReleaseToCancelHint() {
        this.setBackgroundColor(Color.RED);
        voice_iv_icon.setImageResource(R.drawable.common_btn_big_white_voice);
        voice_tv_buttomcancel.setVisibility(INVISIBLE);
        voice_tv_topcancel.setVisibility(VISIBLE);
        voice_tv_normal.setVisibility(INVISIBLE);
        voice_tv_recorderdate.setVisibility(VISIBLE);

    }

    public int stopRecoding() {
//        if (wakeLock.isHeld())
//            wakeLock.release();
        Log.i("test", "stopRecoding: 执行");
        this.setBackgroundColor(Color.WHITE);
        voice_iv_icon.setImageResource(R.drawable.common_btn_big_red_voice);
        voice_tv_recorderdate.setText("01:00");
        mCountDownTimer.cancel();
        voice_tv_normal.setText("01:00");
        voice_tv_normal.setVisibility(VISIBLE);
        voice_tv_recorderdate.setVisibility(INVISIBLE);
        voice_tv_buttomcancel.setVisibility(INVISIBLE);
        voice_tv_topcancel.setVisibility(INVISIBLE);
        return voiceRecorder.stopRecoding();
    }

    private void discardRecording() {
        if (!isSdcardExist()) {
            BaseUtil.showToast("发送语音需要sdcard支持");
            return;
        }
        try {
//            wakeLock.acquire();
//            recordingHint.setText(context.getString(com.hyphenate.easeui.R.string.move_up_to_cancel));
//            recordingHint.setBackgroundColor(Color.TRANSPARENT);
            voiceRecorder.startRecording(mContext, "me");
        } catch (Exception e) {
            e.printStackTrace();
//            if (wakeLock.isHeld())
//                wakeLock.release();
            try {
                if (voiceRecorder != null) {
                    voiceRecorder.discardRecording();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }

            BaseUtil.showToast("录音失败，请重试！");
            return;
        }
    }

    private void startRecording() {
        if (!isSdcardExist()) {
            BaseUtil.showToast("发送语音需要sdcard支持");
            return;
        }
        try {
//            wakeLock.acquire();
            voice_circle.setVisibility(VISIBLE);
            voice_tv_recorderdate.setVisibility(INVISIBLE);
            voice_tv_buttomcancel.setVisibility(VISIBLE);
            voiceRecorder.startRecording(getContext(), "me");
        } catch (Exception e) {
            e.printStackTrace();
//            if (wakeLock.isHeld())
//                wakeLock.release();
            if (voiceRecorder != null) {
                voiceRecorder.discardRecording();
            }
            BaseUtil.showToast("录音失败，请重试！");
            return;
        }
    }

    public String getVoiceFilePath() {
        return voiceRecorder.getVoiceFilePath();
    }

    public void setEaseVoiceRecorderCallback(EaseVoiceRecorderCallback easeVoiceRecorderCallback) {
        this.mEaseVoiceRecorderCallback = easeVoiceRecorderCallback;
    }


    public interface EaseVoiceRecorderCallback {
        /**
         * on voice record complete
         *
         * @param voiceFilePath   录音完毕后的文件路径
         * @param voiceTimeLength 录音时长
         */
        void onVoiceRecordComplete(String voiceFilePath, int voiceTimeLength);
    }

    private void showToast(String msg) {
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }

    private void showToast(int msgId) {
        Toast.makeText(getContext(), msgId, Toast.LENGTH_SHORT).show();
    }

    public static String getMillon(long time, String format) {

        //return new SimpleDateFormat(format).format(new Date(time));
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+0"));
        return sdf.format(time);
    }

    public static boolean isSdcardExist() {
        return android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
    }

    private boolean requestAudio(Activity activity) {
        if (afterM()) {
            int hasPermission = activity.checkSelfPermission(Manifest.permission.RECORD_AUDIO);
            if (hasPermission != PackageManager.PERMISSION_GRANTED) {
                activity.requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, 55);
                return false;
            }
        }
        return true;
    }

//    private boolean requestStorage(Activity activity){
//        if (afterM()){
//            int hasPermission = activity.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
//            if (hasPermission != PackageManager.PERMISSION_GRANTED) {
//                activity.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
//                        REQUEST_CODE_ASK_PERMISSIONS);
//                return false;
//            }
//        }
//        return true;
//    }

    private boolean afterM() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

}
