package com.aiitec.hiim.im.model;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.aiitec.hiim.LocationMessageActivity;
import com.aiitec.hiim.base.App;
import com.aiitec.hiim.im.adapter.ChatAdapter;
import com.aiitec.hiim.utils.GlideRoundTransform;
import com.aiitec.openapi.utils.LogUtil;
import com.amap.api.maps2d.model.LatLng;
import com.bumptech.glide.Glide;
import com.herentan.giftfly.ui.location.entity.Area;
import com.tencent.imsdk.TIMCustomElem;
import com.tencent.imsdk.TIMMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import static com.aiitec.hiim.im.model.CustomMessage.Type.LOCATION;


/**
 * 自定义消息
 *
 * @author anthony
 */
public class CustomMessage extends Message {

    private static final int TYPE_TYPING = 14;
    private static final int TYPE_CUSTOM = 15;

    private final String ACTION = "action";
    private final String CONTENT = "content";

    //物理地址实体
    private final String ADDRESS = "address";
    private final String TITLE = "title";
    private final String LATITUDE = "latitude";
    private final String LONGITUDE = "longitude";
    private final String MAPURL = "mapUrl";

    //礼物包实体
    private final String PACKAGEID = "packageId";
    private final String PACKAGETYPE = "packageType";
    private final String SENDERNAME = "senderName";
    private final String RECEIVERNAME = "receiverName";

    //商品和订单相关
    private final String GOODSID = "goodsId";
    private final String IMAGE = "image";
    private final String MONEY = "money";
    private final String ORDERID = "orderId";
    private final String ORDERNUM = "orderNum";
    private final String TIME = "time";

    /**
     * 物理地址信息
     */
    private String address;
    private String title;
    private String mapUrl;
    private String latitude;
    private String longitude;
    private Area mArea;
    private Type type;
    private String content;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public CustomMessage(TIMMessage message) {
        this.message = message;
        TIMCustomElem elem = (TIMCustomElem) message.getElement(0);
        parse(elem.getData());

    }

