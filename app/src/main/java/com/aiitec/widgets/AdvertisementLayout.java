package com.aiitec.widgets;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.aiitec.hiim.R;
import com.aiitec.hiim.adapter.BannerAdapter;
import com.aiitec.hiim.model.Ad;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


/**
 * @author ailibin
 * @time 18/06/19
 */
public class AdvertisementLayout extends LinearLayout {

    private ViewPager viewPager;
    private LinearLayout group;
    private ImageView[] images;
    /**
     * 指示器
     */
    private LinearLayout indicatorLayout;
    /**
     * 是否显示指示器
     */
    private boolean isShowIndicator = true;
    private int currentItem = 0;
    /**
     * 轮播当前位置
     */
    private int currentPosition = 0;
    private Context context;
    private ScheduledExecutorService scheduledExecutorService;
    private int TIME_UNIT;
    int pageIndex = 1;
    private List<Ad> list;
    private float RATIO = 2f / 4;
    private int dp6;
    private int dp9;
    /***
     * 这个type = 2 是为了上课的广告用的，那个是圆角的imageView
     */
    private int type = 1;
    private indicatorClickListener mIndicatorClickListener;

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    private void initView(View view) {
        dp6 = dip2px(view.getContext(), 6);
        dp9 = dip2px(view.getContext(), 9);
        viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        //控制滑动速度
        ViewPagerUtils.smoothForViewPager(getContext(), viewPager, 666);
        indicatorLayout = view.findViewById(R.id.layout_viewpager_indicator);
    }

    public AdvertisementLayout(Context context) {
        super(context);
        this.context = context;
        LayoutInflater mInflater = LayoutInflater.from(context);
        View adview = mInflater.inflate(R.layout.layout_ad, null);
        initView(adview);
        setIndicatorCenter(Gravity.CENTER);
        addView(adview);
    }

    public AdvertisementLayout(Context context, int type) {
        super(context);
        this.context = context;
        this.type = type;
        LayoutInflater mInflater = LayoutInflater.from(context);
        View adview = mInflater.inflate(R.layout.layout_ad, null);
        initView(adview);
        setIndicatorCenter(Gravity.CENTER);
        addView(adview);
    }

