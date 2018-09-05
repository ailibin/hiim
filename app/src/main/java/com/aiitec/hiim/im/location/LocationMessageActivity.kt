package com.aiitec.hiim

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import com.aiitec.hiim.annotation.ContentView
import com.aiitec.hiim.base.BaseKtActivity
import com.aiitec.openapi.utils.LogUtil
import com.amap.api.maps2d.AMap
import com.amap.api.maps2d.AMapOptions
import com.amap.api.maps2d.SupportMapFragment
import com.amap.api.maps2d.model.*
import kotlinx.android.synthetic.main.activity_location_message.*

/**
 * Created by ailibin on 2018/3/27.
 * 位置消息
 */
@ContentView(R.layout.activity_location_message)
class LocationMessageActivity : BaseKtActivity() {

    var aMap: AMap? = null
    //屏幕中心的marker
    private var screenMarker: Marker? = null
    private var LUJIAZUI: CameraPosition? = null
    private var mLatLng: LatLng? = null
    private var aMapFragment: SupportMapFragment? = null
    private val MAP_FRAGMENT_TAG = "map"
    private var title: String = ""
    private var address: String = ""


    override fun init(savedInstanceState: Bundle?) {
//        mapView.onCreate(savedInstanceState)
        addBaseStatusBarView()
        setColumnTitle("位置信息")
        mLatLng = intent.getParcelableExtra("LatLng")
        title = intent.getStringExtra("title")
        address = intent.getStringExtra("address")
        setBottomView()
        LogUtil.d("ailibin", "mLatLng: " + mLatLng.toString())
//        initMapView()
        setCameraPosition()
        initMapOptions()
        // 此方法须覆写，虚拟机需要在很多情况下保存地图绘制的当前状态。
    }

    private fun setBottomView() {
        if (TextUtils.isEmpty(title) && TextUtils.isEmpty(address)) {
            rlt_container.visibility = View.GONE
        } else {
            rlt_container.visibility = View.VISIBLE
            tv_title.text = title
            tv_detail.text = address
        }
    }


    private fun initMapOptions() {
        val aOptions = AMapOptions()
        aOptions.zoomGesturesEnabled(true)//通过手势缩放地图
        aOptions.scrollGesturesEnabled(false)// 禁止通过手势移动地图
        aOptions.camera(LUJIAZUI)
        if (aMapFragment == null) {
            aMapFragment = SupportMapFragment.newInstance(aOptions)
            val fragmentTransaction = supportFragmentManager
                    .beginTransaction()
//            fragmentTransaction.add(android.R.id.content, aMapFragment,
//                    MAP_FRAGMENT_TAG)
            fragmentTransaction.add(R.id.flt_map_container, aMapFragment,
                    MAP_FRAGMENT_TAG)
            fragmentTransaction.commit()
        }
    }

    //设置展示的位置
    private fun setCameraPosition() {
        LUJIAZUI = CameraPosition.Builder()
                .target(mLatLng).zoom(18f).bearing(0f).tilt(30f).build()
    }


//    private fun initMapView() {
//        //初始化地图控制器对象
//        if (aMap == null) {
//            aMap = mapView.map
//        }
//        aMap?.setOnMapLoadedListener { addMarkersToMap() }
//    }

    /**
     * 初始化AMap对象
     */
    private fun initMap() {
        if (aMap == null) {
            aMap = aMapFragment?.map// amap对象初始化成功
        }
        aMap?.setOnMapLoadedListener { addMarkersToMap() }
    }

    //设置一个中心中心表示点
    private fun addMarkersToMap() {
        val latLng = aMap?.cameraPosition?.target
        //这里拿到传过来的数据
        val screenPosition = aMap?.projection?.toScreenLocation(latLng)
        if (screenMarker == null) {
            screenMarker = aMap?.addMarker(MarkerOptions().anchor(0.5f, 0.5f)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.purple_pin)))
        }
        //设置Marker在屏幕上,不能跟随地图移动的
        screenMarker?.setPositionByPixels(screenPosition!!.x, screenPosition.y)
    }

    override fun onPause() {
        super.onPause()
//        mapView.onPause()
        aMapFragment?.onPause()
    }

    override fun onResume() {
        super.onResume()
//        mapView.onResume()
        aMapFragment?.onResume()
        initMap()
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        aMapFragment?.onSaveInstanceState(outState!!)
//        mapView.onSaveInstanceState(outState!!)
    }
}