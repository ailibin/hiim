package com.aiitec.entitylibary.response

import com.aiitec.openapi.model.ResponseQuery

class SharePosterResponseQuery : ResponseQuery(){

    /**
     * 背景图
     */
    var sharePoster : String? = null

    /**
     * 二维码图片
     */
    var userCode : String? = null

    /**
     * 路径
     */
    var shareUrl : String? = null
}