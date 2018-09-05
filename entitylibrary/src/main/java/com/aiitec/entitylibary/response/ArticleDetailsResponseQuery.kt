package com.aiitec.entitylibary.response

import com.aiitec.entitylibary.model.Article
import com.aiitec.openapi.model.ResponseQuery

class ArticleDetailsResponseQuery : ResponseQuery() {

    var articles : Article? = null
}