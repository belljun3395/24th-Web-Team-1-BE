package com.few.repo.dao.article.command

data class ArticleViewHisCommand(
    val articleId: Long,
    val memberId: Long,
)