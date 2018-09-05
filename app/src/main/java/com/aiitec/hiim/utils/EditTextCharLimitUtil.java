package com.aiitec.hiim.utils;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;

/**
 * @author afb
 * @date 2017/11/2
 * 限制输入十个汉字或30个字符(utf-8编码,一个汉字3个字节,其它的基本都是1个字节)
 */

public class EditTextCharLimitUtil {

    /**
     * 限制输入的最大字符数
     */
    private final int mMaxLength;

    /**
     * 最低输入字数
     */
    private int mMinLength;

    /**
     * 超限时输出的提示的内容
     */
    private String mToastText;

    private EditText mEditText;

    private Context mContext;

    private String mContent;


    /**
     * @param mContext
     * @param mEditText:  需要监视的输入框
     * @param mMaxLength  :支持输入的最大字符数（1个汉字为3个字符，1个英文字母为1个字符）
     * @param mToastText: 输入字符数超出最大值时的toast文字提示，为null时，不提示
     */
    public EditTextCharLimitUtil(Context mContext, EditText mEditText, int mMaxLength, int mMinLength,
                                 String mToastText) {
        this.mContext = mContext;
        this.mMaxLength = mMaxLength;
        this.mToastText = mToastText;
        this.mEditText = mEditText;
        this.mMinLength = mMinLength;
    }

    /**
     * @param mContext
     * @param mEditText:  需要监视的输入框
     * @param mMaxLength  :支持输入的最大字符数（1个汉字为3个字符，1个英文字母为1个字符）
     * @param mToastText: 输入字符数超出最大值时的toast文字提示，为null时，不提示
     */
    public EditTextCharLimitUtil(Context mContext, EditText mEditText, int mMaxLength,
                                 String mToastText) {
        this(mContext, mEditText, mMaxLength, 0, mToastText);
//        this.mContext = mContext;
//        this.mMaxLength = mMaxLength;
//        this.mToastText = mToastText;
//        this.mEditText = mEditText;
    }


    public String getEditContent() {
        return mContent;
    }

    public void init() {
        mEditText.addTextChangedListener(new TextWatcher() {
            private CharSequence temp;
            private int selectionStart;
            private int selectionEnd;

            @Override
            public void beforeTextChanged(CharSequence s, int arg1, int arg2, int arg3) {
                temp = s;
            }

            @Override
            public void onTextChanged(CharSequence s, int arg1, int arg2, int arg3) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                selectionStart = mEditText.getSelectionStart();
                selectionEnd = mEditText.getSelectionEnd();
                try {
                    if (temp.toString().getBytes("GBK").length > mMaxLength) {
                        if (!TextUtils.isEmpty(mToastText)) {
                            Toast.makeText(mContext, mToastText, Toast.LENGTH_SHORT).show();
                        }
                        s.delete(selectionStart - 1, selectionEnd);
//                        int tempSelection = selectionStart;
//                        mEditText.setText(s);
//                        mEditText.setSelection(tempSelection);
                    }

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }

        });

    }

}
