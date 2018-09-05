package com.aiitec.widgets;

import android.content.Context;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.AttributeSet;
import android.widget.EditText;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 禁止输入表情的EditText
 * @author Anthony
 * @version 1.0
 * createTime 2018/4/28.
 */
public class NoEmojiEditText extends android.support.v7.widget.AppCompatEditText {

    public NoEmojiEditText(Context context) {
        super(context);
        init();
    }

    public NoEmojiEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public NoEmojiEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    private void init(){
        setEmojiFilter(this);
    }


    /**
     * 给edittext设置过滤器 过滤emoji
     *
     * @param et
     */
    public static void setEmojiFilter(EditText et) {
        InputFilter emojiFilter = new InputFilter() {
            String regularStr = "[\ud83c\udc00-\ud83c\udfff]|[\ud83d\udc00-\ud83d\udfff]|[\u2600-\u27ff]";
            Pattern pattern = Pattern.compile(regularStr, Pattern.UNICODE_CASE | Pattern.CASE_INSENSITIVE);

            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                Matcher matcher = pattern.matcher(source);
                if(matcher.find()){
                    return "";
                }
                return null;
            }
        };
        et.setFilters(new InputFilter[]{emojiFilter});
    }
}
