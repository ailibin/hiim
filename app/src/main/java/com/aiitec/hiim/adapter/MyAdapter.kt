package com.aiitec.hiim.adapter

import android.content.Context
import android.widget.TextView
import com.aiitec.hiim.R
/**
 * @author  Anthony
 * @version 1.0
 * createTime 2017/11/18.
 */
class MyAdapter(context : Context, datas : MutableList<String>) : CommonRecyclerViewAdapter<String>(context, datas){
    override fun convert(h: CommonRecyclerViewHolder, item: String, position: Int) {
        val text = h.getView<TextView>(android.R.id.text1)
        text.text = item
        text.setBackgroundResource(R.drawable.item_selector)
    }

    override fun getLayoutViewId(viewType: Int): Int = android.R.layout.simple_list_item_1

}