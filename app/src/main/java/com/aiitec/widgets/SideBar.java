package com.aiitec.widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.aiitec.openapi.utils.ScreenUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Anthony
 * @version 1.0
 *          createTime 2017/9/20.
 */

public class SideBar extends View {

    //    private static final int SELECTED_COLOR = Color.parseColor("#FFD347");
    private static final int SELECTED_COLOR = Color.parseColor("#DE7FEF");

    private static final int NORMAL_COLOR = Color.parseColor("#777777");
    /**
     * 触摸事件
     */
    private OnTouchingLetterChangedListener onTouchingLetterChangedListener;
    /**
     * 26个字母
     */
    public static String[] letters = {"A", "B", "C", "D", "E", "F", "G", "H", "I",
            "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
            "W", "X", "Y", "Z", "#"};
    public static List<String> b = new ArrayList<>();
    /**
     * 选中
     */
    private int choose = -1;
    private Paint paint = new Paint();

    private TextView mTextDialog;

    public void setTextView(TextView mTextDialog) {
        this.mTextDialog = mTextDialog;
    }

    public SideBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public SideBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SideBar(Context context) {
        super(context);
    }

    /**
     * 重写这个方法
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 获取焦点改变背景颜色.
//        int height = getHeight();// 获取对应高度
        //   /*height / b.size()*/获取每一个字母的高度
        int singleHeight = ScreenUtils.dip2px(getContext(), 20f);
        getLayoutParams().height = singleHeight * b.size();
        // 获取对应宽度
        int width = getWidth();
        if (b.size() > 0) {


            for (int i = 0; i < b.size(); i++) {
                paint.setColor(Color.rgb(33, 65, 98));
                paint.setTypeface(Typeface.DEFAULT_BOLD);
                paint.setAntiAlias(true);
                paint.setTextSize(ScreenUtils.dip2px(getContext(), 14));
                // 选中的状态
                if (i == choose) {
                    paint.setColor(SELECTED_COLOR);
                    //粗体
                    paint.setFakeBoldText(true);
                } else {
                    paint.setColor(NORMAL_COLOR);
                }
                // x坐标等于中间-字符串宽度的一半.
                float xPos = width / 2 - paint.measureText(b.get(i)) / 2;
                float yPos = singleHeight * i + singleHeight;
                canvas.drawText(b.get(i), xPos, yPos, paint);
                paint.reset();// 重置画笔
            }
        }

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        final int action = event.getAction();
        // 点击y坐标
        final float y = event.getY();
        final int oldChoose = choose;
        final OnTouchingLetterChangedListener listener = onTouchingLetterChangedListener;
        // 点击y坐标所占总高度的比例*b数组的长度就等于点击b中的个数.
        final int c = (int) (y / getHeight() * b.size());

        switch (action) {
            case MotionEvent.ACTION_UP:
                setBackgroundColor(0x00000000);
                choose = -1;
                invalidate();
                if (mTextDialog != null) {
                    mTextDialog.setVisibility(View.INVISIBLE);
                }
                break;
            default:
//                setBackgroundResource(R.drawable.sidebar_background);
                if (oldChoose != c) {
                    if (c >= 0 && c < b.size()) {
                        if (listener != null) {
                            listener.onTouchingLetterChanged(b.get(c));
                        }
                        if (mTextDialog != null) {
                            mTextDialog.setText(b.get(c));
                            mTextDialog.setVisibility(View.VISIBLE);
                        }

                        choose = c;
                        invalidate();
                    }
                }
                break;
        }
        return true;
    }

    /**
     * 向外公开的方法
     *
     * @param onTouchingLetterChangedListener
     */
    public void setOnTouchingLetterChangedListener(
            OnTouchingLetterChangedListener onTouchingLetterChangedListener) {
        this.onTouchingLetterChangedListener = onTouchingLetterChangedListener;
    }

    /**
     * 回调接口
     *
     * @author lez
     */
    public interface OnTouchingLetterChangedListener {
        void onTouchingLetterChanged(String s);
    }
}
