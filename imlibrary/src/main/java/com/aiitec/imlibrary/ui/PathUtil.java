package com.aiitec.imlibrary.ui;

import android.content.Context;
import android.os.Environment;

import java.io.File;

/**
 * @author Anthony
 * @version 1.0
 *          createTime 2017/9/19.
 */

public class PathUtil {
    public static String pathPrefix;
    public static final String HISTORY_PATH_NAME = "/chat/";
    public static final String IMAGE_PATH_NAME = "/image/";
    public static final String VOICE_PATH_NAME = "/voice/";
    public static final String FILE_PATH_NAME = "/file/";
    public static final String VIDEO_PATH_NAME = "/video/";
    public static final String NETDISK_DOWNLOAD_PATH_NAME = "/netdisk/";
    public static final String MEETING_PATH_NAME = "/meeting/";
    private static File storageDir = null;
    private static PathUtil instance = null;
    private File voicePath = null;
    private File imagePath = null;
    private File historyPath = null;
    private File videoPath = null;
    private File filePath;

    private PathUtil() {
    }

    public static PathUtil getInstance() {
        if(instance == null) {
            instance = new PathUtil();
        }

        return instance;
    }

    public void initDirs(String var1, String var2, Context context) {
        String packageName = context.getPackageName();
        pathPrefix = "/Android/data/" + packageName + "/";
        this.voicePath = generateVoicePath(var1, var2, context);
        if(!this.voicePath.exists()) {
            this.voicePath.mkdirs();
        }

        this.imagePath = generateImagePath(var1, var2, context);
        if(!this.imagePath.exists()) {
            this.imagePath.mkdirs();
        }

        this.historyPath = generateHistoryPath(var1, var2, context);
        if(!this.historyPath.exists()) {
            this.historyPath.mkdirs();
        }

        this.videoPath = generateVideoPath(var1, var2, context);
        if(!this.videoPath.exists()) {
            this.videoPath.mkdirs();
        }

        this.filePath = generateFiePath(var1, var2, context);
        if(!this.filePath.exists()) {
            this.filePath.mkdirs();
        }

    }

    public File getImagePath() {
        return this.imagePath;
    }

    public File getVoicePath() {
        return this.voicePath;
    }

    public File getFilePath() {
        return this.filePath;
    }

    public File getVideoPath() {
        return this.videoPath;
    }

    public File getHistoryPath() {
        return this.historyPath;
    }

    private static File getStorageDir(Context var0) {
        if(storageDir == null) {
            File var1 = Environment.getExternalStorageDirectory();
            if(var1.exists()) {
                return var1;
            }

            storageDir = var0.getFilesDir();
        }

        return storageDir;
    }

    private static File generateImagePath(String var0, String var1, Context var2) {
        String var3 = null;
        if(var0 == null) {
            var3 = pathPrefix + var1 + "/image/";
        } else {
            var3 = pathPrefix + var0 + "/" + var1 + "/image/";
        }

        return new File(getStorageDir(var2), var3);
    }

    private static File generateVoicePath(String var0, String var1, Context var2) {
        String var3 = null;
        if(var0 == null) {
            var3 = pathPrefix + var1 + "/voice/";
        } else {
            var3 = pathPrefix + var0 + "/" + var1 + "/voice/";
        }

        return new File(getStorageDir(var2), var3);
    }

    private static File generateFiePath(String var0, String var1, Context var2) {
        String var3 = null;
        if(var0 == null) {
            var3 = pathPrefix + var1 + "/file/";
        } else {
            var3 = pathPrefix + var0 + "/" + var1 + "/file/";
        }

        return new File(getStorageDir(var2), var3);
    }

    private static File generateVideoPath(String var0, String var1, Context var2) {
        String var3 = null;
        if(var0 == null) {
            var3 = pathPrefix + var1 + "/video/";
        } else {
            var3 = pathPrefix + var0 + "/" + var1 + "/video/";
        }

        return new File(getStorageDir(var2), var3);
    }

    private static File generateHistoryPath(String var0, String var1, Context var2) {
        String var3 = null;
        if(var0 == null) {
            var3 = pathPrefix + var1 + "/chat/";
        } else {
            var3 = pathPrefix + var0 + "/" + var1 + "/chat/";
        }

        return new File(getStorageDir(var2), var3);
    }

    public static File getTempPath(File var0) {
        return new File(var0.getAbsoluteFile() + ".tmp");
    }
}
