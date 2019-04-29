package com.aiitec.hiim.base

import com.amap.api.location.AMapLocation

/**
 * @author  Anthony
 * @version 1.0
 * createTime 2017/11/7.
 */
object Constants {

    val IS_FIRST_LAUNCH = "isFirstLaunch"
    val ARG_TYPE = "type"
    val ARG_ID = "id"
    val ARG_NAME = "name"
    val COURSE_ID = "course_id"
    val ORG_TITLE = "org_title"
    val ORG_ACTION = "org_action"
    val IM_PREFIX = "IM"
    //全局位置对象
    var location: AMapLocation? = null
    //区域id
    var regionId: Long = -1
    //置顶的conversation的identify
    var ORG_STICK_IDENTIFY = "org_stick_identify"
    //置顶的conversation的类型
    var ORG_STICK_TYPE = "ORG_STICK_TYPE"

    /**
     * 发送广播通知
     */
    val ORG_ONSCROLL_EVENT = "org_onscroll_event"
}