    public AdvertisementLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        TypedArray params = context.obtainStyledAttributes(attrs, R.styleable.ad);
        RATIO = params.getFloat(R.styleable.ad_ratio, 0.5f);
        LayoutInflater mInflater = LayoutInflater.from(context);
        View adview = mInflater.inflate(R.layout.layout_ad, null);
        initView(adview);
        setIndicatorCenter(Gravity.CENTER);
        addView(adview);
        params.recycle();
    }


    public void setCircleGravity(int gravity) {
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) group.getLayoutParams();
        layoutParams.gravity = Gravity.CENTER | Gravity.BOTTOM;
        group.setLayoutParams(layoutParams);
        group.setGravity(gravity);
    }

    /**
     * 设置指示器居中，默认指示器在右方(在isShowIndicator为true的情况下设置)
     */
    public void setIndicatorCenter(int gravity) {

        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) indicatorLayout.getLayoutParams();
        layoutParams.gravity = Gravity.CENTER | Gravity.BOTTOM;
        layoutParams.bottomMargin = 12;
        indicatorLayout.setLayoutParams(layoutParams);
        indicatorLayout.setGravity(gravity);

    }


    /**
     * 设置指示器
     *
     * @param selectedPosition 默认指示器位置
     */
    private void setIndicator(int selectedPosition) {
        //selectedPosition=-1左滑的时候会闪退,就是这个参数变成了-1
        if (selectedPosition >= 0) {
            for (int i = 0; i < images.length; i++) {
                images[i].setBackgroundResource(R.drawable.common_icon_dot_empty);
            }
            if (images.length > selectedPosition) {
                images[selectedPosition].setBackgroundResource(R.drawable.common_icon_dot_fill);
            }
        }

    }

    /**
     * 设置指示器的点击事件
     *
     * @param mIndicatorClickListener
     */
    public void setmIndicatorClickListener(indicatorClickListener mIndicatorClickListener) {
        this.mIndicatorClickListener = mIndicatorClickListener;
    }

    /**
     * 指示器的点击事件
     */
    public interface indicatorClickListener {
        /**
         * 点击事件的回调方法
         *
         * @param view
         * @param position
         */
        void onIndicatorClick(View view, int position);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(getDefaultSize(0, widthMeasureSpec), getDefaultSize(0, heightMeasureSpec));
        // Children are just made to fill our space.
        int childWidthSize = getMeasuredWidth();
        //高度和宽度一样
        widthMeasureSpec = MeasureSpec.makeMeasureSpec(childWidthSize, MeasureSpec.EXACTLY);
        heightMeasureSpec = MeasureSpec.makeMeasureSpec((int) (childWidthSize * RATIO), MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @SuppressLint("NewApi")
    public AdvertisementLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

    }

    /**
     * 实例化，动态创建图片下方的小点
     *
     * @param num
     */
    private void initListNavigation(int num) {
        images = new ImageView[num];
        for (int i = 0; i < num; i++) {
            images[i] = new ImageView(context);

            LayoutParams params = new LayoutParams(10, 10);
            if (i == 0) {
                params.width = dp9;
                params.height = dp9;
            } else {
                params.width = dp6;
                params.height = dp6;
            }
            params.leftMargin = 6;
            params.rightMargin = 6;
            images[i].setLayoutParams(params);
            //暂时不要原点
            images[i].setBackgroundColor(Color.TRANSPARENT);
            group.addView(images[i]);
        }


    }


    /**
     * 实例化，动态创建图片下方的小点
     *
     * @param num
     */
    private void initListNavigation(int num, Context context) {
        if (isShowIndicator) {
            // 设置指示器
            images = new ImageView[num];
//            images = new ImageView[num - 2];
            indicatorLayout.removeAllViews();
            for (int i = 0; i < images.length; i++) {
                View view = LayoutInflater.from(context).inflate(
                        R.layout.a_cycle_view_viewpager_indicator, null);
                view.setTag(i);
                view.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = (int) v.getTag();
                        if (mIndicatorClickListener != null) {
                            mIndicatorClickListener.onIndicatorClick(v, position);
                        }
                    }
                });
                images[i] = view.findViewById(R.id.image_indicator);
                indicatorLayout.addView(view);
            }
        }
        if (isShowIndicator) {
            // 默认指向第一项，下方viewPager.setCurrentItem将触发重新计算指示器指向
            setIndicator(0);
        }
    }

    /**
     * 滑动监听
     */
    private class PagerListener implements OnPageChangeListener {
        /**
         * 滑动中
         *
         * @param arg0
         */
        @Override
        public void onPageScrollStateChanged(int arg0) {
            //0--->空闲，1--->是滑行中，2--->加载完毕
            switch (arg0) {
                case 0:
                case 1:
                    //当当前的页是第一张的时候就切成显示最后1张。
                    if (pageIndex == 0) {
                        pageIndex = list.size();
                        // 取消动画
                        viewPager.setCurrentItem(pageIndex, false);
                        //当当前的页是最后一张的时候就切成显示的第一张。
                    } else if (pageIndex == list.size() + 1) {
                        pageIndex = 1;
                        // 取消动画
                        viewPager.setCurrentItem(pageIndex, false);
                    } else {
                        // 动画
                        viewPager.setCurrentItem(pageIndex);
                    }
                    break;
                default:
                    break;
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageSelected(int arg0) {
            if (scheduledExecutorService != null) {
                scheduledExecutorService.shutdown();
            }
            scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
            scheduledExecutorService.scheduleAtFixedRate(new ViewPagerScrollTask(), TIME_UNIT, TIME_UNIT, TimeUnit.SECONDS);
            pageIndex = arg0;
            int position = arg0 - 1;
//            LogUtil.e("ailibin", "position****: " + position);
            if (isShowIndicator) {
                setIndicator(position);
            }
        }
    }


    private class ViewPagerScrollTask implements Runnable {

        @Override
        public void run() {
            synchronized (viewPager) {
//				currentItem = (currentItem + 1) % images.length;
                pageIndex++;
                if (pageIndex >= list.size() + 1) {
                    pageIndex = 1;
                }
                handler.obtainMessage().sendToTarget();
            }
        }

    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            viewPager.setCurrentItem(pageIndex);
//			setCurrentItem() ;
        }

        ;
    };

    /**
     * 广告调用接口
     *
     * @param num       集合的size
     * @param time_unit 自动滑动的时间（秒）
     * @param is_open   是否自动滑动
     * @param list      图片集合
     * @param ratio     这个是广告的宽高的比例(传-1就是默认0.5比例)
     */
    public void startAD(int num, int time_unit, boolean is_open, ArrayList<Ad> list, float ratio) {
        if (ratio != -1) {
            RATIO = ratio;
        }
        this.list = list;
        group.removeAllViews();
        if (is_open) {
            if (time_unit <= 0) {
                TIME_UNIT = 1;
            } else {
                TIME_UNIT = time_unit;
            }
        }
        initListNavigation(num);

        if (num <= 0) {
            findViewById(R.id.linear_ad).setVisibility(View.GONE);
        } else {
            findViewById(R.id.linear_ad).setVisibility(View.VISIBLE);
            BannerAdapter bannerAdapter = new BannerAdapter(context, getList(list));
            if (onItemClickListener != null) {
                bannerAdapter.setOnItemClickListener(onItemClickListener);
            }
            viewPager.setAdapter(bannerAdapter);

            viewPager.addOnPageChangeListener(new PagerListener());
            viewPager.setCurrentItem(pageIndex);
            if (is_open) {
                if (scheduledExecutorService != null) {
                    scheduledExecutorService.shutdown();
                }
                scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
                scheduledExecutorService.scheduleAtFixedRate(new ViewPagerScrollTask(), TIME_UNIT, TIME_UNIT, TimeUnit.SECONDS);

            } else {
                if (scheduledExecutorService != null) {
                    scheduledExecutorService.shutdown();
                }
            }
        }
    }

    /**
     * 广告调用接口
     *
     * @param num       集合的size
     * @param time_unit 自动滑动的时间（秒）
     * @param is_open   是否自动滑动
     * @param list      图片集合
     * @param ratio     这个是广告的宽高的比例(传-1就是默认0.5比例)
     */
    public void startAD(int num, int time_unit, boolean is_open, ArrayList<Ad> list, float ratio, Activity activity) {
        if (ratio != -1) {
            RATIO = ratio;
        }
        this.list = list;
        if (is_open) {
            if (time_unit <= 0) {
                TIME_UNIT = 1;
            } else {
                TIME_UNIT = time_unit;
            }
        }
        initListNavigation(num, context);

        if (num <= 0) {
            findViewById(R.id.linear_ad).setVisibility(View.GONE);
        } else {
            findViewById(R.id.linear_ad).setVisibility(View.VISIBLE);
            BannerAdapter bannerAdapter = new BannerAdapter(context, getList(list));
            if (onItemClickListener != null) {
                bannerAdapter.setOnItemClickListener(onItemClickListener);
            }
            viewPager.setAdapter(bannerAdapter);

            viewPager.addOnPageChangeListener(new PagerListener());
            viewPager.setCurrentItem(pageIndex);
            if (is_open) {
                if (scheduledExecutorService != null) {
                    scheduledExecutorService.shutdown();
                }

                scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
                scheduledExecutorService.scheduleAtFixedRate(new ViewPagerScrollTask(), TIME_UNIT, TIME_UNIT, TimeUnit.SECONDS);

            } else {
                if (scheduledExecutorService != null) {
                    scheduledExecutorService.shutdown();
                }
            }
        }
    }

    /**
     * 广告调用接口
     *
     * @param num       集合的size
     * @param time_unit 自动滑动的时间（秒）
     * @param is_open   是否自动滑动
     * @param list      图片集合
     */
    public void startAD(int num, int time_unit, boolean is_open, ArrayList<Ad> list) {
        startAD(num, time_unit, is_open, list, RATIO);
    }

    /**
     * 最前面加一张最后一张，最后面再加一张第一张。
     *
     * @param list
     * @return
     */
    private ArrayList<Ad> getList(ArrayList<Ad> list) {
        ArrayList<Ad> ll = new ArrayList<>();
        if (list.size() > 1) {
            indicatorLayout.setVisibility(View.VISIBLE);
            for (int i = 0; i < list.size(); i++) {
                if (i == 0) {
                    ll.add(list.get(list.size() - 1));
                }
                ll.add(list.get(i));
                if (i == list.size() - 1) {
                    ll.add(list.get(0));
                }
            }
        } else {
            ll.addAll(list);
            indicatorLayout.setVisibility(View.GONE);
        }
        return ll;
    }

    private BannerAdapter.OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(BannerAdapter.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}
