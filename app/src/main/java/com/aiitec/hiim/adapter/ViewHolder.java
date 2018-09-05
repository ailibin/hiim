package com.aiitec.hiim.adapter;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ViewHolder {
	
	private final SparseArray<View> mViews;
	private View mConvertView;
	private int mPosition;
	
	private ViewHolder(Context context, ViewGroup parent, int layoutId, int position){
		this.mPosition = position;
		this.mViews = new SparseArray<View>();
		this.mConvertView = LayoutInflater.from(context).inflate(layoutId, parent, false);
		//setTag
		mConvertView.setTag(this);
	}



	/**
	 * 拿到一个 ViewHolder 对象
	 * @param context
	 * @param convertView
	 * @param parent
	 * @param layoutId
	 * @param position
	 * @return
	 */
	public static ViewHolder get(Context context, View convertView, ViewGroup parent, int layoutId, int position){
		if (convertView == null) {
			return new ViewHolder(context, parent, layoutId, position);
		}else {
			ViewHolder holder = (ViewHolder) convertView.getTag();
			holder.mPosition = position;
			return holder;
		}
	}
	
	
	/**
	 * 通过控件的Id获取对应的控件，如果没有则加入 Map中
	 * @param viewId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T extends View> T getView(int viewId){
		View view = mViews.get(viewId);
		if (view == null) {
			view = mConvertView.findViewById(viewId);
			mViews.put(viewId, view);
		}
		return (T) view;
	}
	
	/**
	 * 返回该 item 的 view
	 * @return
	 */
	public View getConvertView(){
		return mConvertView;
	}
	
	/**
	 * 返回该item的 position
	 * @return
	 */
	public int getPosition(){
		return mPosition;
	}
}






