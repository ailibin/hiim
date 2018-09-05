package com.aiitec.openapi.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.aiitec.openapi.constant.AIIConstant;


import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

/**
 * 常用方法类
 * 
 * @author Anthony
 * @version 1.0
 * 
 */
public final class PacketUtil {

    public static String session_id;

    /**
     * 获取版本名称
     * 
     * @param context
     *            上下文对象
     * @return 版本名称
     */
    public static String getVersionName(Context context) {
        String version = null;
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            version = info.versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return version;
    }

    /**
     * 格式化json
     * 
     * @param content
     * @return
     */
    public static String formatJson(String content) {

        StringBuffer sb = new StringBuffer();
        int index = 0;
        int count = 0;
        while (index < content.length()) {
            char ch = content.charAt(index);
            if (ch == '{' || ch == '[') {
                sb.append(ch);
                sb.append('\n');
                count++;
                for (int i = 0; i < count; i++) {
                    sb.append('\t');
                }
            } else if (ch == '}' || ch == ']') {
                sb.append('\n');
                count--;
                for (int i = 0; i < count; i++) {
                    sb.append('\t');
                }
                sb.append(ch);
            } else if (ch == ',') {
                sb.append(ch);
                sb.append('\n');
                for (int i = 0; i < count; i++) {
                    sb.append('\t');
                }
            } else {
                sb.append(ch);
            }
            index++;
        }
        return sb.toString();
    }

    /**
     * 把格式化的json紧凑
     * 
     * @param content
     * @return
     */
    public static String compactJson(String content) {
        String regEx = "[\t\n]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(content);
        return m.replaceAll("").trim();
    }

    /**
     * 是否是常用数据类型，包括常用类的包装类Integer等和String
     * 
     * @param classType
     *            需要比较的类
     * @return 是否是常用数据类型
     */
    public static boolean isCommonField(Class<?> classType) {
        boolean isCommonField = (classType.equals(int.class) || classType.equals(Integer.class)
                || classType.equals(float.class) || classType.equals(Float.class) || classType.equals(double.class)
                || classType.equals(Double.class) || classType.equals(long.class) || classType.equals(Long.class)
                || classType.equals(char.class) || classType.equals(String.class) || classType.equals(boolean.class) || classType
                .equals(Boolean.class));
        return isCommonField;
    }

    /**
     * 获得指定文件的byte数组
     * 
     * @param filePath
     *            文件路径
     * @return byte数组
     */
    public static byte[] getBytes(String filePath) {
        byte[] buffer = null;
        try {
            File file = new File(filePath);
            FileInputStream fis = new FileInputStream(file);
            ByteArrayOutputStream bos = new ByteArrayOutputStream(1000);
            byte[] b = new byte[1000];
            int n;
            while ((n = fis.read(b)) != -1) {
                bos.write(b, 0, n);
            }
            fis.close();
            bos.close();
            buffer = bos.toByteArray();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buffer;
    }

    /**
     * inputStream转byte[]
     * 
     * @param input
     * @return
     * @throws IOException
     */
    public static byte[] toByteArray(InputStream input) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int n = 0;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
        }
        return output.toByteArray();
    }

    /**
     * 获取协议缓存路径
     * @param context
     * @return
     */
    public static String getCacheDir(Context context){
        String cacheDir = "";
        if(context.getExternalCacheDir() != null){
            cacheDir = context.getExternalCacheDir().getAbsolutePath()+"/cache/";
        } else if(AiiUtil.isSDCardEnable()){
            cacheDir = AiiUtil.getSDCardPath()+"/"+context.getPackageName()+"/cache/";
        }
        if(AIIConstant.USER_ID > 0){
            StringBuilder sb = new StringBuilder();
            sb.append(cacheDir);
            if(!cacheDir.endsWith("/")){
                sb.append("/");
            }
            sb.append(AIIConstant.USER_ID).append("/");
            cacheDir = sb.toString();
        }
        return cacheDir;

    }
}