    /**
     * 地理位置消息构造函数
     *
     * @param area
     * @param type
     */
    public CustomMessage(Area area, Type type) {
        mArea = area;
        this.type = type;
        message = new TIMMessage();
        String data = "";
        JSONObject dataJson = new JSONObject();
        LogUtil.d("ailibin", "mArea: " + mArea.toString());
        try {
            dataJson.put(ACTION, type.value);
            dataJson.put(TITLE, mArea.getName());
            dataJson.put(ADDRESS, mArea.getAddress());
            dataJson.put(MAPURL, mArea.getImagePath());
            dataJson.put(LATITUDE, mArea.getLatLon().getLatitude());
            dataJson.put(LONGITUDE, mArea.getLatLon().getLongitude());
            data = dataJson.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        TIMCustomElem elem = new TIMCustomElem();
        elem.setData(data.getBytes());
        message.addElement(elem);
    }

    //收礼
//    public CustomMessage(Type type, long packageId, int packageType, String senderName, String receiverName) {
//        this.type = type;
//        this.packageType = packageType;
//        this.packageId = packageId;
//        this.senderName = senderName;
//        this.receiverName = receiverName;
//        LogUtil.d("ailibin", "&&&&&senderName: " + senderName + " receiverName:" + receiverName);
//        message = new TIMMessage();
//        String data = "";
//        JSONObject dataJson = new JSONObject();
//        try {
//            dataJson.put(ACTION, type.value);
//            dataJson.put(PACKAGETYPE, packageType);
//            dataJson.put(PACKAGEID, packageId);
//            dataJson.put(SENDERNAME, senderName);
//            dataJson.put(RECEIVERNAME, receiverName);
//            data = dataJson.toString();
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        TIMCustomElem elem = new TIMCustomElem();
//        elem.setData(data.getBytes());
//        message.addElement(elem);
//    }


    public CustomMessage(Type type) {
        this.type = type;
        message = new TIMMessage();
        String data = "";
        JSONObject dataJson = new JSONObject();
        try {
            dataJson.put(ACTION, type.value);
            if (type == Type.TYPING) {
                //正在输入 TYPE_TYPING = 14
                dataJson.put("userAction", TYPE_TYPING);
                dataJson.put("actionParam", "EIMAMSG_InputStatus_Ing");
            } else {
                dataJson.put("userAction", TYPE_CUSTOM);
            }
            data = dataJson.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        TIMCustomElem elem = new TIMCustomElem();
        elem.setData(data.getBytes());
        message.addElement(elem);
    }


    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    private void parse(byte[] data) {
        type = LOCATION;
        try {
            String str = new String(data, "UTF-8");
            LogUtil.d("ailibin", "str: " + str);
            JSONObject jsonObj = new JSONObject(str);
            int action = jsonObj.optInt(ACTION, 1);
            int userAction = jsonObj.optInt("userAction", 1);
            type = Type.valueOf(action);
            if (userAction == 14) {
                type = Type.TYPING;
            } else if (userAction == 15) {
                type = Type.INVALID;
            }
            content = jsonObj.optString(CONTENT);
            title = jsonObj.optString(TITLE);
            address = jsonObj.optString(ADDRESS);
            mapUrl = jsonObj.optString(MAPURL);
            latitude = jsonObj.optString(LATITUDE);
            longitude = jsonObj.optString(LONGITUDE);
            LogUtil.d("ailibin", "latitude: " + latitude + "longitude: " + longitude);

//            LogUtil.d("ailibin", "title: " + title + " address: " + address + " imagePath: " + imagePath);
            if (type == Type.TYPING) {
                String actionParam = jsonObj.getString("actionParam");
                if ("EIMAMSG_InputStatus_End".equals(actionParam)) {
                    type = Type.INVALID;
                }
            }
            LogUtil.w("data:" + str);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }


    private void loadImage(String path, ImageView imageView) {
        if (path.startsWith("http://")) {
            Glide.with(App.Companion.getContext()).load(path)/*.placeholder(R.drawable.img_default)
                    .error(R.drawable.img_default)*/
//                    .override(width, width)
                    .transform(new GlideRoundTransform(App.Companion.getContext(), 12))
                    .into(imageView);

        } else {
            Glide.with(App.Companion.getContext()).load(new File(path))/*.placeholder(R.drawable.img_default)
                    .error(R.drawable.img_default)*/
//                    .override(width, width)
                    .transform(new GlideRoundTransform(App.Companion.getContext(), 15))
                    .into(imageView);
        }

    }

    /**
     * 显示消息
     *
     * @param viewHolder 界面样式
     * @param context    显示消息的上下文
     */
    @Override
    public void showMessage(final ChatAdapter.ViewHolder viewHolder, final Context context) {
        clearView(viewHolder);
        if (checkRevoke(viewHolder)) {
            return;
        }
        viewHolder.sendStatus.setVisibility(View.GONE);
        //接受消息回调界面默认消失掉
        viewHolder.ll_item_custom_message.setVisibility(View.GONE);
        switch (type) {
            case LOCATION:
                //发送成功后,位置点击事件
                if (message.isSelf()) {
                    //自己的是右边的布局
                    viewHolder.tv_right_item_location_custom_message_title.setText(title);
                    viewHolder.tv_right_item_location_custom_message_content.setText(address);
                    loadImage(mapUrl, viewHolder.iv_right_item_location_custom_message_mapView);
                } else {
                    //别人的布局
                    viewHolder.tv_left_item_location_custom_message_title.setText(title);
                    viewHolder.tv_left_item_location_custom_message_content.setText(address);
                    loadImage(mapUrl, viewHolder.iv_left_item_location_custom_message_mapView);
                }
                LatLng latLng = null;
                if (!TextUtils.isEmpty(latitude) && !TextUtils.isEmpty(longitude)) {
                    double DLatitude = Double.parseDouble(latitude);
                    double DLongitude = Double.parseDouble(longitude);
                    latLng = new LatLng(DLatitude, DLongitude);
                } else {
                    latLng = new LatLng(23.130561, 113.240655);
                }

                if (!message.isSelf()) {
                    //不是自己(这里传一个经度和纬度对象过去)
                    setLocationEvent(viewHolder, viewHolder.include_left_location_container, latLng, context, title, address);
                } else {
                    setLocationEvent(viewHolder, viewHolder.include_right_location_container, latLng, context, title, address);
                }
                break;
            default:
                break;
        }
    }

    /**
     * 设置点击地理位置消息的事件
     *
     * @param viewHolder
     */
    private void setLocationEvent(final ChatAdapter.ViewHolder viewHolder, View parentView, final LatLng latLng,
                                  final Context context, final String title, final String address) {
        parentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //跳转到地图中去(可以进行缩放)
                Intent intent = new Intent(context, LocationMessageActivity.class);
                intent.putExtra("LatLng", latLng);
                intent.putExtra("title", title);
                intent.putExtra("address", address);
                context.startActivity(intent);
            }
        });
    }


    private OnAcceptListener onAcceptListener;

    public void setOnAcceptListener(OnAcceptListener onAcceptListener) {
        this.onAcceptListener = onAcceptListener;
    }

    public interface OnAcceptListener {
        void onAccept(long packageId, int packageType, String senderName, int action);
    }


    /**
     * 获取消息摘要
     */
    @Override
    public String getSummary() {
        switch (type) {
            case LOCATION:
                //位置消息
                return "位置消息";
            case TYPING:
                //提示对方正在输入
                return "对方正在输入...";
            default:
                return "系统消息";
        }
    }

    /**
     * 保存消息或消息文件
     */
    @Override
    public void save() {

    }

    public enum Type {

        /**
         * 位置类型
         */
        LOCATION(1),
        /**
         * 正在输入类型
         */
        TYPING(14),
        /**
         * 无效类型
         */
        INVALID(15);

        Type(int value) {
            this.value = value;
        }

        private int value;

        public int getValue() {
            return value;
        }

        public static int getValues(Type status) {
            return status.getValue();
        }

        /**
         * 通过int值获取枚举类，请使用这个方法valueOf(1)
         * valueOf(String value) 因为不能重写，所以可以使用valueOf(“INVITE“), 而不能使用valueOf("1")
         *
         * @param value 传入的值
         * @return 对应的枚举类
         */
        public static Type valueOf(int value) {
            for (int i = 0; i < values().length; i++) {
                if (values()[i].getValue() == value) {
                    return values()[i];
                }
            }
            //默认返回15
            return Type.INVALID;
        }
    }

}
