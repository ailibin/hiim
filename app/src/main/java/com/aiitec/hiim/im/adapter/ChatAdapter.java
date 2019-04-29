package com.aiitec.hiim.im.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aiitec.hiim.R;
import com.aiitec.hiim.im.model.CustomMessage;
import com.aiitec.hiim.im.model.Message;
import com.aiitec.hiim.im.utils.AiiUtil;
import com.aiitec.hiim.im.utils.LogUtil;
import com.aiitec.hiim.utils.GlideImgManager;
import com.aiitec.hiim.utils.ScreenUtils;
import com.tencent.imsdk.TIMConversationType;
import com.tencent.imsdk.TIMElemType;
import com.tencent.imsdk.TIMSoundElem;

import java.util.ArrayList;
import java.util.List;


/**
 * 聊天界面adapter
 *
 * @author afb
 */
public class ChatAdapter extends ArrayAdapter<Message> {

    private final String TAG = "ChatAdapter";

    private int resourceId;
    private View view;
    private ViewHolder viewHolder;
    private int dp5;
    private int dp10;
    private String otherAvatar;
    private int serviceAvatar;
    private TIMConversationType type;
    private boolean mIsShowEmpty = false;
    private List<String> mEmptyList = new ArrayList<>();

    public void setOtherAvatar(String otherAvatar) {
        this.otherAvatar = otherAvatar;
    }

    //设置客服头像
    public void setServiceAvatar(int serviceAvatar) {
        this.serviceAvatar = serviceAvatar;
    }

    //设置聊天类型(是群聊还是单聊)
    public void setConversationType(TIMConversationType type) {
        this.type = type;
    }

    /**
     * Constructor
     *
     * @param context  The current context.
     * @param resource The resource ID for a layout file containing a TextView to use when
     *                 instantiating views.
     * @param objects  The objects to represent in the ListView.
     */
    public ChatAdapter(Context context, int resource, List<Message> objects) {
        super(context, resource, objects);
        resourceId = resource;
        dp5 = ScreenUtils.dip2px(context, 5);
        dp10 = ScreenUtils.dip2px(context, 10);
    }


    public void setEmptyTeamMessage(boolean isShowEmpty) {
        this.mIsShowEmpty = isShowEmpty;
    }

    public void updateEmptyData(List<String> emptyList) {
        LogUtil.d("emptyList:" + emptyList.toString());
//        teamEmptyAdapter.updateData(emptyList);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView != null) {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        } else {
            view = LayoutInflater.from(getContext()).inflate(resourceId, null);
            viewHolder = new ViewHolder();
            viewHolder.leftMessage = view.findViewById(R.id.leftMessage);
            viewHolder.rightMessage = view.findViewById(R.id.rightMessage);
            viewHolder.leftPanel = view.findViewById(R.id.leftPanel);
            viewHolder.rightPanel = view.findViewById(R.id.rightPanel);
            viewHolder.sending = view.findViewById(R.id.sending);
            viewHolder.error = view.findViewById(R.id.sendError);
            viewHolder.voiceUnread = view.findViewById(R.id.iv_voice_unread);
            viewHolder.sender = view.findViewById(R.id.sender);
            viewHolder.sendStatus = view.findViewById(R.id.sendStatus);
            viewHolder.rightDesc = view.findViewById(R.id.rightDesc);
            viewHolder.systemMessage = view.findViewById(R.id.systemMessage);
            viewHolder.ll_item_custom_message = view.findViewById(R.id.ll_item_custom_message);
//            viewHolder.ll_item_custom_empty_message = view.findViewById(R.id.ll_item_custom_empty_message);
            viewHolder.tv_item_custom_message_content = view.findViewById(R.id.tv_item_custom_message_content);
//            viewHolder.btn_item_custom_message_join = view.findViewById(R.id.btn_item_custom_message_join);
            viewHolder.tv_right_duration = view.findViewById(R.id.tv_right_duration);
            viewHolder.tv_left_duration = view.findViewById(R.id.tv_left_duration);
            viewHolder.leftAvatar = view.findViewById(R.id.leftAvatar);
            viewHolder.rightAvatar = view.findViewById(R.id.rightAvatar);

            //定位相关
            viewHolder.include_left_location_container = view.findViewById(R.id.include_left_location_container);
            viewHolder.include_right_location_container = view.findViewById(R.id.include_right_location_container);
            //这里左右两边都要有,所以要多写一遍
            viewHolder.tv_right_item_location_custom_message_title = viewHolder.include_right_location_container.findViewById(R.id.tv_item_location_custom_message_title);
            viewHolder.tv_right_item_location_custom_message_content = viewHolder.include_right_location_container.findViewById(R.id.tv_item_location_custom_message_content);
            viewHolder.tv_left_item_location_custom_message_title = viewHolder.include_left_location_container.findViewById(R.id.tv_item_location_custom_message_title);
            viewHolder.tv_left_item_location_custom_message_content = viewHolder.include_left_location_container.findViewById(R.id.tv_item_location_custom_message_content);
            viewHolder.iv_left_item_location_custom_message_mapView = viewHolder.include_left_location_container.findViewById(R.id.iv_item_location_custom_message_mapView);
            viewHolder.iv_right_item_location_custom_message_mapView = viewHolder.include_right_location_container.findViewById(R.id.iv_item_location_custom_message_mapView);

            view.setTag(viewHolder);
        }

