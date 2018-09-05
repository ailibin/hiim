package com.aiitec.entitylibary.response

import com.aiitec.entitylibary.model.Wxpay
import com.aiitec.openapi.model.ResponseQuery

/**
 * Created by ailibin on 2018/5/11.
 */
class PaySubmitResponseQuery : ResponseQuery() {

    //	唯一订单号（支付用）
    //var orderSn: String? = null

    //订单金额(这里不能加Double等数字类型,不然会解析错误)
    //var amount = 0.0

//    var amount: Double = 0.0

    //    //微信支付时需要。JSON 格式字符串。
    //var wxPay: String? = null

    //支付宝支付请求参数。
    //var alipay: String? = null

    //微信支付时需要实体对象
    var wxPay: Wxpay? = null

    //微信支付时需要。JSON 格式字符串。
//    var wxPay: String? = null

    //支付宝
    var aliPay: String? = null
}