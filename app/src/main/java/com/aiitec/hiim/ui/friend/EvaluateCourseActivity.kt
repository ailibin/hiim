package com.aiitec.hiim.ui.friend

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Rect
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import com.aiitec.hiim.R
import com.aiitec.hiim.annotation.ContentView
import com.aiitec.hiim.base.BaseKtActivity
import com.aiitec.hiim.base.Constants
import com.aiitec.hiim.im.location.util.ToastUtil
import com.aiitec.hiim.im.utils.AiiUtil
import com.aiitec.hiim.im.utils.LogUtil
import com.aiitec.hiim.utils.BaseUtil
import com.aiitec.hiim.utils.EditTextCharLimitUtil
import com.aiitec.hiim.utils.ScreenUtils
import com.aiitec.widgets.crop.util.UploadPhotoHelper
import com.aiitec.widgets.pickerview.OptionsPickerView
import com.aiitec.widgets.pickerview.TimePickerView
import com.aiitec.widgets.pickerview.model.Region
import com.aiitec.widgets.pickerview.utils.CityDBUtils
import kotlinx.android.synthetic.main.activity_evaluate_course.*
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

/**
 * 需要评价的课程
 */
@ContentView(R.layout.activity_evaluate_course)
class EvaluateCourseActivity : BaseKtActivity() {

    //选择图片的工具类
    private lateinit var selectPictureHelper: UploadPhotoHelper
    /**
     * 这里测试选择地区
     */
    private lateinit var cityDbUtils: CityDBUtils
    private var allProvinceList: ArrayList<Region>? = null
    private var allCityList: ArrayList<ArrayList<Region>>? = null
    private var allCountyList: ArrayList<ArrayList<ArrayList<Region>>>? = null
    private var selectedRegionId = -1
    //上传的头像图片
//    private var uploadHeaderFile: com.aiitec.openapi.model.File? = null
    private var courseId: Long? = null

    override fun init(savedInstanceState: Bundle?) {

        addBaseStatusBarView()
        setColumnTitle("课程评价")
        courseId = intent.getLongExtra(Constants.COURSE_ID, 0)
        initConfiguration()
        setTextLimitWatchListener(et_evaluate_for_course)
        setListener()

    }

    private fun initConfiguration() {

//        selectPictureHelper = UploadPhotoHelper(this@EvaluateCourseActivity, true, "", 2)
        selectPictureHelper = UploadPhotoHelper(this@EvaluateCourseActivity, true, "", 1)
        //初始化载入地区信息的帮助类
        cityDbUtils = CityDBUtils(this@EvaluateCourseActivity)
        cityDbUtils.getAllRegionsByGrade(object : CityDBUtils.GetAllRegionsByGradeListener {

            override fun allProvinces(provinceList: ArrayList<Region>?) {
                allProvinceList = provinceList
            }

            override fun allCitys(cityList: ArrayList<ArrayList<Region>>?) {
                allCityList = cityList
            }

            override fun allCountys(countyList: ArrayList<ArrayList<ArrayList<Region>>>?) {
                allCountyList = countyList
            }
        })
    }


