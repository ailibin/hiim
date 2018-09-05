package com.aiitec.widgets.pickerview.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.aiitec.widgets.pickerview.model.Region;

import java.util.ArrayList;

/**
 * 城市列表选择工具类
 *
 * @author Anthony
 *         modify xiaobing
 */
public class CityDBUtils {

    private CityDatebbase aiiOpenDb;
    public static final int ALL = 0x00;
    public static final int PROVICE = 0x01;
    public static final int CITY = 0x02;
    public static final int COUNTY = 0x03;
    private InitCitysThread findCitysThread;
    private SearchCitysThread searchCitysThread;
    private InitAllCitysThread findAllCitySThread;
    private OnCityInitListener onCityInitListener;

    public CityDBUtils(Context context) {
        aiiOpenDb = CityDatebbase.getInstance(context);
    }

    /**
     * 初始化省
     */
    public void initProvince() {
        findCitysThread = null;
        findCitysThread = new InitCitysThread();
        findCitysThread.findCitysByParent(1, PROVICE);
        findCitysThread.start();
    }

    /**
     * 初始化城市
     */
    public void initCities(Region region, int areaLevel) {
        findCitysThread = null;
        findCitysThread = new InitCitysThread();
        findCitysThread.findCitysByParent(region, areaLevel);
        findCitysThread.start();
    }

    /**
     * 根据城市名搜索城市
     *
     * @param city      城市名
     * @param areaLevel 省、市、县代号
     */
    public void initSearchCities(final String city, int areaLevel) {
        searchCitysThread = null;
        searchCitysThread = new SearchCitysThread();
        searchCitysThread.findCityByCityName(city, areaLevel);
        searchCitysThread.start();
    }

    /**
     * 初始化查找所有城市
     */
    public void initAllCities() {
        findAllCitySThread = null;
        findAllCitySThread = new InitAllCitysThread();
        findAllCitySThread.start();
    }

    /**
     * 初始化城市的的线程
     */
    private class InitCitysThread extends Thread {
        private Region parentRegion;
        private int regionId;
        private int areaLevel;

        void findCitysByParent(int regionId, int areaLevel) {
            this.regionId = regionId;
            this.areaLevel = areaLevel;
        }

        void findCitysByParent(Region region, int areaLevel) {
            this.parentRegion = region;
            this.regionId = region.getId();
            this.areaLevel = areaLevel;
        }

        @Override
        public void run() {
            super.run();
            try {
                ArrayList<Region> list = aiiOpenDb.findCityRegions(regionId);
                Message msg = new Message();
                msg.what = areaLevel;
                if (parentRegion != null) {
                    MsgInfo info = new MsgInfo();
                    info.setParentRegion(parentRegion);
                    info.setChildRegionList(list);
                    msg.obj = info;
                    msg.arg2 = 11;
                } else {
                    msg.obj = list;
                    msg.arg2 = -1;
                }
                handler.sendMessage(msg);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 初始化所有城市的的线程
     */
    private class InitAllCitysThread extends Thread {
        private int areaLevel;

        InitAllCitysThread() {
            this.areaLevel = ALL;
        }

        @Override
        public void run() {
            super.run();
            try {
                ArrayList<Region> list = aiiOpenDb.findAllRegions();
                Message msg = new Message();
                msg.what = areaLevel;
                msg.obj = list;
                handler.sendMessage(msg);
            } catch (Exception e) {
                Log.i("xiaobing", "初始化所有城市的的线程出错了");
                e.printStackTrace();
            }
        }
    }

    /**
     * 模糊查询城市的线程
     */
    private class SearchCitysThread extends Thread {
        private String city;
        private int areaLevel;

        void findCityByCityName(String city, int areaLevel) {
            this.city = city;
            this.areaLevel = areaLevel;
        }

        @Override
        public void run() {
            super.run();
            try {
                ArrayList<Region> list = aiiOpenDb.findRegionsFromSearch(city);
                Message msg = new Message();
                msg.what = areaLevel;
                msg.obj = list;
                handler.sendMessage(msg);
            } catch (Exception e) {
                Log.i("xiaobing", "模糊查询城市的线程出错了");
                e.printStackTrace();
            }
        }
    }

    /**
     * 初始化地区后的回调接口
     */
    public interface OnCityInitListener {

        void getCitys(Region parentRegion, ArrayList<Region> regions, int areaLevel);
    }

    /**
     * 设置回调接口
     *
     * @param onCityInitListener
     */
    public void setOnCityInitListener(OnCityInitListener onCityInitListener) {
        this.onCityInitListener = onCityInitListener;
    }

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            Region parentRegion = null;
            ArrayList<Region> citysList = new ArrayList<>();
            if (msg.arg2 == 11) {
                MsgInfo info = (MsgInfo) msg.obj;
                parentRegion = info.getParentRegion();
                citysList = info.getChildRegionList();
            } else {
                citysList = (ArrayList<Region>) msg.obj;
            }
            if (onCityInitListener != null) {
                onCityInitListener.getCitys(parentRegion, citysList, msg.what);
            }
        }
    };

