package com.aiitec.openapi.packet;

import android.content.Context;
import android.os.Build;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.aiitec.openapi.constant.CommonKey;
import com.aiitec.openapi.json.annotation.JSONField;
import com.aiitec.openapi.json.enums.AIIAction;
import com.aiitec.openapi.model.SessionRequestQuery;
import com.aiitec.openapi.utils.AiiUtil;
import com.aiitec.openapi.utils.PacketUtil;

import java.text.DecimalFormat;
import java.util.UUID;

/**
 * session请求类
 * 
 * @author Anthony
 * 
 */
public final class SessionRequest extends Request {

    @JSONField(name = "q")
    private SessionRequestQuery query;

    public SessionRequestQuery getQuery() {
        return query;
    }

    public void setQuery(SessionRequestQuery query) {
        this.query = query;
    }

    public SessionRequest(Context context, AIIAction action) {
        setSessionData(context, action);
    }

    public SessionRequest(Context context) {
        setSessionData(context, AIIAction.ONE);
    }

    /**
     * 设置session信息
     * 
     * @param context
     *            上下文对象
     */
    public void setSessionData(Context context, AIIAction action) {
        String deviceId = getDeviceId(context);
        String deviceToken = AiiUtil.getString(context, CommonKey.KEY_DEVICETOKEN);
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        DecimalFormat format = new DecimalFormat("0.#");
        double z = Math.sqrt(Math.pow(dm.widthPixels, 2) + Math.pow(dm.heightPixels, 2));
        double size = (z / (160 * dm.density));
        query = new SessionRequestQuery();
        query.setAction(action);
        query.setLang(2502);
        query.setModel(Build.MODEL);
        query.setVersion(PacketUtil.getVersionName(context));
        query.setScreenSize(format.format(size));
        if (TextUtils.isEmpty(deviceToken) || deviceToken.equals("-1")) {
            query.setDeviceToken(deviceId);
            query.setDeviceType(32);
        } else {
            query.setDeviceToken(deviceToken);
            query.setDeviceType(2);
        }
        query.setResolution(dm.widthPixels + "*" + dm.heightPixels);
        query.setDeviceInfo(Build.VERSION.RELEASE);
        setQuery(query);
    }

    /**
     * 获取手机设备号并加密，没有请求到信鸽设备号就用手机的设备号
     * 
     * @return 加密后的设备号
     */
    public String getDeviceId(Context context) {
        String tmDevice = "", tmSerial = "", androidId;
        try {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            tmDevice = "" + tm.getDeviceId();
            tmSerial = "" + tm.getSimSerialNumber();

        } catch (Exception e) {
            e.printStackTrace();// mi 4 发现获取不到权限的情况， 会闪退
        }
        // androidId 不需要去权限，但是有可能返回空
        androidId = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
        if (androidId == null) {
            androidId = context.getPackageName();
        }
        UUID deviceUuid = new UUID(androidId.hashCode(), ((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
        return AiiUtil.md5(deviceUuid.toString() + context.getPackageName());
    }

}
