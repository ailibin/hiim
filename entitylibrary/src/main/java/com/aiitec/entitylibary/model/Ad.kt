package com.aiitec.entitylibary.model

import com.aiitec.openapi.model.Entity

/**
 * @author  Anthony
 * @version 1.0
 * createTime 2017/11/18.
 */

class Ad : Entity() {

    var imagePath: String? = null
    /**
     *  广告链接
     */
    var link: String? = null
    var name: String? = null


    /**
     * 后台返回的正规的属性
     */
    var id: Long = -1
    /**
     * 轮播图跳转 url
     */
    var url: String? = null
    /**
     * 轮播图片
     */
    var image_url: String? = null

    /**
     *文章详情内容
     */
    var content: String? = null

    /**
     *跳转的类型 type=1跳转url type=2文章详情
     */
    var type: Int = -1

}