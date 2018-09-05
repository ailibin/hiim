package com.aiitec.hiim.im.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aiitec.hiim.R;
import com.aiitec.hiim.base.BaseAbsListViewAdapter;
import com.aiitec.hiim.im.entity.OperationContent;

import java.util.List;

/**
 * @Author afb
 * @Version 1.0
 * Created on 2017/9/19
 * @effect 用户操作弹窗列表的适配器
 */

public class MemberOperationAdapter extends BaseAbsListViewAdapter {

    public MemberOperationAdapter(Context context, List listData) {
        super(context, listData);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MemberOperationViewHolder holder = null;
        if (convertView == null) {
            convertView = loadView(R.layout.item_for_member_operation_pop);
            holder = new MemberOperationViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (MemberOperationViewHolder) convertView.getTag();
        }
        OperationContent userOperation = (OperationContent) listData.get(position);
        holder.tvUserOperationName.setText(userOperation.getContent());
        return convertView;
    }

    class MemberOperationViewHolder {
        TextView tvUserOperationName;

        MemberOperationViewHolder(View view) {
            tvUserOperationName = view.findViewById(R.id.tv_user_operation_name);
        }
    }
}