    private class MsgInfo {
        private Region parentRegion;
        private ArrayList<Region> childRegionList;

        public Region getParentRegion() {
            return parentRegion;
        }

        public void setParentRegion(Region parentRegion) {
            this.parentRegion = parentRegion;
        }

        public ArrayList<Region> getChildRegionList() {
            return childRegionList;
        }

        public void setChildRegionList(ArrayList<Region> childRegionList) {
            this.childRegionList = childRegionList;
        }
    }

    /**
     * 销毁已创建的线程
     */
    public void destory() {
        findAllCitySThread = null;
        findCitysThread = null;
        searchCitysThread = null;
        aiiOpenDb = null;
    }

    /**
     * 获取省市县所有城市集合
     */
    /*@SuppressLint("StaticFieldLeak")
    public void getAllRegionsByGrade(final GetAllRegionsByGradeListener allRegionsListener) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {


                ArrayList<Region> allProvinces = aiiOpenDb.findCityRegions(1);
                if (allProvinces != null) {
                    allRegionsListener.allProvinces(allProvinces);
                }
                ArrayList<ArrayList<Region>> allCitys = new ArrayList<>();
                ArrayList<ArrayList<ArrayList<Region>>> allCountys = new ArrayList<>();
                for (Region province : allProvinces) {
                    ArrayList<Region> oneProvinceCitysList = aiiOpenDb.findCityRegions(province.getId());
                    ArrayList<ArrayList<Region>> oneProvinceCountyList = new ArrayList<>();
                    allCitys.add(oneProvinceCitysList);
                    for (Region city : oneProvinceCitysList) {
                        try {
                            ArrayList<Region> oneCityCountyList = aiiOpenDb.findCityRegions(city.getId());
                            oneProvinceCountyList.add(oneCityCountyList);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    allCountys.add(oneProvinceCountyList);
                }
                allRegionsListener.allCitys(allCitys);
                allRegionsListener.allCountys(allCountys);
                return null;
            }
        }.execute();
    }*/

    @SuppressLint("StaticFieldLeak")
    public void getAllRegionsByGrade(final GetAllRegionsByGradeListener allRegionsListener) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {


                ArrayList<Region> allProvinces = aiiOpenDb.findProvinceRegions(1);
                ArrayList<Region> allCity = aiiOpenDb.findCitysRegions(2);
                ArrayList<Region> allArea = aiiOpenDb.findCitysRegions(4);
                if (allProvinces != null) {
                    allRegionsListener.allProvinces(allProvinces);
                }
                ArrayList<ArrayList<Region>> allCitys = new ArrayList<>();
                ArrayList<ArrayList<ArrayList<Region>>> allCountys = new ArrayList<>();
                for (Region province : allProvinces) {
                    ArrayList<Region> oneProvinceCitysList = new ArrayList<>();
                    for (Region city : allCity) {
                        if(city.getParentId() == province.getId()){
                            oneProvinceCitysList.add(city);
                        }
                    }
                    //allCitys.add(oneProvinceCitysList);

//                    ArrayList<Region> oneProvinceCitysList = aiiOpenDb.findCityRegions(province.getId());
                    ArrayList<ArrayList<Region>> oneProvinceCountyList = new ArrayList<>();
                    allCitys.add(oneProvinceCitysList);
                    for (Region city : oneProvinceCitysList) {
                        try {
                            ArrayList<Region> oneCityCountyList = new ArrayList<>();
                            for (Region area : allArea) {
                                if(area.getParentId() == city.getId()){
                                    oneCityCountyList.add(area);
                                }
                            }
                            oneProvinceCountyList.add(oneCityCountyList);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    allCountys.add(oneProvinceCountyList);
                }
                allRegionsListener.allCitys(allCitys);
                allRegionsListener.allCountys(allCountys);
                return null;
            }
        }.execute();
    }

    /**
     * 获取省市县所有城市集合的回调接口
     */
    public interface GetAllRegionsByGradeListener {
        /**
         * 获取所有省级城市的回调方法
         */
        void allProvinces(ArrayList<Region> provinceList);

        /**
         * 获取所有市级城市的回调方法
         */
        void allCitys(ArrayList<ArrayList<Region>> cityList);

        /**
         * 获取所有县级城市的回调方法
         */
        void allCountys(ArrayList<ArrayList<ArrayList<Region>>> countyList);
    }
}
