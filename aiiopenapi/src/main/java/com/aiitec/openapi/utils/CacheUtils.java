package com.aiitec.openapi.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.aiitec.openapi.constant.AIIConstant;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class CacheUtils {
	private final List<String> cacheDirs = new ArrayList<String>();
	
	public void addCacheDir(String dir){
		cacheDirs.add(dir);
	}
	
	public void removeCacheDir(String dir){
		if(cacheDirs.contains(dir)){
			cacheDirs.remove(dir);
		}
		
	}

	private Context context;
	public CacheUtils(Context context) {
		this.context = context;
		cacheDirs.add(getCacheDir());
	}
	
	/**
     * 获取协议缓存路径
     * @return
     */
    public String getCacheDir(){
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
    public void clearCache(){
		new Thread(new Runnable() {
			@Override
			public void run() {

				for(String dir: cacheDirs){
					deleteFolderFile(dir);
				}
				AiiUtil.clearData(context);
			
				if(onClearCacheListener != null){
					new Handler(Looper.getMainLooper()).post(new Runnable() {
						
						@Override
						public void run() {
							onClearCacheListener.onFinish();
						}
					});
				}
			}
		}).start();
	}
	/**
	 * 获取缓存文件大小
	 */
	public void getCacheSize(final OnCacheSizeListener onCacheSizeListener) {

		new Thread(new Runnable() {

			@Override
			public void run() {
				long size = 0;
				for(String dir : cacheDirs){
					size += getFolderSize(new File(dir));
				}
				
				final String cachaSize = getFormatSize(size);
			
				final long finalSize = size; 
			
				if(onCacheSizeListener != null){
					new Handler(Looper.getMainLooper()).post(new Runnable() {
						
						@Override
						public void run() {
							onCacheSizeListener.getSize(finalSize, cachaSize);
						}
					});
				}
			}
		}).start();
	}
	/**
	 * 格式化单位
	 *
	 * @param size
	 * @return
	 */
	public static String getFormatSize(double size) {
		double kiloByte = size / 1024;
		if (kiloByte < 1) {
			return "0.0 K";
		}

		double megaByte = kiloByte / 1024;
		if (megaByte < 1) {
			BigDecimal result1 = new BigDecimal(Double.toString(kiloByte));
			return result1.setScale(2, BigDecimal.ROUND_HALF_UP)
					.toPlainString() + "K";
		}

		double gigaByte = megaByte / 1024;
		if (gigaByte < 1) {
			BigDecimal result2 = new BigDecimal(Double.toString(megaByte));
			return result2.setScale(2, BigDecimal.ROUND_HALF_UP)
					.toPlainString() + "M";
		}

		double teraBytes = gigaByte / 1024;
		if (teraBytes < 1) {
			BigDecimal result3 = new BigDecimal(Double.toString(gigaByte));
			return result3.setScale(2, BigDecimal.ROUND_HALF_UP)
					.toPlainString() + "G";
		}
		BigDecimal result4 = new BigDecimal(teraBytes);
		return result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString()
				+ "T";
	}
	/**
	 * 删除指定目录下文件及目录
	 *
	 * @return
	 */
	public static void deleteFolderFile(String filePath) {
		if (!TextUtils.isEmpty(filePath)) {
			try {
				File file = new File(filePath);
				if(!file.exists()){
					return;
				}
				if (file.isDirectory()) {// 处理目录
					File files[] = file.listFiles();
					for (int i = 0; i < files.length; i++) {
						deleteFolderFile(files[i].getAbsolutePath());
					}
				}
				if (!file.isDirectory()) {// 如果是文件，删除
					file.delete();
				} else {// 目录
					if (file.listFiles().length == 0) {// 目录下没有文件或者目录，删除
						file.delete();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	/**
	 * 获取文件夹大小
	 *
	 * @param file
	 *            File实例
	 * @return long
	 */
	public static long getFolderSize(java.io.File file) {

		long size = 0;
		try {
			if(!file.exists()){
				return 0;
			}
			if (file.isDirectory()) {
				java.io.File[] fileList = file.listFiles();
				for (int i = 0; i < fileList.length; i++) {
					if (fileList[i].isDirectory()) {
						size = size + getFolderSize(fileList[i]);

					} else {
						size = size + fileList[i].length();

					}
				}
			} else {
				size = file.length();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		// return size/1048576;
		return size;
	}
	private OnClearCacheListener onClearCacheListener;
	public void setOnClearCacheListener(
			OnClearCacheListener onClearCacheListener) {
		this.onClearCacheListener = onClearCacheListener;
	}
	
	public interface OnClearCacheListener {
		void onFinish();
	}

	public interface OnCacheSizeListener {
		void getSize(long size, String formatSize);
	}
}
