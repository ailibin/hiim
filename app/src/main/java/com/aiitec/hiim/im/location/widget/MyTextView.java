package com.aiitec.hiim.im.location.widget;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;

import com.aiitec.openapi.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ailibin on 2018/1/31.
 * 关键字变色
 */

public class MyTextView extends android.support.v7.widget.AppCompatTextView {

    public MyTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setSpecifiedTextsColor(String text, String specifiedTexts, int color) {


        List<Integer> sTextsStartList = new ArrayList<>();
        int sTextLength = specifiedTexts.length();
        String temp = text;
        int lengthFront = 0;//记录被找出后前面的字段的长度
        int start = -1;
        do {
            //specifiedTexts的内容为空的话进来搜索就会报异常,所以必须保证specifiedTexts的内容不为空才能调setSpecifiedTextsColor()这个方法
            start = temp.indexOf(specifiedTexts);
            if (start != -1) {
                start = start + lengthFront;
                sTextsStartList.add(start);
                lengthFront = start + sTextLength;
                temp = text.substring(lengthFront);
            }

            LogUtil.d("ailibin", "start: " + start + " sTextsStartList: " + sTextsStartList.toString()
                    + " lengthFront: " + lengthFront + " temp: " + temp);

        } while (start != -1);

        SpannableStringBuilder styledText = new SpannableStringBuilder(text);
        for (Integer i : sTextsStartList) {
            styledText.setSpan(new ForegroundColorSpan(color), i, i + sTextLength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        }

        setText(styledText);
    }
}
