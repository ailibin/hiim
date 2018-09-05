package com.aiitec.hiim.im.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.aiitec.hiim.R;
import com.aiitec.hiim.im.model.Conversation;
import com.aiitec.hiim.im.utils.TimeUtil;
import com.aiitec.hiim.utils.GlideImgManager;
import com.aiitec.openapi.utils.LogUtil;
import com.daimajia.swipe.SimpleSwipeListener;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.BaseSwipeAdapter;
import com.google.android.flexbox.FlexboxLayout;
import com.tencent.imsdk.TIMConversationType;

import java.util.List;

/**
 * 会话界面adapter
 *
 * @author ailibin
 */
public class ConversationAdapter extends BaseSwipeAdapter {

    private List<Conversation> datas;
    private Context context;
    private OnDeleteListener onDeleteListener;

    public void setOnDeleteListener(OnDeleteListener onDeleteListener) {
        this.onDeleteListener = onDeleteListener;
    }

    public interface OnDeleteListener {
        void onDelete(int position, long unReadNum);
    }

    /**
     * Constructor
     *
     * @param context The current context.
     * @param objects The objects to represent in the ListView.
     */
    public ConversationAdapter(Context context, List<Conversation> objects) {
        super();
        this.context = context;
        datas = objects;
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe;
    }

    @Override
    public View generateView(final int position, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_conversation, null);
        final SwipeLayout swipeLayout = view.findViewById(getSwipeLayoutResourceId(position));
        //设置没有侧滑,因为微信没有侧滑删除
        swipeLayout.setSwipeEnabled(false);
        swipeLayout.addSwipeListener(new SimpleSwipeListener() {
            @Override
            public void onOpen(SwipeLayout layout) {
            }
        });

        view.findViewById(R.id.tv_item_delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                swipeLayout.close();
                if (onDeleteListener != null) {
                    onDeleteListener.onDelete(position, getItem(position).getUnreadNum());
                }
            }
        });
        return view;
    }

    @Override
    public void fillValues(int position, View convertView) {

        final TextView tvName = convertView.findViewById(R.id.tv_user_name_for_chat_item);
        final ImageView avatar = convertView.findViewById(R.id.iv_user_icon_item_for_chat_item);
        TextView lastMessage = convertView.findViewById(R.id.tv_last_message_for_chat_item);
        TextView time = convertView.findViewById(R.id.tv_time_for_chat_item);
        TextView unread = convertView.findViewById(R.id.tv_unread_for_chat_item);
        View line = convertView.findViewById(R.id.view_dividing_line);
        //试试google新布局
        FlexboxLayout flexboxLayout = convertView.findViewById(R.id.flexbox_layout);

        if (position == datas.size() - 1) {
            //最后一个item的分割线不显示
            line.setVisibility(View.GONE);
            flexboxLayout.setShowDividerHorizontal(FlexboxLayout.SHOW_DIVIDER_NONE);
        } else {
            flexboxLayout.setShowDividerHorizontal(FlexboxLayout.SHOW_DIVIDER_END);
            line.setVisibility(View.VISIBLE);
        }

        final Conversation data = getItem(position);
        String name = data.getName();
        //会话类型
        TIMConversationType type = data.getType();
        tvName.setText(name);
//        if (type == TIMConversationType.C2C) {
//            //如果是单聊
//            final String dbName = AiiUtil.getString(context, "nickname_" + data.getIdentify(), name);
//            if (TextUtils.isEmpty(dbName)) {
//                //名字为空,从存储的地方获取昵称
//                tvName.setText(name);
//            }
//            //以IM开头的(这里如果是群会话),这个名称显示有问题
//            if (BaseUtil.isAllNum(dbName) || name.startsWith("IM")) {
//                List<String> identifiers = new ArrayList<>();
//                identifiers.add(dbName);
//                //这里还是请求用户详情
//                TIMFriendshipManager.getInstance().getUsersProfile(identifiers, new TIMValueCallBack<List<TIMUserProfile>>() {
//                    @Override
//                    public void onError(int code, String desc) {
//                        tvName.setText(dbName);
//                    }
//
//                    @Override
//                    public void onSuccess(List<TIMUserProfile> result) {
//                        //这里先设置成昵称吧,然后有的地方昵称有的地方备注,很奇怪
//                        if (!TextUtils.isEmpty(result.get(0).getNickName())) {
//                            tvName.setText(result.get(0).getNickName());
//                        } else {
//                            tvName.setText(AiiUtil.getString(context, "nickname_" + data.getIdentify(), dbName));
//                        }
//                    }
//                });
//            } else {
//                //先设置成昵称(如果后面要改就改过来)
//                tvName.setText(AiiUtil.getString(context, "nickname_" + data.getIdentify(), name));
//            }
//        } else {
//            //群聊
//            if (name.startsWith("IM") || TextUtils.isEmpty(name)) {
//                //获取群identify
//                String identify = data.getIdentify();
//                final List<String> newName = new ArrayList<>();
//                newName.clear();
//                newName.add(identify);
//                List<String> groupIdentifys = new ArrayList<>();
//                groupIdentifys.clear();
//                groupIdentifys.add(identify);
//                //需要获取信息的群组 ID 列表
//                TIMGroupManagerExt.getInstance().getGroupDetailInfo(
//                        groupIdentifys,
//                        new TIMValueCallBack<List<TIMGroupDetailInfo>>() {
//                            @Override
//                            public void onError(int i, String s) {
//                                tvName.setText(newName.get(0));
//                            }
//
//                            @Override
//                            public void onSuccess(List<TIMGroupDetailInfo> timGroupDetailInfos) {
//                                //这里获取群名称
//                                if (timGroupDetailInfos != null) {
//                                    //只有一个群
//                                    newName.set(0, timGroupDetailInfos.get(0).getGroupName());
//                                }
//                                tvName.setText(newName.get(0));
//                            }
//                        });
//            } else {
//                tvName.setText(name);
//            }
//        }

        LogUtil.d("ailibin", "avatar: " + data.getAvatarUrl(context));
        GlideImgManager.load(context, data.getAvatarUrl(context), R.drawable.my_icon_default_avatar2x, avatar, GlideImgManager.GlideType.TYPE_CIRCLE);
        lastMessage.setText(data.getLastMessageSummary());
        time.setText(TimeUtil.getTimeStr(data.getLastMessageTime()));
        long unRead = data.getUnreadNum();
        if (unRead <= 0) {
            unread.setVisibility(View.INVISIBLE);
        } else {
            unread.setVisibility(View.VISIBLE);
            String unReadStr = String.valueOf(unRead);
            if (unRead < 10) {
                unread.setBackgroundResource(R.drawable.im_point1);
            } else {
                unread.setBackgroundResource(R.drawable.im_point2);
                if (unRead > 99) {
                    unReadStr = context.getResources().getString(R.string.time_more);
                }
            }
            unread.setText(unReadStr);
        }
    }


    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public Conversation getItem(int position) {
        return datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

}
