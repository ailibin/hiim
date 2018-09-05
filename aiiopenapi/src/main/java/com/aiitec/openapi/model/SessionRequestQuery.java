package com.aiitec.openapi.model;

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
import com.aiitec.openapi.utils.AiiUtil;
import com.aiitec.openapi.utils.PacketUtil;

import java.text.DecimalFormat;
import java.util.UUID;
public class SessionRequestQuery extends RequestQuery {

    public SessionRequestQuery() {

    }

    public SessionRequestQuery(Context context) {
        setSessionData(context);
    }

    public SessionRequestQuery(Context context, AIIAction action) {
        this.action = action;
        setSessionData(context);
    }

    public SessionRequestQuery(AIIAction action) {
        this.action = action;
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

    /**
     * 设置session信息
     * 
     * @param context
     *            上下文对象
     */
    public void setSessionData(Context context) {
        String deviceId = getDeviceId(context);
        String deviceToken = AiiUtil.getString(context, CommonKey.KEY_DEVICETOKEN);
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        DecimalFormat format = new DecimalFormat("0.#");
        double z = Math.sqrt(Math.pow(dm.widthPixels, 2) + Math.pow(dm.heightPixels, 2));
        double size = (z / (160 * dm.density));

        setAction(action);
        setLang(2502);
        setModel(Build.MODEL);
        setVersion(PacketUtil.getVersionName(context));
        setScreenSize(format.format(size));
        if (TextUtils.isEmpty(deviceToken) || deviceToken.equals("-1")) {
            setDeviceToken(deviceId);
            setDeviceType(32);
        } else {
            setDeviceToken(deviceToken);
            setDeviceType(2);
        }
        setResolution(dm.widthPixels + "*" + dm.heightPixels);
        setDeviceInfo(Build.VERSION.RELEASE);
        //礼物飞项目需要加base
        setDir("base");

    }

    /** 协议版本 */
    private String version;
    /**
     * 机型型号 IPad Android IPhone等
     */
    private String model;
    /** 屏幕尺寸 如 480*854 */
    private String resolution;
    /** 语言 */
    private int lang = -1;
    /** 屏幕尺寸 */
    private String screenSize;
    /** 设备号 */
    private String deviceToken;
    /** 设备信息（名称） */
    @JSONField(name = "info")
    private String deviceInfo;
    /** 设备类型 */
    private int deviceType = -1;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getResolution() {
        return resolution;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }

    public int getLang() {
        return lang;
    }

    public void setLang(int lang) {
        this.lang = lang;
    }

    public String getScreenSize() {
        return screenSize;
    }

    public void setScreenSize(String screenSize) {
        this.screenSize = screenSize;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    public String getDeviceInfo() {
        return deviceInfo;
    }

    public void setDeviceInfo(String deviceInfo) {
        this.deviceInfo = deviceInfo;
    }

    public int getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(int deviceType) {
        this.deviceType = deviceType;
    }

}
