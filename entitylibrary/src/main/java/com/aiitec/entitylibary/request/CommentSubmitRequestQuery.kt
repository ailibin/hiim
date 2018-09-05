package com.aiitec.entitylibary.request

import com.aiitec.openapi.model.RequestQuery

/**
 * Created by ailibin on 2018/7/1.
 */
class CommentSubmitRequestQuery : RequestQuery() {

    /**
     * 1 评论  2 提问(action=2)
     */
    var type: Int = -1

    var courseId: Long? = null

    var chapterId: Long? = null

    /**
     * 评论内容
     */
    var content: String? = null

    /**
     * 评论星级数量
     */
    var star: Int = -1

    /**
     * 评论星级数,这个是老师已完结的课程星星评价
     */
    var starrating: Int = -1


}