package com.aiitec.hiim.im.adapter;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.aiitec.hiim.R;
import com.aiitec.hiim.adapter.CommonRecyclerViewAdapter;
import com.aiitec.hiim.adapter.CommonRecyclerViewHolder;
import com.aiitec.hiim.im.entity.Image;
import com.aiitec.hiim.utils.GlideImgManager;
import com.aiitec.hiim.utils.ScreenUtils;

import java.io.File;
import java.util.List;


/**
 * @author Anthony
 *         createTime 2017/10/13 18:43
 * @version 1.0
 */
public class PhotoAdapter extends CommonRecyclerViewAdapter<Image> {

    /**
     * 选择的图片个数
     */
    public int selectNum;
    /**
     * 选中的index
     */
    public int selectIndex = -1;

    private int width;

    public PhotoAdapter(final Context context, List<Image> datas) {
        super(context, datas);
        width = ScreenUtils.getScreenWidth() / 3 - ScreenUtils.dip2px(context, 9);

    }

    @Override
    public void convert(CommonRecyclerViewHolder h, final Image entity, final int position) {
        View layout = h.getView(R.id.layout_photo);

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(width, width);
        layout.setLayoutParams(params);
        File file = new File(entity.getPath());
        if (position != 0) {
            View photo_iv_selected = h.getView(R.id.photo_iv_selected);
            if (entity.isSelected()) {
                photo_iv_selected.setVisibility(View.VISIBLE);
            } else {
                photo_iv_selected.setVisibility(View.GONE);
            }
            ImageView iv = (ImageView) layout;
            if (file.exists()) {

                iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
                GlideImgManager.loadFile(context, entity.getPath(), R.color.gray_line, R.color.gray_line, iv);
            } else {
                iv.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                iv.setImageResource(R.drawable.my_icon_default_avatar2x);
            }
        } else {
            ImageView photo_iv_camera = h.getView(R.id.photo_iv_camera);
            photo_iv_camera.setImageResource(R.drawable.chat_btn_takephoto2x);
        }


    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return 1;
        } else {
            return 2;
        }
    }

    @Override
    public int getLayoutViewId(int viewType) {
        switch (viewType) {
            case 1:
                return R.layout.adapter_photo_camera;
            case 2:
                return R.layout.adapter_photo;
            default:
                return R.layout.adapter_photo;
        }
    }

    public int getSelectNum() {
        return selectNum;
    }

    public void setSelectNum(int selectNum) {
        this.selectNum = selectNum;
    }
}
