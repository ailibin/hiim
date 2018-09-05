package com.aiitec.openapi.utils;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;

/**
 * 屏幕像素相关工具
 * @author Anthony
 *
 */
public class ScreenUtils {
	
	public static DisplayMetrics metric = new DisplayMetrics();
	private static int screenWidth;
	private static int screenHeight;
	private static float density;

	public static void init(Context context) {
		if(context == null){
			throw new RuntimeException("context must be not null !");
		}
		WindowManager manager = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		manager.getDefaultDisplay().getMetrics(metric);
		screenWidth = metric.widthPixels;
		screenHeight = metric.heightPixels;
		density = metric.density;

	}
	/**
	 * 得到屏幕高度
	 * 
	 * @author shc DateTime 2014-11-20 下午3:04:44
	 * @param context
	 * @return
	 */
	public static int getScreenWidth(Context context) {
		if(screenWidth > 0){
			return screenWidth;
		}
		init(context);
		return screenWidth;
		
	}

	/**
	 * 得到屏幕宽度
	 * 
	 * @author shc DateTime 2014-11-20 下午3:04:44
	 * @param context
	 * @return
	 */
	public static int getScreenHeight(Context context) {
		if(screenHeight > 0){
			return screenHeight;
		}
		init(context);
		return screenHeight;
	}

	public static float getDensity(Context context) {
		if(density > 0){
			return density;
		}
		init(context);
		return density;
	}

	/**
	 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
	 * @param context
	 * @param dpValue dp值
	 * @return 像素值
	 */
	public static int dip2px(Context context, float dpValue) {
		
		final float scale = getDensity(context);
		return (int) (dpValue * scale + 0.5f);
	}

	/**
	 * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
	 * @param context
	 * @param pxValue 像素值
	 * @return dp值
	 */
	public static int px2dip(Context context, float pxValue) {

		final float scale = getDensity(context);
		return (int) (pxValue / scale + 0.5f);
	}

	/**
	 * 在view还没有创建出来时测量其宽度和高度（0为宽度，1为高度）
	 *
	 * @param measureView
	 * @return int数组
	 */
	public static int[] getViewWidthAndHeight(View measureView) {
		int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
		int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
		measureView.measure(w, h);
		int height = measureView.getMeasuredHeight();
		int width = measureView.getMeasuredWidth();
		int[] wAndH = {width, height};

		return wAndH;
	}

}
