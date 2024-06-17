package com.few.api.web.controller.article

import com.few.api.web.controller.article.response.ReadArticleResponse
import com.few.api.web.controller.article.response.WriterInfo
import com.few.api.web.support.ApiResponse
import com.few.api.web.support.ApiResponseGenerator
import org.springframework.http.HttpStatus
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.net.URL
import java.time.LocalDateTime

@Validated
@RestController
@RequestMapping("/api/v1/articles")
class ArticleController {

    @GetMapping("/{articleId}")
    fun readArticle(
        @PathVariable(value = "articleId") articleId: Long
    ): ApiResponse<ApiResponse.SuccessBody<ReadArticleResponse>> {
        val data = ReadArticleResponse(
            id = 1L,
            writer = WriterInfo(
                id = 1L,
                name = "안나포",
                url = URL("http://localhost:8080/api/v1/writers/1")
            ),
            title = "ETF(상장 지수 펀드)란? 모르면 손해라고?",
            content = "content",
            problemIds = listOf(1L, 2L, 3L),
            category = "경제",
            createdAt = LocalDateTime.now()
        )
        return ApiResponseGenerator.success(data, HttpStatus.OK)
    }
}