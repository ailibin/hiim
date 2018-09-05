package com.aiitec.openapi.utils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 文件md5加密类
 * 
 * @author Anthony
 * @version 1.0
 * @createTime 2016-4-22
 */
public class FileMD5Utils {



    public static String getMD5(byte[] data) throws NoSuchAlgorithmException, IOException {
        return getMd5InputStream(new ByteArrayInputStream(data));
    }
    public static String getMD5(File file) throws NoSuchAlgorithmException, IOException {
        return getMd5InputStream(new FileInputStream(file));
    }
    public static String getMD5(InputStream fis) throws NoSuchAlgorithmException, IOException  {
        return getMd5InputStream(fis);
    }

    public static String getMd5(String filename) throws NoSuchAlgorithmException, IOException {
        return getMd5InputStream(new FileInputStream(filename));
    }

    private static String getMd5InputStream(InputStream fis) throws NoSuchAlgorithmException, IOException {

        byte[] buffer = new byte[1024];
        MessageDigest complete = MessageDigest.getInstance("MD5");
        int numRead;
        do {
            numRead = fis.read(buffer);
            if (numRead > 0) {
                complete.update(buffer, 0, numRead);
            }
        } while (numRead != -1);

        fis.close();
        byte[] b = complete.digest();
        StringBuffer result = new StringBuffer();
        for (int i = 0; i < b.length; i++) {
            result .append(Integer.toString((b[i] & 0xff) + 0x100, 16).substring(1));
        }
        return result.toString();
    }


}
