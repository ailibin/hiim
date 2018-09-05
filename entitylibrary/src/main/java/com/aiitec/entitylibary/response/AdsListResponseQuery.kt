package com.aiitec.entitylibary.response

import com.aiitec.entitylibary.model.Ad
import com.aiitec.openapi.model.ResponseQuery

/**
 *@Author Xiaobing
 * @Version 1.0
 * Created on 2018/3/13
 *@effect 广告数据的请求响应实体类
 */
class AdsListResponseQuery : ResponseQuery() {
    /**
     *广告图数据集合
     */
    var Ads: ArrayList<Ad>? = null
}