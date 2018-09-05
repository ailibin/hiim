package com.aiitec.hiim.utils;

import android.os.CountDownTimer;
import android.widget.TextView;

/**
 * @author Anthony
 * @version 1.0
 * createTime 2017/9/15.
 */

public class SmscodeCountDown extends CountDownTimer {

    private TextView btn_smscode;

    /**
     * @param millisInFuture    The number of millis in the future from the call
     *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
     *                          is called.
     * @param countDownInterval The interval along the way to receive
     *                          {@link #onTick(long)} callbacks.
     */
    public SmscodeCountDown(long millisInFuture, long countDownInterval) {
        super(millisInFuture, countDownInterval);
    }

    @Override
    public void onTick(long millisUntilFinished) {
        try {
            btn_smscode.setText("重新获取(" + (millisUntilFinished / 1000) + "s)");
        } catch (Exception e) {
            e.printStackTrace();
        }
        btn_smscode.setEnabled(false);
    }

    @Override
    public void onFinish() {
        resetSmsCode();
//        btn_smscode.setEnabled(true);
//        btn_smscode.setText("重新获取");
//        this.cancel();
    }

    public void resetSmsCode() {
        btn_smscode.setEnabled(true);
        btn_smscode.setText("重新获取");
        this.cancel();
    }


    public void setSmscodeBtn(TextView btn_smscode) {
        this.btn_smscode = btn_smscode;
    }
}
