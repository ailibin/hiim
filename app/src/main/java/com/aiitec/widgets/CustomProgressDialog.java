package com.aiitec.widgets;


import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.aiitec.hiim.R;


/**
 * 自定义ProgressDialog
 */
public class CustomProgressDialog extends Dialog {
    private Context context = null;
    private static CustomProgressDialog customProgressDialog = null;

    public CustomProgressDialog(Context context) {
        this(context, R.style.CustomProgressDialog);

    }

    public CustomProgressDialog(Context context, int theme) {
        super(context, theme);
        this.context = context;
        setContentView(R.layout.customprogressdialog);
        getWindow().getAttributes().gravity = Gravity.CENTER;
    }

    public static CustomProgressDialog createDialog(Context context) {
        customProgressDialog = new CustomProgressDialog(context, R.style.CustomProgressDialog);
        customProgressDialog.setContentView(R.layout.customprogressdialog);
        customProgressDialog.getWindow().getAttributes().gravity = Gravity.CENTER;
//	        ImageView imageView = (ImageView) customProgressDialog.findViewById(R.id.loadingImageView);
//	        Animation anim=AnimationUtils.loadAnimation(context, R.anim.progress_round);
//	        imageView.setAnimation(anim);
        return customProgressDialog;
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {

        if (customProgressDialog == null) {
            return;
        }
/*
            ImageView imageView = (ImageView) customProgressDialog.findViewById(R.id.loadingImageView);
	        AnimationDrawable animationDrawable = (AnimationDrawable) imageView.getBackground();
	        animationDrawable.start();*/
    }

    /**
     * [Summary]
     * setTitile 标题
     *
     * @param strTitle
     * @return
     */
    @Deprecated
    public CustomProgressDialog setTitile(String strTitle) {
        return customProgressDialog;
    }

    /**
     * [Summary]
     * setMessage 提示内容
     *
     * @param strMessage
     * @return
     */
    public CustomProgressDialog setMessage(String strMessage) {
        TextView tvMsg = findViewById(R.id.id_tv_loadingmsg);

        if (tvMsg != null && !TextUtils.isEmpty(strMessage)) {
            tvMsg.setText(strMessage);
            tvMsg.setVisibility(View.VISIBLE);
        } else {
            tvMsg.setVisibility(View.GONE);
        }

        return customProgressDialog;
    }
}