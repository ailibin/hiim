package com.aiitec.imlibrary.tlslibrary.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.aiitec.imlibrary.R;
import com.aiitec.imlibrary.tlslibrary.service.TLSService;


public class IndependentRegisterActivity extends Activity {

    public final static String TAG = "IndependentRegisterActivity";
    private TLSService tlsService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(MResource.getIdByName(getApplication(), "layout", "tencent_tls_ui_activity_independent_register"));
        setContentView(R.layout.tencent_tls_ui_activity_independent_register);

        tlsService = TLSService.getInstance();
//        tlsService.initAccountRegisterService(this,
//                (EditText) findViewById(MResource.getIdByName(getApplication(), "id", "username")),
//                (EditText) findViewById(MResource.getIdByName(getApplication(), "id", "password")),
//                (EditText) findViewById(MResource.getIdByName(getApplication(), "id", "repassword")),
//                (Button) findViewById(MResource.getIdByName(getApplication(), "id", "btn_register"))
//        );

        tlsService.initAccountRegisterService(this,
                (EditText) findViewById(R.id.username),
                (EditText) findViewById(R.id.password),
                (EditText) findViewById(R.id.repassword),
                (Button) findViewById(R.id.btn_register)
        );


        // 设置返回按钮
//        findViewById(MResource.getIdByName(getApplication(), "id", "returnIndependentLoginActivity"))
        findViewById(R.id.returnIndependentLoginActivity)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        IndependentRegisterActivity.this.onBackPressed();
                    }
                });
    }
}
