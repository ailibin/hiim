package com.aiitec.hiim.im.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.aiitec.hiim.R;
import com.aiitec.hiim.adapter.CommonRecyclerViewAdapter;
import com.aiitec.hiim.adapter.CommonRecyclerViewHolder;
import com.aiitec.hiim.im.entity.Contact;
import com.aiitec.hiim.utils.GlideImgManager;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ailibin
 * @version 1.0
 * createTime 2018/1/8.
 */

public class ContactAdapter extends CommonRecyclerViewAdapter<Contact> implements SectionIndexer {

    /**
     * 自定义item的布局
     */
    private int itemLayoutId;

    private boolean isChecked = false;

    /**
     * 已经选择的联系人数据
     */
    private List<Contact> mSelectItems = new ArrayList<>();

    /**
     * 默认多选(0 多选 1 单选)
     */
    private int checkType = 0;

    private boolean isVisible = true;

    //单选按钮是否显示
    private boolean isSingleVisible = false;

    public ContactAdapter(Context context, List<Contact> data) {
        super(context, data);
    }

    public void setItemLayoutId(int itemLayoutId) {
        this.itemLayoutId = itemLayoutId;
    }

    public void updateData(List<Contact> selectItems) {
        mSelectItems.clear();
        mSelectItems.addAll(selectItems);
    }

    /**
     * 设置是单选还是多选
     */
    public void setCheckType(int checkType) {
        this.checkType = checkType;
    }

    /**
     * 设置多选图片是否显示
     */
    public void setCheckedImageVisible(boolean isVisible) {
        this.isVisible = isVisible;
    }

    /**
     * 设置单选图片是否显示
     */
    public void setCheckedSingleImageVisible(boolean isSingleVisible) {
        this.isSingleVisible = isSingleVisible;
    }

    @Override
    public void convert(final CommonRecyclerViewHolder h, final Contact entity, final int position) {
        TextView tv_item_contact_letter = h.getView(R.id.tv_item_contact_letter);
        ImageView civ_item_contact_avatar = h.getView(R.id.civ_item_contact_avatar);
        TextView tv_item_contact_name = h.getView(R.id.tv_item_contact_name);
        View line_item = h.getView(R.id.line_item);
        //根据position获取分类的首字母的char ascii值
        int section = getSectionForPosition(position);

        //如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
        if (position == getPositionForSection(section)) {
            tv_item_contact_letter.setVisibility(View.VISIBLE);
            tv_item_contact_letter.setText(entity.getSortLetters());
        } else {
            tv_item_contact_letter.setVisibility(View.GONE);
        }
        char thisLetter = data.get(position).getSortLetters().charAt(0);
        //最后一条不显示线
        if (position == data.size() - 1) {
            line_item.setVisibility(View.GONE);
        } else {
            char nextLetter = data.get(position + 1).getSortLetters().charAt(0);

            if (thisLetter == nextLetter) {
                //如果两条拼音的首字母一样，就显示线
                line_item.setVisibility(View.VISIBLE);
            } else {
                line_item.setVisibility(View.GONE);
            }
        }

        switch (itemLayoutId) {
            case R.layout.item_contact:
                break;
//            case R.layout.afb_item_contact_self_define:
//                //选择好友布局等多种情况布局
//                final ImageView checkView = h.getView(R.id.cb_check_for_select_friend);
//                final ImageView singleCheckView = h.getView(R.id.single_check_for_select_friend);
//                if (isSingleVisible) {
//                    //单选按钮显示
//                    if (entity.isSelected()) {
//                        singleCheckView.setVisibility(View.VISIBLE);
//                        singleCheckView.setImageResource(R.drawable.common_btn_choose_pressed2x);
//                    } else {
//                        singleCheckView.setVisibility(View.GONE);
//                    }
//                } else {
//                    singleCheckView.setVisibility(View.GONE);
//                }
//
//                if (!isVisible) {
//                    checkView.setVisibility(View.GONE);
//                } else {
//                    checkView.setVisibility(View.VISIBLE);
//                }
//                checkView.setSelected(entity.isSelected());
//                break;
            default:
                break;
        }
//        LogUtil.d("ailibin", "contactAdapter: " + entity.getImagePath());
        GlideImgManager.load(context, entity.getImagePath(), R.drawable.my_icon_default_avatar2x,
                civ_item_contact_avatar, GlideImgManager.GlideType.TYPE_CIRCLE);
        //这里如果设置了备注,就用用户的备注信息,否则就用昵称
        if (!TextUtils.isEmpty(entity.getAlias())) {
            tv_item_contact_name.setText(entity.getAlias());
        } else {
            tv_item_contact_name.setText(entity.getName());
        }

    }


    @Override
    public int getLayoutViewId(int viewType) {
        return itemLayoutId;
    }

    @Override
    public Object[] getSections() {
        return new Object[0];
    }

    @Override
    public int getPositionForSection(int sectionIndex) {
        for (int i = 0; i < data.size(); i++) {
            String sortStr = data.get(i).getSortLetters();
            char firstChar = sortStr.toUpperCase().charAt(0);
            if (firstChar == sectionIndex) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public int getSectionForPosition(int position) {
        return data.get(position).getSortLetters().charAt(0);
    }

}
