package com.aiitec.hiim.im.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.aiitec.hiim.R;
import com.aiitec.hiim.adapter.CommonRecyclerViewAdapter;
import com.aiitec.hiim.adapter.CommonRecyclerViewHolder;
import com.aiitec.hiim.im.entity.ListUser;
import com.aiitec.hiim.utils.GlideImgManager;

import java.util.List;

/**
 * 好友关系链管理消息adapter
 *
 * @author ailibin
 * @date 2018/1/5
 */
public class NewFriendAdapter extends CommonRecyclerViewAdapter<ListUser> {

    /**
     * Constructor
     *
     * @param context The current context.
     * @param objects The objects to represent in the ListView.
     */
    public NewFriendAdapter(Context context, List<ListUser> objects) {
        super(context, objects);
    }


    @Override
    public void convert(CommonRecyclerViewHolder h, final ListUser entity, final int position) {
        ImageView avatar = h.getView(R.id.avatar);
        TextView name = h.getView(R.id.name);
        View line_item = h.getView(R.id.line_item);
        TextView status = h.getView(R.id.status);
        if (position == data.size() - 1) {
            line_item.setVisibility(View.GONE);
        } else {
            line_item.setVisibility(View.VISIBLE);
        }
        Resources res = context.getResources();
        final ListUser data = getItem(position);
        GlideImgManager.load(context, data.getImagePath(), R.drawable.my_icon_default_avatar2x, avatar, GlideImgManager.GlideType.TYPE_CIRCLE);
        name.setText(data.getNickname());
        status.setTextColor(res.getColor(R.color.black5));
        int relationship = data.getRelationship();

        switch (relationship) {
            case 2:
                status.setText(res.getString(R.string.newfri_agree));
                //设置字体颜色
                status.setTextColor(Color.parseColor("#DE7FEF"));
                break;
            case 1:
                status.setText(res.getString(R.string.newfri_wait));
                status.setBackgroundResource(R.color.transparent);
                break;
            case 3:
                status.setText(res.getString(R.string.newfri_accept));
                //设置字体颜色
                status.setTextColor(Color.parseColor("#999999"));
                status.setBackgroundResource(R.color.transparent);
                break;
            default:
                break;
        }
    }

    @Override
    public int getLayoutViewId(int viewType) {
        return R.layout.item_new_fiend;
    }

    private OnAgreeListener onAgreeListener;

    public void setOnAgreeListener(OnAgreeListener onAgreeListener) {
        this.onAgreeListener = onAgreeListener;
    }

    public interface OnAgreeListener {
        /**
         * 同意
         *
         * @param position
         * @param id
         */
        void onAgree(int position, long id);
    }

}
