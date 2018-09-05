package com.aiitec.openapi.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;


/**
 * 加密类
 * 
 * @author Anthony
 * 
 */
public class Encrypt {

    private static final String TIME_ZONE = "GMT+8";
    /**
	 * MD5加密
	 * @param source 加密内容
	 * @return 加密后的内容（String类型）
	 * @throws UnsupportedEncodingException
	 */
	public static final String md5(String source)  {
		String dest = null;
		try {
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			byte[] byteArray;
			try {
				byteArray = source.getBytes("UTF-8");
				byte[] md5Bytes = md5.digest(byteArray);
				StringBuffer hexValue = new StringBuffer();
				for (int i = 0; i < md5Bytes.length; i++) {
					int val = (md5Bytes[i]) & 0xff;
					if (val < 16)
						hexValue.append("0");
					hexValue.append(Integer.toHexString(val));
				}
				dest = hexValue.toString();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return dest;
	}
	
    /**
     * 加密
     * 
     * @param md5Query
     * @return 加密后的字符串
     */
    public static String encrypt(String md5Query) {
        String encryption = "";
        String str = initTime();
        int timeNum = Integer.parseInt(str);
        int index = timeNum % 8;
        String str2 = subStringTime(str, index);
        String source = StrToBinstr(str2);
        encryption = md5(md5(md5Query) + source);
        return encryption;
    }

    private static String initTime() {
        TimeZone time = TimeZone.getTimeZone(TIME_ZONE); // 设置为东八区
        TimeZone.setDefault(time);// 设置时区
        Calendar calendar = Calendar.getInstance();// 获取实例
        DateFormat f = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());// 构造格式化模板
        return f.format(calendar.getTime());
    }

    private static String subStringTime(String str, int index) {
        return str.substring(0, index) + str.charAt(str.length() - 1) + str.substring(index, str.length() - 1);
    }

    /**
     * 将字符串转换成二进制字符串
     * 
     * @param str
     * @return
     */
    private static String StrToBinstr(String str) {
        long data = Long.parseLong(str);
        String result = "";
        result = Long.toBinaryString(data);
        return result;
    }

    /**
     * 加盐内容
     */
    private static String saltingStr = "81hqbcqfn5m80dreg526s8knq6";

    /**
     * 修改加盐内容
     * 
     * @param saltingStr
     */
    public static void setSaltingStr(String saltingStr) {
        Encrypt.saltingStr = saltingStr;
    }

    /**
     * 密码加盐
     * 
     * @param str
     * @return 加盐后的密码
     */
    public static String saltingPassword(String str) {
        return md5(str + saltingStr);
    }
}
