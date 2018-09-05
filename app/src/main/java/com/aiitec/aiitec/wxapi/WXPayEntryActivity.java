package com.aiitec.aiitec.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.aiitec.hiim.R;
import com.aiitec.openapi.utils.LogUtil;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;


public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {

	private IWXAPI api;

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
		 LogUtil.i("onPayFinish, errCode = " + resp.errCode+"   "+resp.errStr);

		if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
			int errCode = resp.errCode;
//			if (errCode == 0) {
//				// 支付成功
//				EventBus.getDefault().post(new WechatPayEvent(WechatPayEvent.Companion.getPAY_SUCCESS()));
//			} else if (errCode == -2) {
//				EventBus.getDefault().post(new WechatPayEvent(WechatPayEvent.Companion.getPAY_CANCEL()));
//			} else if (errCode == -1) {
//				EventBus.getDefault().post(new WechatPayEvent(WechatPayEvent.Companion.getPAY_FAILED()));
//			}

			finish();
		}
	}

}
