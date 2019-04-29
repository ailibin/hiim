package com.aiitec.hiim.im.adapter

import android.app.Activity
import android.content.Context
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.aiitec.hiim.R
import com.aiitec.hiim.adapter.CommonRecyclerViewAdapter
import com.aiitec.hiim.adapter.CommonRecyclerViewHolder
import com.aiitec.hiim.im.entity.Item
import com.aiitec.hiim.utils.ScreenUtils

/**
 * Created by ailibin on 2018/1/9.
 * 聊天界面底部四个操作图标
 */
class ChatOperationAdapter(context: Context, data: List<Item>) : CommonRecyclerViewAdapter<Item>(context, data) {
    override fun convert(h: CommonRecyclerViewHolder?, item: Item?, position: Int) {

        val ivOperation = h?.getView<ImageView>(R.id.iv_operation_imageView)
        val tvContent = h?.getView<TextView>(R.id.tv_operation_content)
        val container = h?.getView<LinearLayout>(R.id.ll_item_container)

        val screenWidth = ScreenUtils.getScreenWidth()
        val marginOffset = ScreenUtils.dip2px(context as Activity?, 50f)
        val videoItemsWidthSize = screenWidth - marginOffset
        container?.layoutParams?.width = videoItemsWidthSize / 4


        if (item != null) {
            ivOperation?.setImageResource(item?.imagePath)
        }
        tvContent?.text = item?.content

    }

    override fun getLayoutViewId(viewType: Int): Int = R.layout.item_operation_adapter
}