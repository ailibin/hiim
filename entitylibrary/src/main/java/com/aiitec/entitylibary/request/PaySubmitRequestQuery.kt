package com.aiitec.entitylibary.request

import com.aiitec.openapi.model.RequestQuery

/**
 * Created by ailibin on 2018/5/11.
 */
//|--chapterId Number 否 课节 id 需要 a=3/a=5
//|--price Number 否 充值、打赏金额 需要 a=4/a=5
//|--payNum Number 否 会员开通的年数 需要 a=1
class PaySubmitRequestQuery : RequestQuery() {

    //1APP 2PC
    var payType: Int? = 1

    //支付方式(1支付宝；2微信 3余额
    var payment: Int? = 0

    //是否使用余额抵扣 1 是 2 否
    var isBalance: Int? = 1

    //会员开通的年数 需要 a=1
    var payNum: Int? = 1

    /**
     * 课程id
     */
    var courseId: Long? = null

    /**
     * 课节 id 需要 a=3/a=5
     */
    var chapterId: Long? = null

    /**
     * 充值、打赏金额 需要 a=4/a=5
     */
    var price: Double? = 0.0


}