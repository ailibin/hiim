package com.aiitec.hiim.im.location

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.os.Environment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.view.WindowManager
import com.aiitec.hiim.R
import com.aiitec.hiim.annotation.ContentView
import com.aiitec.hiim.base.App
import com.aiitec.hiim.base.BaseKtActivity
import com.aiitec.hiim.im.location.util.AMapUtil
import com.aiitec.hiim.im.location.util.ToastUtil
import com.aiitec.hiim.utils.BaseUtil
import com.aiitec.openapi.utils.LogUtil
import com.aiitec.openapi.utils.ScreenUtils
import com.amap.api.location.AMapLocation
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClientOption
import com.amap.api.location.AMapLocationListener
import com.amap.api.maps2d.AMap
import com.amap.api.maps2d.CameraUpdateFactory
import com.amap.api.maps2d.LocationSource
import com.amap.api.maps2d.model.*
import com.amap.api.services.core.AMapException
import com.amap.api.services.core.LatLonPoint
import com.amap.api.services.core.PoiItem
import com.amap.api.services.core.SuggestionCity
import com.amap.api.services.help.Inputtips
import com.amap.api.services.help.InputtipsQuery
import com.amap.api.services.help.Tip
import com.amap.api.services.poisearch.PoiResult
import com.amap.api.services.poisearch.PoiSearch
import com.herentan.giftfly.ui.location.LocationAdapter
import com.herentan.giftfly.ui.location.PoiLocationAdapter
import com.herentan.giftfly.ui.location.entity.Area
import com.herentan.giftfly.ui.location.entity.LatLon
import kotlinx.android.synthetic.main.activity_location.*
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


/**
 * Created by ailibin on 2018/1/14.
 * 发送位置的activity(需要定位到当前的城市位置,然后根据所在的城市来搜索周边)
 */
