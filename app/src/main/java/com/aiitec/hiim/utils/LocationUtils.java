package com.aiitec.hiim.utils;

import android.content.Context;

import com.aiitec.hiim.base.Constants;
import com.aiitec.hiim.im.utils.LogUtil;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;

/**
 * @author Anthony
 * @version 1.0
 * createTime 2018/1/19.
 */

public class LocationUtils implements AMapLocationListener {


    private Context context;

    public LocationUtils(Context context) {
        this.context = context;
    }

    int locationCount = 0;
    public static int locationFailMaxCount = 12;
    AMapLocationClient mlocationClient;

    /**
     * 开始定位
     */
    public synchronized void startLocation() {
//        //强制打开gps权限
//        if (Utils.isGPSOpen(this)) {
//            Utils.openGPS(this);
//        }
        if (mlocationClient == null) {
            mlocationClient = new AMapLocationClient(context);
            //声明mLocationOption对象
            //初始化定位参数
            AMapLocationClientOption mLocationOption = new AMapLocationClientOption();
            //设置返回地址信息，默认为true
            mLocationOption.setNeedAddress(true);
            //设置定位监听
            mlocationClient.setLocationListener(this);
            //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            //设置定位间隔,单位毫秒,默认为2000ms
//            mLocationOption.setInterval(5000);
            //==============
//            mLocationOption.setHttpTimeOut(2000);
            mLocationOption.setOnceLocation(true);
            mLocationOption.setMockEnable(true);
            mLocationOption.setSensorEnable(true);
            //===============
            //设置定位参数
            mlocationClient.setLocationOption(mLocationOption);
        }
        // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
        // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
        // 在定位结束后，在合适的生命周期调用onDestroy()方法
        // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
        //启动定位
        mlocationClient.startLocation();
    }


    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        locationCount++;
        if (amapLocation != null) {
            if (amapLocation.getErrorCode() == 0) {
                //定位成功回调信息，设置相关消息
//                amapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见定位类型表
//                amapLocation.getLatitude();//获取纬度
//                amapLocation.getLongitude();//获取经度
//                amapLocation.getAccuracy();//获取精度信息
//                amapLocation.getTime();//定位时间
//                amapLocation.getAddress();//地址，如果option中设置isNeedAddress为false，则没有此结果，网络定位结果中会有地址信息，GPS定位不返回地址信息。
//                amapLocation.getCountry();//国家信息
//                amapLocation.getProvince();//省信息
//                amapLocation.getCity();//城市信息
//                String district = amapLocation.getDistrict();//城区信息
//                String street = amapLocation.getStreet();//街道信息
//                amapLocation.getStreetNum();//街道门牌号信息
//                amapLocation.getCityCode();//城市编码
//                amapLocation.getAdCode();//地区编码
//                amapLocation.getAoiName();//获取当前定位点的AOI信息
                LogUtil.i(amapLocation.toString());
                Constants.INSTANCE.setLocation(amapLocation);
                mlocationClient.stopLocation();
                locationCount = 0;

//                if (Constants.selectedCity == null) {
                try {
                    long regionId = Long.parseLong(amapLocation.getAdCode());
                    Constants.INSTANCE.setRegionId(regionId);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
//                    CityDatebbase datebbase = CityDatebbase.getInstance(getApplicationContext());
//                    Region region = datebbase.findRegionsFromId((int) regionId);
//                    if (region != null) {
//                        if (!String.valueOf(regionId).endsWith("00")) {//城市都是00结尾，如果不是，则取父一级
//                            regionId = region.getParentId();
//                        }
//                    }
//
//                    Constants.selectedCity = new Region(regionId, amapLocation.getCity());
//
//                }
//                EventBus.getDefault().post(new LocationChangeEvent());
            } else {
                //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                LogUtil.e("AmapError", "location Error, ErrCode:"
                        + amapLocation.getErrorCode() + ", errInfo:"
                        + amapLocation.getErrorInfo());

                if (locationCount > locationFailMaxCount) {
                    mlocationClient.stopLocation();
                    locationCount = 0;
                }
            }

        } else {//定位失败，就让它继续定位，超过12次才停止定位
            if (locationCount > locationFailMaxCount) {
                mlocationClient.stopLocation();
                locationCount = 0;
            }
        }
    }

}
