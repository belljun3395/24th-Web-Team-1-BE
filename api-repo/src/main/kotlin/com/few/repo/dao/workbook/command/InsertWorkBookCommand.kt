package com.few.repo.dao.workbook.command

import java.net.URL

data class InsertWorkBookCommand(
    val title: String,
    val mainImageUrl: URL,
    val category: String,
    val description: String,
)