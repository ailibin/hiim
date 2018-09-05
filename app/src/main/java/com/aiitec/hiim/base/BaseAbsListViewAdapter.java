package com.aiitec.hiim.base;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author afb
 * @Version 1.0
 * Created on 2018/1/4
 * @effect AbsListView适配器的基类
 */

public abstract class BaseAbsListViewAdapter<T> extends BaseAdapter {

    protected Context context;
    /**
     * 数据源
     */
    protected List<T> listData;
    /**
     * xml布局加载器
     */
    private LayoutInflater layoutInflater;

    public BaseAbsListViewAdapter(Context context, List<T> listData) {
        this.context = context;
        if (listData == null) {
            listData = new ArrayList<>();
        }
        this.listData = listData;
        layoutInflater = LayoutInflater.from(context);
    }

    /**
     * 供外部获取列表数据源的方法
     *
     * @return
     */
    public List<T> getListData() {
        return listData;
    }

    /**
     * 供外部设置列表数据源的方法
     *
     * @param listData
     */
    public void setListData(List<T> listData) {
        this.listData = listData;
    }

    @Override
    public int getCount() {
        return listData.size();
    }

    @Override
    public Object getItem(int position) {
        return listData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * 载入布局xml文件，转换为View
     *
     * @return
     */
    protected View loadView(int itemLayoutResId) {
        return layoutInflater.inflate(itemLayoutResId, null);
    }
}