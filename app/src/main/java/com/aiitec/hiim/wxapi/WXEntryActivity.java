package com.aiitec.hiim.wxapi;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.aiitec.entitylibary.model.User;
import com.aiitec.entitylibary.request.UserDetailsRequestQuery;
import com.aiitec.entitylibary.request.UserLoginRequestQuery;
import com.aiitec.entitylibary.response.UserDetailsResponseQuery;
import com.aiitec.entitylibary.response.UserLoginResponseQuery;
import com.aiitec.hiim.base.App;
import com.aiitec.hiim.base.Constants;
import com.aiitec.hiim.ui.MainActivity;
import com.aiitec.hiim.ui.login.BindPhoneActivity;
import com.aiitec.hiim.utils.BaseUtil;
import com.aiitec.hiim.utils.WXUtils;
import com.aiitec.openapi.cache.AiiFileCache;
import com.aiitec.openapi.constant.AIIConstant;
import com.aiitec.openapi.json.enums.AIIAction;
import com.aiitec.openapi.model.ResponseQuery;
import com.aiitec.openapi.net.AIIResponse;
import com.aiitec.openapi.utils.LogUtil;
import com.aiitec.openapi.utils.PacketUtil;
import com.aiitec.openapi.utils.ToastUtil;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.umeng.analytics.MobclickAgent;

/**
 * @author ailibin
 */
public class WXEntryActivity extends Activity implements IWXAPIEventHandler {


    private static final int RETURN_MSG_TYPE_LOGIN = 1;
    private static final int RETURN_MSG_TYPE_SHARE = 2;
    private static final String RETURN_MSG_TYPE_BIND = "letar_weixin_bind_authorization";

    private static WXEntryActivity instance;

    public static WXEntryActivity getInstance() {
        if (instance == null) {
            instance = new WXEntryActivity();
        }
        return instance;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //注意：
        //第三方开发者如果使用透明界面来实现WXEntryActivity，需要判断handleIntent的返回值，如果返回值为false，
        // 则说明入参不合法未被SDK处理，应finish当前透明界面，避免外部通过传递非法参数的Intent导致停留在透明界面，引起用户的疑惑
        instance = this;
        if (!WXUtils.wxApi.handleIntent(getIntent(), this)) {
            finish();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        WXUtils.wxApi.handleIntent(intent, this);
    }

    /**
     * 微信发送请求到第三方应用时，会回调到该方法
     *
     * @param baseReq
     */
    @Override
    public void onReq(BaseReq baseReq) {

    }

    /**
     * 第三方应用发送到微信的请求处理后的响应结果，会回调到该方法,app发送消息给微信，处理返回消息的回调
     *
     * @param baseResp
     */
    @Override
    public void onResp(BaseResp baseResp) {
        LogUtil.d("ailibin", "微信的回调信息：" + baseResp.errCode);
        switch (baseResp.errCode) {

            case BaseResp.ErrCode.ERR_USER_CANCEL:
                //登录或分享取消
                finish();
                break;
            case BaseResp.ErrCode.ERR_OK:
                switch (baseResp.getType()) {
                    case RETURN_MSG_TYPE_LOGIN:
                        //拿到了微信返回的临时票据code,立马再去请求access_token
                        String code = ((SendAuth.Resp) baseResp).code;
                        LogUtil.d("ailibin", "code: " + code);
                        if(((SendAuth.Resp) baseResp).state.equals(RETURN_MSG_TYPE_BIND)){
                            requestUserChangeBind(code);
                            return;
                        }
                        requestUserPartnerLoginProtocol(code);
                        break;
                    case RETURN_MSG_TYPE_SHARE:
                        //微信分享成功
                        finish();
                        break;
                    default:
                        break;
                }
                break;

            case BaseResp.ErrCode.ERR_UNSUPPORT:
                //不支持
                BaseUtil.showToast("当前应用不支持微信登录");
                finish();
                break;
            default:
        }
    }


    /**
     * 请求第三方微信登陆
     */
    private void requestUserPartnerLoginProtocol(final String code) {
        UserLoginRequestQuery query = new UserLoginRequestQuery();
        query.setDir("Cis");
        query.setAction(AIIAction.TWO);
        query.setCode(code);
        App.Companion.getAiiRequest().send(query, new AIIResponse<UserLoginResponseQuery>(this, false) {
            @Override
            public void onSuccess(UserLoginResponseQuery response, int index) {
                super.onSuccess(response, index);

                int isBindMoblile = response.isBindMobile();
                if (isBindMoblile == 2) {
                    //已经绑定了第三方了
                    requestUserDetails();
                } else {
                    Intent intent = new Intent(WXEntryActivity.this, BindPhoneActivity.class);
                    intent.putExtra("openid", code);
                    startActivity(intent);
                    finish();
                    LogUtil.d("ailibin", "openid: " + code);
                }
            }

            @Override
            public void onFinish(int index) {
                super.onFinish(index);
            }
        });
    }

    /**
     * 重新绑定微信号
     */
    private void requestUserChangeBind(String code){
        UserLoginRequestQuery query = new UserLoginRequestQuery();
        query.setDir("Cis");
        query.setNamespace("UserChangeBind");
        query.setAction(AIIAction.TWO);
        query.setCode(code);
        App.Companion.getAiiRequest().send(query, new AIIResponse<ResponseQuery>(this, false) {

            @Override
            public void onSuccess(ResponseQuery response, int index) {
                super.onSuccess(response, index);
                ToastUtil.show(WXEntryActivity.this, "绑定成功");
                finish();
            }
        });
    }

    /**
     * 请求用户详情协议
     */
    private void requestUserDetails() {
        UserDetailsRequestQuery query = new UserDetailsRequestQuery();
        query.setDir("Cis");
        query.setId(0);
        App.Companion.getAiiRequest().send(query, new AIIResponse<UserDetailsResponseQuery>(this, false) {
            @Override
            public void onSuccess(UserDetailsResponseQuery response, int index) {
                super.onSuccess(response, index);
                //保存到全局变量中
                User user = response.getUser();
                Constants.INSTANCE.setUser(user);
                if (user != null) {
                    AIIConstant.USER_ID = user.getId();
                    AiiFileCache.changeDir(PacketUtil.getCacheDir(WXEntryActivity.this));
                    MobclickAgent.onProfileSignIn(String.valueOf(user.getId()));
                    changeUser(user.getId());
                }
                startActivity(new Intent(WXEntryActivity.this, MainActivity.class));
                finish();
            }
        });
    }

    /**
     * 更改用户
     *
     * @param userId 用户id
     */
    private void changeUser(long userId) {
        //更改用户id的时候，需要把缓存路径也更改一下，否则读取缓存有可能读到别人的缓存
        AIIConstant.USER_ID = userId;
        AiiFileCache.changeDir(PacketUtil.getCacheDir(this));
    }

}
