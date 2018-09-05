package com.aiitec.entitylibary.response

import com.aiitec.entitylibary.model.Article
import com.aiitec.openapi.model.ListResponseQuery

/**
 * 文章列表
 */
class ArticleListResponseQuery : ListResponseQuery() {

    var articles: List<Article>? = null

}