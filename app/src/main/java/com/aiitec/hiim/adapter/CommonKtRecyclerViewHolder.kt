package com.aiitec.hiim.adapter

import android.support.v7.widget.RecyclerView
import android.util.SparseArray
import android.view.View
import android.widget.ImageView
import android.widget.TextView


/**
 * @description
 * @author Anthony
 * createTime 2017/6/8
 * @version 1.0
 */
class CommonKtRecyclerViewHolder
/**
 * 构造函数

 * @param itemView
 */
(itemView: View) : RecyclerView.ViewHolder(itemView) {

    /**
     * 用来保存条目视图里面所有的控件
     */
    private var mViews: SparseArray<View>? = null

    init {
        mViews = SparseArray<View>()
    }

    /**
     * 根据控件id获取控件对象

     * @param viewId
     * *
     * @return
     */
    fun <T : View> getView(viewId: Int): T? {

        // 从集合中根据这个id获取view视图对象
        var view: T? = mViews!!.get(viewId) as T?

        // 如果为空，说明是第一次获取，里面没有，那就在布局文件中找到这个控件，并且存进集合中
        if (view == null) {
            view = itemView.findViewById<T?>(viewId)
            mViews!!.put(viewId, view )
        }

        // 返回控件对象
        return view
    }

    /**
     * 为TextView设置文本,按钮也可以用这个方法,button是textView的子类

     * @param textViewId
     * *
     * @param content
     */
    fun setText(textViewId: Int, content: String) {
        getView<TextView>(textViewId)?.text = content
    }

    /**
     * 为ImageView设置图片

     * @param iv
     * *
     * @param imageId
     */
    fun setImage(iv: ImageView, imageId: Int) {
        iv.setImageResource(imageId)
    }

    /**
     * 为ImageView设置图片

     * @param imgId
     * *
     * @param imageId
     */
    fun setImage(imgId: Int, imageId: Int) {
        getView<ImageView>(imgId)?.setImageResource(imageId)
    }


}