package com.aiitec.hiim.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.aiitec.openapi.utils.LogUtil;
import com.alipay.sdk.app.PayTask;

import java.util.Map;

/**
 * @Author ailibin
 * @Version 1.0
 * Created on 2018/3/30
 * @effect 支付宝支付工具类(这里签名和订单信息都是在服务器做的,所以不需要做任何签名)
 */

public class AliPayUtils {

    private Activity activity;

    public AliPayUtils(Activity activity) {
        this.activity = activity;
    }

    private final int PAY_FLAG = 1;
    private AliPayResultListener resultListener;

    /**
     * 处理支付回调
     */
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == PAY_FLAG) {
                PayResult payResult = new PayResult((Map<String, String>) msg.obj);
                /**
                 对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
                 */
                // 同步返回需要验证的信息
                String resultInfo = payResult.getResult();
                LogUtil.i("ailibin", "支付结果为：" + resultInfo);
                String resultStatus = payResult.getResultStatus();
                // 判断resultStatus 为9000则代表支付成功
                //同步通知
                if (TextUtils.equals(resultStatus, "9000")) {
                    // 支付成功
                    resultListener.aliPaySucceed();
                } else if (TextUtils.equals(resultStatus, "6001")) {
                    // 支付取消
                    resultListener.aliPayCancel();
                } else {
                    //支付失败
                    resultListener.aliPayDefeat();
                }
            }
        }
    };

    /**
     * 调起支付
     *
     * @param orderInfo
     */
    public void toPay(final String orderInfo, AliPayResultListener resultListener) {
        this.resultListener = resultListener;
        Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                PayTask alipay = new PayTask(activity);
                Map<String, String> result = alipay.payV2(orderInfo, true);

                Message msg = new Message();
                msg.what = PAY_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };

        new Thread(payRunnable).start();
    }

    /**
     * 支付结果类
     */
    private class PayResult {
        private String resultStatus;
        private String result;
        private String memo;

        PayResult(Map<String, String> rawResult) {
            if (rawResult == null) {
                return;
            }

            for (String key : rawResult.keySet()) {
                if (TextUtils.equals(key, "resultStatus")) {
                    resultStatus = rawResult.get(key);
                } else if (TextUtils.equals(key, "result")) {
                    result = rawResult.get(key);
                } else if (TextUtils.equals(key, "memo")) {
                    memo = rawResult.get(key);
                }
            }
        }

        @Override
        public String toString() {
            return "resultStatus={" + resultStatus + "};memo={" + memo
                    + "};result={" + result + "}";
        }

        /**
         * @return the resultStatus
         */
        public String getResultStatus() {
            return resultStatus;
        }

        /**
         * @return the memo(备忘录)
         */
        public String getMemo() {
            return memo;
        }

        /**
         * @return the result
         */
        public String getResult() {
            return result;
        }
    }

    /**
     * 支付回调的接口
     */
    public interface AliPayResultListener {
        /**
         * 支付成功
         */
        void aliPaySucceed();

        /**
         * 支付取消
         */
        void aliPayCancel();

        /**
         * 支付失败
         */
        void aliPayDefeat();
    }
}
