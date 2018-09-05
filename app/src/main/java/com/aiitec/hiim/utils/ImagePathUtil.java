package com.aiitec.hiim.utils;


import com.aiitec.hiim.base.Api;

/**
 * @Author Xiaobing
 * @Version 1.0
 * Created on 2017/10/19
 * @effect 拼接图片路径的工具
 */

public class ImagePathUtil {

    /**
     * 获取完整图片路径
     *
     * @param relativeImagePath 相对路径
     * @return
     */
    public static String getWholeImagePath(String relativeImagePath) {
        if (relativeImagePath != null) {
            if (relativeImagePath.startsWith("http")) {
                return relativeImagePath;
            } else {
                return Api.INSTANCE.getIMAGE_URL() + "/uploadfiles/" + relativeImagePath;
            }
        } else {
            return "";
        }
    }

    /**
     * 获取图片的绝对路径
     *
     * @param absoluteImagePath
     * @return
     */
    public static String getAbsoluteImagePath(String absoluteImagePath) {
        if (absoluteImagePath != null) {
            if (absoluteImagePath.startsWith("http")) {
                return absoluteImagePath;
            } else {
                return Api.INSTANCE.getBASE_URL() + absoluteImagePath;
            }
        } else {
            return "";
        }
    }
}
