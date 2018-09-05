package com.herentan.giftfly.ui.location

import android.content.Context
import android.graphics.Color
import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.aiitec.hiim.R
import com.aiitec.hiim.adapter.CommonRecyclerViewAdapter
import com.aiitec.hiim.adapter.CommonRecyclerViewHolder
import com.aiitec.hiim.im.location.widget.MyTextView
import com.herentan.giftfly.ui.location.entity.Area

/**
 * Created by ailibin on 2018/1/15.
 *  搜索出来的的adapter
 */
class LocationAdapter(context: Context, data: List<Area>) : CommonRecyclerViewAdapter<Area>(context, data) {

    override fun convert(h: CommonRecyclerViewHolder?, item: Area?, position: Int) {

        val ivSelect = h?.getView<ImageView>(R.id.iv_select_address)

        val poiFieldId = h?.getView<MyTextView>(R.id.poi_field_id)

        val poiFieldValue = h?.getView<TextView>(R.id.poi_value_id)

        val includeLine = h?.getView<View>(R.id.include_line)

        val includeFirstLine = h?.getView<View>(R.id.include_first_line)

        //地址名字
        if (!TextUtils.isEmpty(item?.searchKey)) {
            poiFieldId?.setSpecifiedTextsColor(item?.name, item?.searchKey, Color.parseColor("#DE7FEF"))
        } else {
            poiFieldId?.text = item?.name
        }

        //内容,详细的地址信息
        poiFieldValue?.text = item?.address

        if (position == 0) {
            includeFirstLine?.visibility = View.VISIBLE
        } else {
            includeFirstLine?.visibility = View.GONE
        }

        if (item != null) {
            if (item.isSelected) {
                ivSelect?.visibility = View.VISIBLE
//                ivSelect?.setImageResource(R.drawable.common_btn_choose_pressed2x)
            } else {
                ivSelect?.visibility = View.GONE
            }
        }

        if (position == data.size - 1) {
            includeLine?.visibility = View.GONE
        } else {
            includeLine?.visibility = View.VISIBLE
        }
    }


    override fun getLayoutViewId(viewType: Int): Int {
        return R.layout.item_layout
    }
}