package com.aiitec.imlibrary.ui;

import android.app.AppOpsManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaRecorder;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.format.Time;
import android.util.Log;

import java.io.File;
import java.io.IOException;

/**
 * @author Anthony
 * @version 1.0
 *          createTime 2017/9/19.
 */
public class EaseVoiceRecorder {

    public static final String TAG = EaseVoiceRecorder.class.getSimpleName();
    public static final String PREFERENCE_NAME = EaseVoiceRecorder.class.getSimpleName();
    MediaRecorder recorder;

    static final String PREFIX = "voice";
    static final String EXTENSION = ".amr";

    private boolean isRecording = false;
    private long startTime;
    private String voiceFilePath = null;
    private String voiceFileName = null;
    private File file;
    private Handler handler;
    public static boolean putString(Context context, String key, String value) {
        SharedPreferences settings = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(key, value);
        return editor.commit();
    }

    public static String getString(Context context, String key) {
        return getString(context, key);
    }
    public EaseVoiceRecorder(Handler handler) {
        this.handler = handler;
        BuildProperties.newInstance();
    }

    /**
     * start recording to the file
     */
    public String startRecording(Context appContext, String uid) {
        file = null;
        try {
            // need to create recorder every time, otherwise, will got exception
            // from setOutputFile when try to reuse
            if (recorder != null) {
                recorder.release();
                recorder = null;
            }
            recorder = new MediaRecorder();
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            // MONO
            recorder.setAudioChannels(1);
            // 8000Hz
            recorder.setAudioSamplingRate(8000);
            // seems if change this to
            recorder.setAudioEncodingBitRate(64);
            // 128, still got same file
            // size.
            // one easy way is to use temp file
            // file = File.createTempFile(PREFIX + userId, EXTENSION,
            // User.getVoicePath());
//            voiceFileName = getVoiceFileName("123");
//            PathUtil.getInstance().initDirs("1","123",appContext);
            voiceFileName = getVoiceFileName(uid);
            voiceFilePath = PathUtil.getInstance().getVoicePath() + "/" + voiceFileName;
            file = new File(voiceFilePath);
            recorder.setOutputFile(file.getAbsolutePath());
            recorder.prepare();
            isRecording = true;
            recorder.start();
            boolean isPermission = checkAppops(appContext, AppOpsManager.OPSTR_RECORD_AUDIO);
            if(isPermission){
                new Thread(new Runnable() {
                    private int time;

                    @Override
                    public void run() {
                        while (isRecording) {
                            try {

                                SystemClock.sleep(100);
                                Message msg = Message.obtain();
                                msg.what = recorder.getMaxAmplitude() * 13 / 0x7FFF;
                                //                            msg.what = new Random().nextInt(100);
                                msg.arg1 = time;
                                handler.sendMessage(msg);
                                time += 100;

                            } catch (Error e) {
                                e.printStackTrace();
                                Log.e(TAG, e.getMessage());
                                isRecording = false;
                            } catch (Exception e) {
                                // from the crash report website, found one NPE crash from
                                // one android 4.0.4 htc phone
                                // maybe handler is null for some reason
                                e.printStackTrace();
//                                Log.e(TAG, e.getMessage());
                                isRecording = false;
                            }
                        }
                    }
                }).start();
            }


            startTime = System.currentTimeMillis();
            Log.d(TAG, "start voice recording to file:" + file.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "prepare() failed");
            isRecording = false;
        }


        return file == null ? null : file.getAbsolutePath();
    }
    /**
     * 查看原生态的权限是否有授权
     *
     * @param context
     * @param op      如定位权限 AppOpsManager.OPSTR_FINE_LOCATION
     * @return
     */
    public static boolean checkAppops(Context context, String op) {
//        if (BuildProperties.isMIUI) { // 只有小米手机才检测
//        int checkLocalPhonePermission = ActivityCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO);
//        if (checkLocalPhonePermission != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions((Activity) context,
//                    new String[]{Manifest.permission.RECORD_AUDIO}, 101);
//            return false;
//        }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                try {
                    //适配小米机型
                    AppOpsManager appOpsManager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
                    int checkOp = appOpsManager.checkOp(op, Binder.getCallingUid(), context.getPackageName());
                    if (checkOp == AppOpsManager.MODE_IGNORED) {
                        return false;
                    }
                } catch (Exception e){
                    e.printStackTrace();
                    return false;
                }

            }
//        }
        return true;
    }



//    // 注意这个状态最好用SharePreference保存起来，需要每次读取检测
//    public static boolean isMIUI(Context context) {
//        //获取缓存状态
//        String miui = getString(context,IS_MIUI);
//        if (miui != null) {
//            if ("1".equals(miui)) {
//                return true;
//            } else if ("2".equals(miui)) {
//                return false;
//            }
//        }
//        Properties prop = new Properties();
//        boolean isMIUI;
//        try {
//            prop.load(new FileInputStream(new File(Environment.getRootDirectory(), "build.prop")));
//        } catch (IOException e) {
//            e.printStackTrace();
//            return false;
//        }
//        isMIUI = prop.getProperty(KEY_MIUI_VERSION_CODE, null) != null
//                || prop.getProperty(KEY_MIUI_VERSION_NAME, null) != null
//                || prop.getProperty(KEY_MIUI_INTERNAL_STORAGE, null) != null;
//        putString(context, IS_MIUI, isMIUI ? "1" : "2");
//        return isMIUI;
//    }
    /**
     * stop the recoding
     *
     * @return seconds of the voice recorded
     */

    public void discardRecording() {
        if (recorder != null) {
            try {
                //这里防止某些手机崩溃掉
                recorder.stop();
                recorder.release();
                recorder = null;
                if (file != null && file.exists() && !file.isDirectory()) {
                    file.delete();
                }
            } catch (IllegalStateException e) {
            } catch (RuntimeException e) {
            }
            isRecording = false;
        }
    }

    public int stopRecoding() {
        try {
            if (recorder != null) {
                isRecording = false;
                recorder.stop();
                recorder.release();
                recorder = null;

                if (file == null || !file.exists() || !file.isFile()) {
                    return -1;
                }
                if (file.length() == 0) {
                    file.delete();
                    return -1;
                }
                int seconds = (int) (System.currentTimeMillis() - startTime) / 1000;
                Log.d(TAG, "voice recording finished. seconds:" + seconds + " file length:" + file.length());
                return seconds;
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        return 0;
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        if (recorder != null) {
            recorder.release();
        }
    }

    private String getVoiceFileName(String uid) {
        Time now = new Time();
        now.setToNow();
        return uid + now.toString().substring(0, 15) + EXTENSION;
    }

    public boolean isRecording() {
        return isRecording;
    }


    public String getVoiceFilePath() {
        return voiceFilePath;
    }

    public String getVoiceFileName() {
        return voiceFileName;
    }
}