@ContentView(R.layout.activity_location)
class LocationActivity : BaseKtActivity(),
        PoiSearch.OnPoiSearchListener,
        TextWatcher, Inputtips.InputtipsListener,
        AMap.OnMapScreenShotListener,
        LocationSource, AMapLocationListener {

    var aMap: AMap? = null

    /**
     * 定位相关的监听和实体
     */
    private var mListener: LocationSource.OnLocationChangedListener? = null
    private var mlocationClient: AMapLocationClient? = null
    private var mLocationOption: AMapLocationClientOption? = null

    /**
     * 周边搜索相关初始化
     */
    private var poiResult: PoiResult? = null // poi返回的结果
    private var currentPage = 0// 当前页面，从0开始计数
    private var query: PoiSearch.Query? = null// Poi查询条件类
    private var lp = LatLonPoint(39.993167, 116.473274)//位置点的经度和纬度

    /**
     * 当前位置的经度和纬度
     */
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    private var poiSearch: PoiSearch? = null
    private var poiItems: List<PoiItem>? = null// poi数据
    private var city = "北京市"
    private var cityId: Long = 0
    //屏幕中心的marker
    private var screenMarker: Marker? = null
    private var location: AMapLocation? = null
    //装图片地址
    private var datas = ArrayList<String>()
    //上传成功后的文件集相关
    private var uploadedFileList = ArrayList<com.aiitec.openapi.model.File>()
    private var pictureFile: com.aiitec.openapi.model.File? = null
    //填充物颜色
    private val STROKE_COLOR = Color.argb(180, 3, 145, 255)
    private val FILL_COLOR = Color.argb(10, 0, 0, 180)

    /**
     * 搜索adapter
     */
    private var adapter: LocationAdapter? = null
    /**
     * 周边底部的adapter(用户最终需要选择的)
     */
    private var poiAdapter: PoiLocationAdapter? = null

    /**
     * 一开始就初始化的底部数据
     */
    private var poiAddressDatas = ArrayList<Area>()
    /**
     * 搜索出来的地址
     */
    private var addressDatas = ArrayList<Area>()
    /**
     * 已经选择的地址也放在一个集合中
     */
    private var selectedAddDatas = ArrayList<Area>()
    private var searchKey = ""
    //默认刷新
    private var mIsRefresh = true
    //定位一次就够了
    private var isLocation = false

    override fun init(savedInstanceState: Bundle?) {
        addBaseStatusBarView()
        setColumnTitle("位置")
        setRightBtnVisible(true)
        setRightBtnText("发送", ContextCompat.getColor(this, R.color.black3))

        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
        initAddressAdapter()
        // 此方法须覆写，虚拟机需要在很多情况下保存地图绘制的当前状态。
        mapView.onCreate(savedInstanceState)
        et_input_search_content.addTextChangedListener(this)
        initMapView(true)
        setListener()
    }

    /**
     * 初始化地址搜索辅助的adapter
     */
    private fun initAddressAdapter() {
        //搜索出来的顶部adapter
        adapter = LocationAdapter(this, addressDatas)
        val layoutManager = LinearLayoutManager(this)
        recy_search.adapter = adapter
        recy_search.layoutManager = layoutManager

        //一进入地图就初始化的底部周边的adapter
        poiAdapter = PoiLocationAdapter(this, poiAddressDatas)
        val poiLayoutManager = LinearLayoutManager(this)
        //把底部recycleView高度设置为屏幕的百分之40
        val height = ScreenUtils.getScreenHeight(this) * 0.4
        recy_poiSearch?.layoutParams?.height = height.toInt()
        recy_poiSearch?.adapter = poiAdapter
        recy_poiSearch?.layoutManager = poiLayoutManager
    }

    private fun initMapView(isRefresh: Boolean) {
        //初始化地图控制器对象
        if (aMap == null) {
            aMap = mapView.map
        }
        mIsRefresh = isRefresh
        setUpMap()
        //中心添加一个跳动marker
        aMap?.setOnMapLoadedListener { addMarkersToMap() }
        // 设置可视范围变化时的回调的接口方法(地图移动的时候回调方法)
        aMap?.setOnCameraChangeListener(object : AMap.OnCameraChangeListener {
            override fun onCameraChange(position: CameraPosition) {
                //重新查找周边,获取当前的经纬度
                val latLon = position.target
                lp = LatLonPoint(latLon.latitude, latLon.longitude)
                doSearchQuery()
            }

            override fun onCameraChangeFinish(position: CameraPosition) {
                //camera完成之后,设置可以刷新底部地址数据
                mIsRefresh = true
                //设置地图的中心点的坐标
                aMap?.moveCamera(CameraUpdateFactory.newCameraPosition(position))
            }
        })
    }


    /**
     * 设置一些amap的属性
     */
    private fun setUpMap() {
        aMap?.setLocationSource(this)// 设置定位监听
        aMap?.uiSettings?.isMyLocationButtonEnabled = true// 设置默认定位按钮是否显示
        aMap?.isMyLocationEnabled = true// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        setupLocationStyle()
    }

    /**
     * 在地图上添加marker
     */
    private fun addMarkersToMap() {
        addMarkerInScreenCenter()
    }

    /**
     * 在地图上添加marker
     */
    private fun addMarkersToMap(latLng: LatLng) {
        addMarkerInScreenCenter(latLng)
    }

    /**
     * 在屏幕中心添加一个Marker
     */
    private fun addMarkerInScreenCenter() {
        val latLng = aMap?.cameraPosition?.target
//        val cameraUpdate = CameraUpdateFactory.newCameraPosition(CameraPosition(
//                latLng, 18f, 30f, 30f))
//        aMap?.moveCamera(cameraUpdate)
        val screenPosition = aMap?.projection?.toScreenLocation(latLng)
        if (screenMarker == null) {
            screenMarker = aMap?.addMarker(MarkerOptions()
                    .anchor(0.5f, 0.5f)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.purple_pin)))
        }
        //设置Marker在屏幕上,不跟随地图移动(需要移动),经纬度转屏幕位置
        screenMarker?.setPositionByPixels(screenPosition!!.x, screenPosition.y)

    }

    private fun addMarkerInScreenCenter(newLatLng: LatLng) {
        val latLng = aMap?.cameraPosition?.target
        val cameraUpdate = CameraUpdateFactory.newCameraPosition(CameraPosition(
                newLatLng, 18f, 30f, 30f))
        aMap?.moveCamera(cameraUpdate)
        val screenPosition = aMap?.projection?.toScreenLocation(latLng)
        if (screenMarker == null) {
            screenMarker = aMap?.addMarker(MarkerOptions()
                    .anchor(0.5f, 0.5f)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.purple_pin)))
        }
        //设置Marker在屏幕上,不跟随地图移动(需要移动),经纬度转屏幕位置
        screenMarker?.setPositionByPixels(screenPosition!!.x, screenPosition.y)

    }

    /**
     * 对地图进行截屏
     */
    fun getMapScreenShot() {
        aMap?.getMapScreenShot(this)
    }


    /**
     * 带有地图渲染状态的截屏回调方法。
     * 根据返回的状态码，可以判断当前视图渲染是否完成。
     *
     * @param bitmap 调用截屏接口返回的截屏对象。
     *
     */
    override fun onMapScreenShot(bitmap: Bitmap?) {

        val sdf = SimpleDateFormat("yyyyMMddHHmmss")
        if (null == bitmap) {
            return
        }
        try {
            val path = Environment.getExternalStorageDirectory().toString() + "/giftfly_" + sdf.format(Date()) + ".png"
            val fos = FileOutputStream(path)
            val b = bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
            try {
                fos.flush()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            try {
                fos.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }

            if (b) {
                //先上传图片到服务器,拿到路径后再展示
                toUpLoadImage(path)
            } else {
                ToastUtil.show(this@LocationActivity, "截屏失败")
            }
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
    }

    /**
     * 上传图片到服务器(这种方式会造成服务器压力过大,暂时这样处理把)
     */
    private fun toUpLoadImage(path: String) {
        datas.add(path)
//        uploadFiles()
    }

//    private fun uploadFiles() {
//        val type = 1
//        val tempDatas = ArrayList<File>()
//        datas.filter { it != "add" }.map { File(it) }
//                .forEach { tempDatas.add(it) }
//        LogUtil.d("ailibin", "tempDatas: " + tempDatas.toString())
//        customDialogShow()
//        UploadFileUtils.requestUploadFiles(this, tempDatas, type, object : AIIResponse<FileListResponseQuery>(this) {
//            override fun onSuccess(response: FileListResponseQuery?, index: Int) {
//                super.onSuccess(response, index)
//                customDialogDismiss()
//                uploadedFileList.clear()
//                uploadedFileList = (response?.files as ArrayList<com.aiitec.openapi.model.File>?)!!
//                pictureFile = uploadedFileList[0]
//
//                //上传成功的文件路径(拿到绝对路径)
//                val path = pictureFile?.path
//                LogUtil.d("ailibin", "path: " + path)
//                val imagePath = ImagePathUtil.getWholeImagePath(path)
//                if (imageListener != null) {
//                    imageListener?.onSuccess(imagePath)
//                }
//            }
//
//            override fun onServiceError(content: String?, status: Int, index: Int) {
//                super.onServiceError(content, status, index)
//                customDialogDismiss()
//            }
//
//            override fun onFailure(content: String?, index: Int) {
//                super.onFailure(content, index)
//                customDialogDismiss()
//            }
//        })
//    }

    private var imageListener: ImageListener? = null

    interface ImageListener {
        fun onSuccess(path: String)
    }

    private fun setImageListener(imageListener: ImageListener?) {
        this.imageListener = imageListener
    }

    private fun setupLocationStyle() {
        // 自定义系统定位蓝点
        val myLocationStyle = MyLocationStyle()
        // 自定义定位蓝点图标
        myLocationStyle.myLocationIcon(BitmapDescriptorFactory.fromResource(R.drawable.gps_point))
        // 自定义精度范围的圆形边框颜色
        myLocationStyle.strokeColor(STROKE_COLOR)
        //自定义精度范围的圆形边框宽度
        myLocationStyle.strokeWidth(3f)
        // 设置圆形的填充颜色
        myLocationStyle.radiusFillColor(FILL_COLOR)
        // 将自定义的 myLocationStyle 对象添加到地图上
        aMap?.setMyLocationStyle(myLocationStyle)

    }


    /**
     * poi没有搜索到数据，返回一些推荐城市的信息
     */
    private fun showSuggestCity(cities: List<SuggestionCity>) {
        var infomation = "推荐城市\n"
        for (i in cities.indices) {
            infomation += ("城市名称:" + cities[i].cityName + "城市区号:"
                    + cities[i].cityCode + "城市编码:"
                    + cities[i].adCode + "\n")
        }
        ToastUtil.show(this, infomation)
    }


    /**
     * 开始进行poi搜索
     */
    private fun doSearchQuery() {
        currentPage = 0
        // 第一个参数表示搜索字符串，第二个参数表示poi搜索类型，第三个参数表示poi搜索区域（空字符串代表全国）
        query = PoiSearch.Query("", "", city)
        query?.pageSize = 10// 设置每页最多返回多少条poiitem
        query?.pageNum = currentPage// 设置查第一页
        if (lp != null) {
            poiSearch = PoiSearch(this, query)
            poiSearch?.setOnPoiSearchListener(this)
            // 设置搜索区域为以lp点为圆心，其周围5000米范围
            poiSearch?.bound = PoiSearch.SearchBound(lp, 2000, true)
            // 异步搜索
            poiSearch?.searchPOIAsyn()
        }
    }

    /**
     * 输入内容变化的监听
     */
    override fun afterTextChanged(p0: Editable?) {
    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        val newText = s.toString().trim({ it <= ' ' })
        if (TextUtils.isEmpty(newText)) {
            //搜索内容为空,就不显示列表数据了
            addressDatas.clear()
            adapter?.notifyDataSetChanged()
        } else {
            //关键字
            searchKey = newText
            if (!AMapUtil.IsEmptyOrNullString(newText)) {
                val inputquery = InputtipsQuery(newText, city)
                val inputTips = Inputtips(this@LocationActivity, inputquery)
                inputTips.setInputtipsListener(this)
                inputTips.requestInputtipsAsyn()
            }
        }
    }

    /**
     * 输入内容提示列表(推荐列表)
     */
    override fun onGetInputtips(tipList: MutableList<Tip>?, rCode: Int) {
        LogUtil.d("ailibin", "tipList: " + tipList.toString() + "point: " + tipList?.get(0)?.point.toString())
        addressDatas.clear()
        if (rCode == AMapException.CODE_AMAP_SUCCESS) {
            // 正确返回
            recy_search.visibility = View.VISIBLE
            for (i in tipList!!.indices) {
                val area = Area()
                val latLon = LatLon()
                //地址名称(如协盛大厦,协和医院)
                area.name = tipList[i].name
                //地址要详细地址,就需要拼接字符串
                area.address = tipList[i].district + tipList[i].address
                //设置地区编号
                area.regionId = tipList[i].adcode
                //关键字
                area.searchKey = searchKey
                //设置经纬度
                latLon.latitude = tipList[i].point.latitude
                latLon.longitude = tipList[i].point.longitude
                area.latLon = latLon
                addressDatas.add(area)
            }
            //设置默认选择第一项
            val defaultArea = addressDatas[0]
            defaultArea.isSelected = true
            addressDatas[0] = defaultArea
            adapter?.notifyDataSetChanged()
        } else {
            recy_search.visibility = View.GONE
            ToastUtil.showerror(this@LocationActivity, rCode)
        }

    }

    /**
     * 周边搜索相关实现方法
     */
    override fun onPoiItemSearched(p0: PoiItem?, p1: Int) {

    }

    /**
     * 搜索结果
     */
    override fun onPoiSearched(result: PoiResult?, rcode: Int) {
        poiAddressDatas.clear()
        if (rcode == AMapException.CODE_AMAP_SUCCESS) {
            isLocation = true
            if (result?.query != null) {// 搜索poi的结果
                if (result.query == query) {// 是否是同一条
                    poiResult = result
                    poiItems = poiResult?.pois// 取得第一页的poiitem数据，页数从数字0开始
                    //这里把他保存在我们自定义的实体和集合中,便于展示
                    for (poiItem in poiItems!!) {
                        val area = Area()
                        //地址名称
                        area.name = poiItem.title
                        //地址编号
                        area.regionId = poiItem.adCode
                        //这里详细地址不知道用哪一个,就先用着这个(provinceName+cityName+adName+snippet)
                        area.address = poiItem.provinceName + poiItem.cityName + poiItem.adName + poiItem.snippet
                        //设置经纬度坐标点
                        val latLon = LatLon()
                        latLon.latitude = poiItem.latLonPoint.latitude
                        latLon.longitude = poiItem.latLonPoint.longitude
                        area.latLon = latLon

                        poiAddressDatas.add(area)
                        LogUtil.d("ailibin", "adName: " + poiItem.adName + " title: " + poiItem.title
                                + " snippet: " + poiItem.snippet + " subPois: " + poiItem.subPois
                                + " businessArea: " + poiItem.businessArea + " provinceName: " + poiItem.provinceName
                                + " cityName: " + poiItem.cityName)
                    }
                    poiAdapter?.notifyDataSetChanged()
                    //这里默认设置第一个位置的item被选中
//                    val size = poiAddressDatas.size
                    selectedAddDatas.clear()
                    if (poiAddressDatas.size > 0) {
                        val area = poiAddressDatas[0]
                        area.isSelected = true
                        poiAddressDatas[0] = area
                        selectedAddDatas.add(poiAddressDatas[0])
                        LogUtil.d("ailibin", "newSelectDatas: " + selectedAddDatas.toString())

                    }

                    val suggestionCities = poiResult?.searchSuggestionCitys// 当搜索不到poiitem数据时，会返回含有搜索关键字的城市信息
                    if (poiItems != null && poiItems?.size!! > 0) {
                        //改变一下地图相机
//                        addMarkersToMap()
                        addMarkersToMap()
                    } else if (suggestionCities != null && suggestionCities.size > 0) {
                        showSuggestCity(suggestionCities)
                    } else {
                        ToastUtil.show(this.applicationContext, "对不起，没有搜索到相关数据！")
                    }
                }
            } else {
                ToastUtil.show(this.applicationContext, "对不起，没有搜索到相关数据！")
            }
        } else {
//            ToastUtil.showerror(this.applicationContext, rcode)
        }
    }


    /**-------------定位相关的实现方法开始--------------**/
    /**
     * 停止定位
     */
    override fun deactivate() {
        mListener = null
        if (mlocationClient != null) {
            mlocationClient?.stopLocation()
            mlocationClient?.onDestroy()
        }
        mlocationClient = null

    }

    /**
     * 激活定位
     */
    override fun activate(listener: LocationSource.OnLocationChangedListener?) {
        mListener = listener
        if (mlocationClient == null) {
            mlocationClient = AMapLocationClient(this)
            mLocationOption = AMapLocationClientOption()
            //设置定位监听
            mlocationClient?.setLocationListener(this)
            //设置为高精度定位模式
            mLocationOption?.locationMode = AMapLocationClientOption.AMapLocationMode.Hight_Accuracy
            //设置定位参数
            mlocationClient?.setLocationOption(mLocationOption)
            // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
            // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
            // 在定位结束后，在合适的生命周期调用onDestroy()方法
            // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
            mlocationClient?.startLocation()
        }

    }


    override fun onLocationChanged(amapLocation: AMapLocation?) {
        LogUtil.d("ailibin", "amapLocation: " + amapLocation.toString())
        if (mListener != null && amapLocation != null) {
            if (amapLocation != null && amapLocation.errorCode == 0) {
                //定位成功
                mListener?.onLocationChanged(amapLocation)// 显示系统小蓝点
                //获取当前的城市
                city = amapLocation.city
                //获取当前的城市id
                cityId = amapLocation.cityCode.toLong()
//                LogUtil.d("ailibin", "cityId: " + cityId)
                //获取当前位置的纬度
                latitude = amapLocation.latitude
                //获取当前位置的经度
                longitude = amapLocation.longitude
                //设置坐标
                lp = LatLonPoint(latitude, longitude)
                //设置地图中心点的坐标(向着中心点的坐标进行缩放)
                aMap?.moveCamera(CameraUpdateFactory.zoomTo(18f))
                //开始自动搜索周边地区(定位一次就不用自动搜索了)
                doSearchQuery()
            } else {
                val errText = "定位失败," + amapLocation.errorCode + ": " + amapLocation.errorInfo
                LogUtil.e("ailibin", errText)
            }
        }
    }

    /**-------------定位相关的实现方法结束--------------**/


    /*-----------------设置监听方法------------------------------*/
    private fun setListener() {
        setRightBtnClickListener(View.OnClickListener {
            //点击发送按钮(发送地理位置)
            if (selectedAddDatas.size == 0) {
                BaseUtil.showToast("请选择一个地理位置")
            } else {
                //自定义消息发送地理位置,这里返回位置信息(包括截屏信息)
                getMapScreenShot()
                setImageListener(object : ImageListener {
                    override fun onSuccess(path: String) {
                        val intent = Intent()
                        val area = selectedAddDatas[0]
                        area.imagePath = path
                        LogUtil.d("ailibin", "area: " + area.toString())
                        intent.putExtra("location", area)
                        setResult(Activity.RESULT_OK, intent)
                        finish()
                    }
                })
            }

        })

        adapter?.setOnRecyclerViewItemClickListener { v, position ->
            if (position >= 0) {
                //单选清空之前已选的选项（除当前点击的item外),其它的位置设置未选择状态,当前点击的位置状态信息取反
                for (index in addressDatas.indices) {
                    //如果当前点击的位置不等于数据的角标,全都设置成未选择状态
                    if (index != position) {
                        addressDatas[index].isSelected = false
                    } else {
                        //取反
                        val isSelect = addressDatas[index].isSelected
                        addressDatas[index].isSelected = !isSelect
                    }
                    recy_search.visibility = View.GONE
                    BaseUtil.hideKeyboard(this)
                }
                checkSelected()
            }
        }


        poiAdapter?.setOnRecyclerViewItemClickListener { v, position ->
            if (position >= 0) {
                //单选清空之前已选的选项（除当前点击的item外),其它的位置设置未选择状态,当前点击的位置状态信息取反
                //单选清空之前已选的选项（除当前点击的item外）
                poiAddressDatas.filterIndexed { index, d -> index != position }
                        .forEach { it.isSelected = false }
                val isSelect = poiAddressDatas[position].isSelected
                poiAddressDatas[position].isSelected = !isSelect
                poiAdapter?.notifyDataSetChanged()
                checkPoiSelected()
            }
        }
    }
    /*-----------------设置监听方法结束------------------------------*/

    /**
     * 已经选择的地址放在一个集合中
     */
    private fun checkSelected() {
        selectedAddDatas.clear()
        for (area in addressDatas) {
            if (area.isSelected) {
                selectedAddDatas.add(area)
                //这里一点击消失掉当前的列表,把选择的数据的经纬度传到主界面重新搜索一次,同时隐藏软件盘,还要初始化camera地图
                lp = LatLonPoint(area.latLon!!.latitude, area.latLon!!.longitude)
                //这里还是不用再次搜索,因为再次搜索和搜索出来选择的数据就不同了,只需要更改首页位置列表的第一项数据即可
                doSearchQuery()
                //这里应该停止定位,不然底部数据列表老是刷新
                if (mlocationClient != null) {
                    mlocationClient?.stopLocation()
                }
                //开个线程搜索速度更快
                App.getInstance().cachedThreadPool.execute(Runnable {
                    val latLon = LatLng(area.latLon!!.latitude, area.latLon!!.longitude)
                    addMarkersToMap(latLon)
                })
                val area = selectedAddDatas[0]
                area.isSelected = true
                poiAddressDatas[0] = area
                poiAdapter?.notifyDataSetChanged()
            }
        }
        adapter?.notifyDataSetChanged()
    }

    /**
     * 这里也要变更选择的地址
     */
    private fun checkPoiSelected() {
        selectedAddDatas.clear()
        for (area in poiAddressDatas) {
            if (area.isSelected) {
                selectedAddDatas.add(area)
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
        if (null != mlocationClient) {
            mlocationClient?.onDestroy()
        }
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
        //停止定位
        deactivate()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState!!)
    }
}