    private fun setListener() {
        tv_commit_evaluate.setOnClickListener {
            //点击评论提交
            if (checkInfo()) {
//                BaseUtil.showToast("点击评论提交")
                val content = et_evaluate_for_course.text.toString().trim()
                val rating = br_for_evaluate_course.rating.toInt()
            }
        }

        et_evaluate_for_course.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

        })

        tv_upload.setOnClickListener {
            //测试上传头像
            toUploadPic(1)
        }

        tv_select_district.setOnClickListener {
            //选择地区
            selectArea()
        }

        tv_select_birthday.setOnClickListener {
            //选择生日
            selectBirthday()
        }


    }


    //上传身份证件照
    private fun toUploadPic(action: Int) {
        selectPictureHelper.getPhotoFromPictureLibrary()
        selectPictureHelper.setGetUploadFileSuccessListener { file, _ ->
            val pictureUploadMap = HashMap<Int, File>()
            pictureUploadMap[1] = file
            //上传身份证件正面图
            LogUtil.e("ailibin", "file: " + file)
        }
    }


    fun screenshot(view: View?): File? {
        if (view == null) {
            return null
        }
        var cacheBitmap = createBitmap(view)
        val file = saveBitmap(cacheBitmap!!)
        LogUtil.e("ailibin", "file: $file")
        file?.let {
            LogUtil.e("ailibin", "file--absolutePath: " + it.absolutePath)
            //这里用文件绝对路径,在android7.0以上不行,在android6.0以下是可以,所以还是要设置bitmap到分享中去
//            shareDialog?.setShareContent(it.absolutePath, "")
//            shareDialog?.setShareContent(cacheBitmap, "")
        }
        return file
    }

    /** 保存方法  */
    fun saveBitmap(img: Bitmap): File? {
        if (!AiiUtil.isSDCardEnable()) {
            ToastUtil.show(this, "SD卡不可用， 请检查后再尝试！")
            return null
        }
        if (img == null) {
            return null
        }
        val format = SimpleDateFormat("MM-dd HH:mm")
        val date = format.format(Date())

        val dir = File(AiiUtil.getSDCardPath() + "/letarScreenShot/")
        if (!dir.exists()) {
            dir.mkdir()
        }
        val file = File(dir.absolutePath, date + "ScreenShot" + Random().nextInt(1000) + ".jpg")
        try {
            val out = FileOutputStream(file)
            img!!.compress(Bitmap.CompressFormat.PNG, 90, out)
            out.flush()
            out.close()
            return file
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }

    fun createBitmap(view: View): Bitmap {
        //整个手机屏幕的视图
        view.isDrawingCacheEnabled = true
        view.buildDrawingCache()
        val bitmap = view.drawingCache
        // 获取状态栏高度
        val frame = Rect()
        window.decorView.getWindowVisibleDisplayFrame(frame)
        val statusBarHeight = frame.top
        Log.i("TAG", "" + statusBarHeight)
        // 获取屏幕长和高
        val width = ScreenUtils.getScreenWidth()
        val height = ScreenUtils.getScreenHeight()
        //等比缩放
        val b = Bitmap.createScaledBitmap(bitmap, width, height, true)
        LogUtil.e("ailibin", "bitmap: $b")
        LogUtil.d("ailibin", "bitmapheight: " + b.height + "  y+height:${statusBarHeight + height - statusBarHeight} ")
        view.destroyDrawingCache()
        return b

    }


    /**
     * 选择城市
     */
    @SuppressLint("SetTextI18n")
    private fun selectArea() {
        if (allProvinceList == null || allCityList == null || allCountyList == null) {
            toast("正在初始化省市区数据")
            return
        }
        val pvOptions = OptionsPickerView.Builder(this@EvaluateCourseActivity,
                OptionsPickerView.OnOptionsSelectListener { options1, options2, options3, _ ->
                    //返回的分别是三个级别的选中位置
                    val province = allProvinceList?.get(options1)?.name
                    val city = allCityList?.get(options1)?.get(options2)?.name
                    var county = ""
                    val countyList = allCountyList?.get(options1)?.get(options2)
                    if (countyList != null && countyList.size > 0) {
                        county = countyList[options3].name
                        selectedRegionId = countyList[options3].id
                    } else {
                        selectedRegionId = allCityList?.get(options1)?.get(options2)?.id!!
                    }
                    tv_select_district.text = "$province $city $county".trim()
                })
                .setTitleBgColor(Color.parseColor("#eaeaea"))
                .setSubmitColor(resources.getColor(R.color.colorPrimary))
                .setCancelColor(resources.getColor(R.color.black6))
                .setTitleText("城市")
                .setTitleColor(resources.getColor(R.color.black3))
                .setDividerColor(resources.getColor(R.color.black9))
                .setTextColorCenter(resources.getColor(R.color.black3)) //设置选中项文字颜色
                .setTextColorOut(resources.getColor(R.color.black6))
                .setTextXOffset(resources.getInteger(R.integer.pickerview_xoffset), 0, -resources.getInteger(R.integer.pickerview_xoffset))
                .setContentTextSize(resources.getInteger(R.integer.pickerview_center_text_size))
                .build()
        pvOptions.setPicker(allProvinceList, allCityList, allCountyList)//三级选择器
        pvOptions.show()
    }


    /**
     * 选择生日
     */
    private fun selectBirthday() {
        //控制时间范围(如果不设置范围，则使用默认时间1900-2100年，此段代码可注释)
        //因为系统Calendar的月份是从0-11的,所以如果是调用Calendar的set方法来设置时间,月份的范围也要是从0-11
        val selectedDate = Calendar.getInstance()
        val startDate = Calendar.getInstance()
        startDate.set(1950, 0, 1)
        val endDate = Calendar.getInstance()
        //时间选择器
        val pvTime = TimePickerView.Builder(this, TimePickerView.OnTimeSelectListener { date, _ ->
            //选中事件回调
            // 这里回调过来的v,就是show()方法里面所添加的 View 参数，如果show的时候没有添加参数，v则为null
//            tv_select_birthday.text = DateUtil.date2Str(date, "yyyy年MM月dd日")
        })
                //年月日时分秒 的显示与否，不设置则默认全部显示
                .setType(booleanArrayOf(true, true, true, false, false, false))
                .setTitleBgColor(Color.parseColor("#eaeaea"))
                .setSubmitColor(resources.getColor(R.color.colorPrimary))
                .setCancelColor(resources.getColor(R.color.black6))
                .setLabel("年", "月", "日", "", "", "")
                .isCenterLabel(false)
                .setDividerColor(resources.getColor(R.color.black9))
                .setContentSize(resources.getInteger(R.integer.pickerview_center_text_size))
                .setDate(selectedDate)
                .setRangDate(startDate, endDate)
                .setTextXOffset(-resources.getInteger(R.integer.pickerview_xoffset), 0,
                        resources.getInteger(R.integer.pickerview_xoffset), 0, 0, 0)
                //.setBackgroundId(0x00FFFFFF) //设置外部遮罩颜色
                .setDecorView(null)
                .build()
                .show()
    }


    private fun checkInfo(): Boolean {

        val etString = et_evaluate_for_course.text.toString().trim()
        if (etString.isEmpty()) {
            BaseUtil.showToast("请输入评价的内容")
            return false
        }
        if (etString.length < 10) {
            BaseUtil.showToast("至少输入十个字符")
            return false
        }
        return true
    }

    /**
     * 设置昵称输入字数限制(300个汉字)
     *
     * @param etInputContent
     */
    private fun setTextLimitWatchListener(etInputContent: EditText) {
        val editTextCharLimitUtil = EditTextCharLimitUtil(this, etInputContent, 600, 10, "")
        editTextCharLimitUtil.init()
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        selectPictureHelper.onRestoreInstanceState(savedInstanceState)
    }

    override fun onSaveInstanceState(savedInstanceState: Bundle) {
        super.onSaveInstanceState(savedInstanceState)
        selectPictureHelper.onSaveInstanceState(savedInstanceState)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        selectPictureHelper.onActivityResult(requestCode, resultCode, data)
    }
}