        if (position < getCount()) {
            final Message data = getItem(position);
            RelativeLayout bubbleView = null;
            TextView tv_duration = null;
            //未读消息图表
            ImageView voiceUnread = viewHolder.voiceUnread;
            if (data.getMessage().isSelf()) {
                if (data instanceof CustomMessage) {
                    if (((CustomMessage) data).getType() == CustomMessage.Type.LOCATION) {
                        //右边加载地图的容器显示
                        viewHolder.include_right_location_container.setVisibility(View.VISIBLE);
                        viewHolder.rightAvatar.setVisibility(View.VISIBLE);
                    }
                } else {
                    //普通的消息
                    viewHolder.include_right_location_container.setVisibility(View.GONE);
                    viewHolder.rightAvatar.setVisibility(View.VISIBLE);
                }
                bubbleView = viewHolder.rightMessage;
                tv_duration = viewHolder.tv_right_duration;
                String imagePath = "";
//                if (Constants.INSTANCE.getUser() != null) {
//                    imagePath = Constants.INSTANCE.getUser().getImagePath();
//                }
                GlideImgManager.load(getContext(), imagePath, R.drawable.my_icon_default_avatar2x, viewHolder.rightAvatar, GlideImgManager.GlideType.TYPE_CIRCLE);
            } else {
                if (data instanceof CustomMessage) {
                    if (((CustomMessage) data).getType() == CustomMessage.Type.LOCATION) {
                        //地址类型数据(左边容器选择,别人发送的消息)
                        viewHolder.include_left_location_container.setVisibility(View.VISIBLE);
                        viewHolder.leftAvatar.setVisibility(View.VISIBLE);
                    }
                } else {
                    //消失自定义的布局
                    viewHolder.include_left_location_container.setVisibility(View.GONE);
                    viewHolder.leftAvatar.setVisibility(View.VISIBLE);
                }
                bubbleView = viewHolder.leftMessage;
                tv_duration = viewHolder.tv_left_duration;
//                voiceUnread = viewHolder.voiceUnread;
                data.getMessage().getConversation().getPeer();
                if (serviceAvatar != 0 & TextUtils.isEmpty(otherAvatar)) {
                    //客服头像
                    GlideImgManager.load(getContext(), "", serviceAvatar, viewHolder.leftAvatar, GlideImgManager.GlideType.TYPE_CIRCLE);
                } else {
                    //2.方法二(这个也不靠谱)
                    if (type == TIMConversationType.C2C) {
                        GlideImgManager.load(getContext(), otherAvatar, R.drawable.my_icon_default_avatar2x, viewHolder.leftAvatar, GlideImgManager.GlideType.TYPE_CIRCLE);
                    } else {
                        //群聊
                        String avatar = AiiUtil.getString(getContext(), "avatar_" + data.getSender());
                        GlideImgManager.load(getContext(), avatar, R.drawable.my_icon_default_avatar2x, viewHolder.leftAvatar, GlideImgManager.GlideType.TYPE_CIRCLE);
                    }
                }
            }

            //设置消息的背景
            if (data.getMessage().getElement(0).getType() == TIMElemType.Image) {
                bubbleView.setBackgroundColor(Color.TRANSPARENT);
                bubbleView.setPadding(0, 0, 0, 0);
                tv_duration.setVisibility(View.GONE);
            } else if (data instanceof CustomMessage) {
                //自定义消息设置背景透明色
                bubbleView.setBackgroundColor(Color.TRANSPARENT);
                bubbleView.setPadding(0, 0, 0, 0);
                tv_duration.setVisibility(View.GONE);
            } else {
                if (data.getMessage().isSelf()) {
                    bubbleView.setBackgroundResource(R.drawable.chat_img_bg_talk_pink2x);
                } else {
                    bubbleView.setBackgroundResource(R.drawable.chat_img_bg_talk_gray2x);
                }
                bubbleView.setPadding(dp10, dp5, dp10, dp5);
                if (data.getMessage().getElement(0).getType() == TIMElemType.Sound) {
                    long duration = ((TIMSoundElem) data.getMessage().getElement(0)).getDuration();
                    tv_duration.setText("" + String.valueOf(duration) + "″");
                    tv_duration.setVisibility(View.VISIBLE);
                } else {
                    tv_duration.setVisibility(View.GONE);
                    //设置未读图标消失
                    voiceUnread.setVisibility(View.GONE);
                }
            }
            if (data.isSendFail()) {
                viewHolder.error.setVisibility(View.VISIBLE);
            } else {
                viewHolder.error.setVisibility(View.GONE);
            }
            viewHolder.leftAvatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //这里要传一个id过去
                    if (onAvatarClickListener != null) {
                        onAvatarClickListener.onClickOtherAvatar(data.getSender());
                    }
                }
            });
            viewHolder.rightAvatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onAvatarClickListener != null) {
                        onAvatarClickListener.onClickSelfAvatar();
                    }
                }
            });

            viewHolder.error.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    resetMessage(data);
                }
            });
            data.showMessage(viewHolder, getContext());
        }
        return view;
    }

    private void resetMessage(Message data) {
        if (onResendListener != null) {
            onResendListener.onResend(data);
        }
    }


    public class ViewHolder {
        public RelativeLayout leftMessage;
        public RelativeLayout rightMessage;
        public RelativeLayout leftPanel;
        public RelativeLayout rightPanel;
        public RelativeLayout sendStatus;
        public LinearLayout ll_item_custom_message;
        public LinearLayout ll_item_custom_empty_message;
        public TextView tv_item_custom_message_content;
        public Button btn_item_custom_message_join;
        public ProgressBar sending;
        public ImageView error;
        public ImageView voiceUnread;
        public ImageView leftAvatar;
        public ImageView rightAvatar;
        public TextView sender;
        public TextView systemMessage;
        public TextView tv_right_duration;
        public TextView tv_left_duration;
        public TextView rightDesc;
//        public ListView empty_listView;
        /**
         * 定位相关
         */
        public TextView tv_left_item_location_custom_message_title;
        public TextView tv_left_item_location_custom_message_content;
        public ImageView iv_left_item_location_custom_message_mapView;
        public TextView tv_right_item_location_custom_message_title;
        public TextView tv_right_item_location_custom_message_content;
        public ImageView iv_right_item_location_custom_message_mapView;
        public View include_left_location_container;
        public View include_right_location_container;

//        //礼物相关
//        public ImageView iv_left_item_gift_custom_message;
//        public ImageView iv_right_item_gift_custom_message;
//        public View include_left_gift_container;
//        public View include_right_gift_container;
//
//        //商品相关
//        public LinearLayout ll_item_custom_goods_message;
//        public ImageView iv_item_goods_icon_custom_message;
//        public TextView tv_item_goods_name_custom_message;
//        public TextView tv_item_goods_price_custom_message;
//
//        //订单相关
//        public LinearLayout ll_item_custom_order_message;
//        public ImageView iv_item_order_icon_custom_message;
//        public TextView tv_item_order_num_custom_message;
//        public TextView tv_item_order_price_custom_message;
//        public TextView tv_item_order_time_custom_message;
    }

    private OnResendListener onResendListener;

    public void setOnResendListener(OnResendListener onResendListener) {
        this.onResendListener = onResendListener;
    }

    public interface OnResendListener {
        void onResend(Message data);
    }

    public interface OnAvatarClickListener {

        /**
         * 点击左边的的头像
         */
        void onClickSelfAvatar();

        /**
         * 点击其它人的头像(传一个id)
         */
        void onClickOtherAvatar(String identify);
    }

    private OnAvatarClickListener onAvatarClickListener;

    public void setOnAvatarClickListener(OnAvatarClickListener onAvatarClickListener) {
        this.onAvatarClickListener = onAvatarClickListener;
    }
}
