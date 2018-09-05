package com.aiitec.hiim.im.model;

import android.content.Context;
import android.util.TypedValue;
import android.widget.TextView;

import com.aiitec.hiim.R;
import com.aiitec.hiim.base.App;
import com.aiitec.hiim.im.adapter.ChatAdapter;
import com.tencent.imsdk.TIMLocationElem;
import com.tencent.imsdk.TIMMessage;


/**
 *
 * @author ailibin
 * @date 2018/2/1
 * 位置消息
 */

public class LocationMessage extends Message {


    private static final String TAG = "LocationMessage";

    public LocationMessage(TIMMessage message) {
        this.message = message;
    }

    /**
     * 地理位置坐标点和描述
     *
     * @param la   纬度坐标点
     * @param lg   经度坐标点
     * @param addr 地址详细地址
     */
    public LocationMessage(double la, double lg, String addr) {
        message = new TIMMessage();
        TIMLocationElem elem = new TIMLocationElem();
        //设置纬度
        elem.setLatitude(la);
        //设置经度
        elem.setLongitude(lg);
        elem.setDesc(addr);
        message.addElement(elem);
    }

    /**
     * 显示消息
     *
     * @param viewHolder 界面样式
     * @param context    显示消息的上下文
     */
    @Override
    public void showMessage(ChatAdapter.ViewHolder viewHolder, Context context) {
        clearView(viewHolder);
        if (checkRevoke(viewHolder)) {
            return;
        }
        TIMLocationElem e = (TIMLocationElem) message.getElement(0);
        TextView tv = new TextView(App.Companion.getContext());
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        tv.setTextColor(App.Companion.getContext().getResources().getColor(isSelf() ? R.color.white : R.color.black));
        tv.setText(e.getDesc() + " " + e.getLatitude() + "," + e.getLongitude());
        getBubbleView(viewHolder).addView(tv);
        showStatus(viewHolder);
    }

    @Override
    public String getSummary() {
        String str = getRevokeSummary();
        if (str != null) {
            return str;
        }
        return App.Companion.getContext().getString(R.string.summary_location);
    }

    @Override
    public void save() {

    }
}
