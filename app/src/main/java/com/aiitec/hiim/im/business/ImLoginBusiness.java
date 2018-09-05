package com.aiitec.hiim.im.business;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.aiitec.imlibrary.presentation.business.LoginBusiness;
import com.aiitec.imlibrary.presentation.event.FriendshipEvent;
import com.aiitec.imlibrary.presentation.event.GroupEvent;
import com.aiitec.imlibrary.presentation.event.MessageEvent;
import com.aiitec.imlibrary.presentation.event.RefreshEvent;
import com.aiitec.imlibrary.presentation.presenter.FriendshipManagerPresenter;
import com.aiitec.imlibrary.tlslibrary.service.TlsBusiness;
import com.aiitec.hiim.R;
import com.aiitec.hiim.base.App;
import com.aiitec.hiim.base.Constants;
import com.aiitec.hiim.im.model.FriendshipInfo;
import com.aiitec.hiim.im.model.GroupInfo;
import com.aiitec.hiim.im.model.UserInfo;
import com.aiitec.hiim.im.utils.PushUtil;
import com.aiitec.hiim.ui.MainActivity;
import com.aiitec.hiim.ui.login.LoginActivity;
import com.aiitec.hiim.utils.BaseUtil;
import com.aiitec.openapi.utils.AiiUtil;
import com.aiitec.openapi.utils.LogUtil;
import com.aiitec.openapi.utils.ToastUtil;
import com.tencent.imsdk.TIMCallBack;
import com.tencent.imsdk.TIMConnListener;
import com.tencent.imsdk.TIMFriendAllowType;
import com.tencent.imsdk.TIMManager;
import com.tencent.imsdk.TIMUserConfig;
import com.tencent.imsdk.TIMUserStatusListener;

/**
 * @author ailibin
 * @date 2018/5/18
 * Im登录的工具类
 */

public class ImLoginBusiness {

    private ImLoginListener listener;

    private static ImLoginBusiness instance;

    public void setImLoginListener(ImLoginListener listener) {
        this.listener = listener;
    }

    public static synchronized ImLoginBusiness getInstance() {
        if (instance == null) {
            instance = new ImLoginBusiness();
        }
        return instance;
    }

    /**
     * @param context
     * @author ailibin
     */
    public void navToHome(final Context context) {

        //登录之前要初始化群和好友关系链缓存
        TIMUserConfig userConfig = new TIMUserConfig();
        //设置刷新监听
        RefreshEvent.getInstance().init(userConfig);
        userConfig = FriendshipEvent.getInstance().init(userConfig);
        userConfig = GroupEvent.getInstance().init(userConfig);
        userConfig = MessageEvent.getInstance().init(userConfig);
        TIMManager.getInstance().setUserConfig(userConfig);

        LoginBusiness.loginIm(UserInfo.getInstance().getId(), UserInfo.getInstance().getUserSig(), new TIMCallBack() {
            @Override
            public void onError(int i, String s) {
                switch (i) {
                    case 6208:
                        ToastUtil.show(context, R.string.kick_logout);
                        context.startActivity(new Intent(context, LoginActivity.class));
                        break;
                    case 6200:
                        ToastUtil.show(context, R.string.login_error_timeout);
                        context.startActivity(new Intent(context, LoginActivity.class));
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onSuccess() {
                //初始化程序后台后消息推送
                PushUtil.getInstance();
                //初始化消息监听
                MessageEvent.getInstance();
                /**
                 * 有时候会出现问题,所以这个设置验证方式去掉吧
                 */
                FriendshipManagerPresenter.setFriendAllowType(TIMFriendAllowType.TIM_FRIEND_NEED_CONFIRM, new TIMCallBack() {
                    @Override
                    public void onError(int i, String s) {
                        LogUtil.w("设置验证方式失败" + "s: " + s + "i: " + i);
                        Intent intent = new Intent(context, LoginActivity.class);
                        context.startActivity(intent);
                    }

                    @Override
                    public void onSuccess() {
                        LogUtil.d("imsdk env " + TIMManager.getInstance().getEnv());
//                        这里跳转到h5首页
                        Intent intent = new Intent(context, MainActivity.class);
                        context.startActivity(intent);
//                        if (listener != null) {
//                            listener.onImLoginSuccess();
//                        }
                    }
                });

            }
        });

        userConfig.setUserStatusListener(new TIMUserStatusListener() {
            @Override
            public void onForceOffline() {
                AiiUtil.putString(context, "user", null);
                Constants.INSTANCE.setUser(null);
                BaseUtil.showToast("您的用户在别处登录，请重新登录");
                if (context != null) {
                    context.startActivity(new Intent(context, LoginActivity.class));
                }
//                requestLogout();
                App.Companion.getInstance().closeAllActivity();
                LogUtil.e("IM login on other");
            }

            @Override
            public void onUserSigExpired() {
                LogUtil.d("onUserSigExpired ");
                Constants.INSTANCE.setUser(null);
                AiiUtil.putString(context, "user", null);
                //票据过期，需要重新登录
                BaseUtil.showToast("票据过期,请重新登录");
                if (context != null) {
                    context.startActivity(new Intent(context, LoginActivity.class));
                }
//                new NotifyDialog().show(context.getResources().getString(R.string.tls_expire), supportFragmentManager);
//                {
//                    dialog, which ->
//                            requestLogout();
//                    context.startActivity(new Intent(context, LoginActivity.class));
//                }
            }
        }).setConnectionListener(new TIMConnListener() {
            @Override
            public void onConnected() {
                LogUtil.i("tencentIM", "onConnected");
            }

            @Override
            public void onDisconnected(int i, String s) {
                LogUtil.i("tencentIM", "onDisconnected");
            }

            @Override
            public void onWifiNeedAuth(String s) {
                LogUtil.i("tencentIM", "onWifiNeedAuth");
            }
        });
    }


    /**
     * 登出IM
     *
     * @param context
     */
    public void logoutIM(final Context context) {
        LoginBusiness.logout(new TIMCallBack() {
            @Override
            public void onError(int i, String s) {
                Toast.makeText(context, context.getString(R.string.setting_logout_fail), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess() {
                TlsBusiness.logout(UserInfo.getInstance().getId());
                UserInfo.getInstance().setId(null);
                MessageEvent.getInstance().clear();
                FriendshipInfo.getInstance().clear();
                GroupInfo.getInstance().clear();
                App.Companion.getInstance().closeAllActivity();
                context.startActivity(new Intent(context, LoginActivity.class));
            }
        });
    }
}
