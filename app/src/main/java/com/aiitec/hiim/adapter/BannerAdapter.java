
package com.aiitec.hiim.adapter;

import android.content.Context;
import android.os.Build;
import android.os.Parcelable;
import android.support.annotation.RequiresApi;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.aiitec.hiim.R;
import com.aiitec.hiim.im.utils.LogUtil;
import com.aiitec.hiim.model.Ad;

import java.util.ArrayList;
import java.util.List;


/**
 * 广告轮播图Adapter
 *
 * @author Anthony
 * createTime 2017-11-18
 */
public class BannerAdapter extends PagerAdapter {
    private View rowView;
    private Context context;
    private SparseArray<View> rowViews = new SparseArray<>();
    private List<Ad> list;

    public BannerAdapter(Context context, List<String> list) {
        this.context = context;
        this.list = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            Ad ad = new Ad();
            ad.setImagePath(list.get(i));
            this.list.add(ad);
        }
    }

    public BannerAdapter(Context context, ArrayList<Ad> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public void destroyItem(View arg0, int arg1, Object arg2) {
        ((ViewPager) arg0).removeView((View) arg2);
    }


    @Override
    public int getCount() {
        return list.size();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public Object instantiateItem(View arg0, final int arg1) {
        BannerHolder holder = null;
        rowView = rowViews.get(arg1);
        if (rowView == null) {
            rowView = LayoutInflater.from(context).inflate(R.layout.banner_item, null);
            holder = new BannerHolder(rowView);
            rowView.setTag(holder);
        } else {
            holder = (BannerHolder) rowView.getTag();
        }
        String adImg = list.get(arg1).getImagePath();

        LogUtil.e("ailibin", "adImg: " + adImg.toString());
//        GlideImgManager.load(context, adImg).placeholder(R.color.gray7).centerCrop().into(holder.getImageView());


        /**    监听点击广告图片后的操作，一般只做网页跳转	*/
        rowView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(list.get(arg1));
                }

            }
        });
        rowViews.put(arg1, rowView);
        ((ViewPager) arg0).addView(rowView, 0);
        return rowView;
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == (arg1);
    }

    @Override
    public void restoreState(Parcelable arg0, ClassLoader arg1) {

    }

    @Override
    public Parcelable saveState() {
        return null;
    }

    @Override
    public void startUpdate(View arg0) {

    }

    private class BannerHolder {
        private View view;
        private ImageView imageview;

        private BannerHolder(View v) {
            view = v;
        }

        ImageView getImageView() {
            if (imageview == null) {
                imageview = (ImageView) view.findViewById(R.id.banner);
            }
            return imageview;
        }
    }

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(Ad ad);
    }
}
