package com.few.repo.dao.workbook.command

data class MapWorkBookToArticleCommand(
    val workbookId: Long,
    val articleId: Long,
    val dayCol: Int,
)