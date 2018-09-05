package com.aiitec.imlibrary.tlslibrary.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.aiitec.imlibrary.tlslibrary.helper.MResource;
import com.aiitec.imlibrary.tlslibrary.helper.SmsContentObserver;
import com.aiitec.imlibrary.tlslibrary.service.TLSService;


public class ResetPhonePwdActivity extends Activity {

    private final static String TAG = "ResetPhonePwdActivity";

    private TLSService tlsService;
    private SmsContentObserver smsContentObserver = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(MResource.getIdByName(getApplication(), "layout", "tencent_tls_ui_activity_reset_phone_pwd"));

        // 设置返回按钮
        findViewById(MResource.getIdByName(getApplication(), "id", "btn_back"))
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ResetPhonePwdActivity.this.onBackPressed();
                    }
                });

        tlsService = TLSService.getInstance();
        tlsService.initResetPhonePwdService(this,
                (EditText) findViewById(MResource.getIdByName(getApplication(), "id", "selectCountryCode")),
                (EditText) findViewById(MResource.getIdByName(getApplication(), "id", "phone")),
                (EditText) findViewById(MResource.getIdByName(getApplication(), "id", "txt_checkcode")),
                (Button) findViewById(MResource.getIdByName(getApplication(), "id", "btn_requirecheckcode")),
                (Button) findViewById(MResource.getIdByName(getApplication(), "id", "btn_verify"))
        );

/*        smsContentObserver = new SmsContentObserver(new Handler(),
                this,
                (EditText) findViewById(MResource.getIdByName(getApplication(), "id", "txt_checkcode")),
                Constants.PHONEPWD_RESET_SENDER);

        //注册短信变化监听
        this.getContentResolver().registerContentObserver(Uri.parse("content://sms/"), true, smsContentObserver);*/
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (smsContentObserver != null) {
            this.getContentResolver().unregisterContentObserver(smsContentObserver);
        }
    }
}
