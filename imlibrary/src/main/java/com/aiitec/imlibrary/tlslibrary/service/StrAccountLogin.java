package com.aiitec.imlibrary.tlslibrary.service;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.aiitec.imlibrary.tlslibrary.activity.ImgCodeActivity;
import com.aiitec.imlibrary.tlslibrary.helper.Util;

import tencent.tls.platform.TLSErrInfo;
import tencent.tls.platform.TLSPwdLoginListener;
import tencent.tls.platform.TLSUserInfo;

import static com.aiitec.imlibrary.tlslibrary.service.TLSService.setLastErrno;


/**
 * Created by weijunyi@tencent.com on 2016/4/28.
 */
public class StrAccountLogin {

    private Context context;

    public StrAccountLogin(Context context) {
        this.context = context;
    }

    public void doStrAccountLogin(String id, String password) {
        TLSService.getInstance().TLSPwdLogin(id, password, new TLSPwdLoginListener() {

            @Override
            public void OnPwdLoginSuccess(TLSUserInfo userInfo) {
                setLastErrno(0);
                ((Activity) context).setResult(Activity.RESULT_OK);
                ((Activity) context).finish();
            }

            @Override
            public void OnPwdLoginReaskImgcodeSuccess(byte[] picData) {
                ImgCodeActivity.fillImageview(picData);
            }

            @Override
            public void OnPwdLoginNeedImgcode(byte[] picData, TLSErrInfo errInfo) {
                Intent intent = new Intent(context, ImgCodeActivity.class);
                intent.putExtra(Constants.EXTRA_IMG_CHECKCODE, picData);
                intent.putExtra(Constants.EXTRA_LOGIN_WAY, Constants.USRPWD_LOGIN);
                context.startActivity(intent);
            }

            @Override
            public void OnPwdLoginFail(TLSErrInfo errInfo) {
                setLastErrno(-1);
                Util.notOK(context, errInfo);
            }

            @Override
            public void OnPwdLoginTimeout(TLSErrInfo errInfo) {
                setLastErrno(-1);
                Util.notOK(context, errInfo);
            }
        });
    }
}
