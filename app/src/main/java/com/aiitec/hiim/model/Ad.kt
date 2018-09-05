package com.aiitec.hiim.model

import com.aiitec.openapi.json.annotation.JSONField
import com.aiitec.openapi.model.Entity

/**
 * @author  Anthony
 * @version 1.0
 * createTime 2017/11/18.
 */
class Ad : Entity(){
    @JSONField(name = "details_image")
    var imagePath: String? = null
    @JSONField(name = "plate_type")
    var plateType = 0
    @JSONField(name = "ads_type")
    var adsType = 0
    var actionContent: String? = null
    var link: String? = null
    @JSONField(name = "course_id")
    var courseId = 0
}