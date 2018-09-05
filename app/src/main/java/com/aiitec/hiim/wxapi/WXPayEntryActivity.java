package com.aiitec.hiim.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import com.aiitec.entitylibary.request.UserDetailsRequestQuery;
import com.aiitec.entitylibary.response.UserDetailsResponseQuery;
import com.aiitec.hiim.R;
import com.aiitec.hiim.base.App;
import com.aiitec.hiim.utils.BaseUtil;
import com.aiitec.openapi.net.AIIResponse;
import com.aiitec.openapi.utils.LogUtil;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.greenrobot.eventbus.EventBus;


/**
 * @author ailibin
 * @date 2018/5/4
 */
public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {

    private IWXAPI api;
    private static WXPayEntryActivity instance;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextView tv = new TextView(this);
        setContentView(tv);
        api = WXAPIFactory.createWXAPI(this, getResources().getString(R.string.weixinId));
        api.handleIntent(getIntent(), this);

    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq req) {

    }

    @Override
    public void onResp(BaseResp resp) {
        LogUtil.i("onPayFinish, errCode = " + resp.errCode + "   " + resp.errStr);

        if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
            int errCode = resp.errCode;
            LogUtil.i("TAG", "返回的errCode:" + errCode);
            // 支付成功
            if (errCode == BaseResp.ErrCode.ERR_OK) {
                requestUserDetails();
                BaseUtil.showToast("支付成功");
                EventBus.getDefault().post("success");

            } else if (errCode == BaseResp.ErrCode.ERR_USER_CANCEL) {
                BaseUtil.showToast("支付取消");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                }, 666);
            } else {
                BaseUtil.showToast("支付失败，请重试：" + String.valueOf(errCode));
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                }, 666);
            }
        }
    }


    /**
     * 请求用户详情
     */
    private void requestUserDetails() {
        UserDetailsRequestQuery query = new UserDetailsRequestQuery();
        query.setDir("Cis");
        query.setId(0);
        App.Companion.getAiiRequest().send(query, new AIIResponse<UserDetailsResponseQuery>(this, false) {
            @Override
            public void onSuccess(UserDetailsResponseQuery response, int index) {
                super.onSuccess(response, index);
//                UserInfo userInfo = response.getUserInfo();
//                if (userInfo != null) {
//                    Constants.INSTANCE.setUserInfo(userInfo);
//                    AIIConstant.USER_ID = userInfo.getId();
//                    AiiFileCache.changeDir(PacketUtil.getCacheDir(WXPayEntryActivity.this));
//                }
//                postEvent();
            }
        });
    }

//    public void postEvent() {
//        Event.OnPaySuccessEvent event = new Event.OnPaySuccessEvent();
//        //微信支付方式
//        event.setType(3);
//        event.setTag(Constants.INSTANCE.getKEY_PAY_SUCCESS());
//        EventBus.getDefault().post(event);
//        finish();
//    }


}
