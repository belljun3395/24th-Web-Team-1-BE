package com.few.api.web.controller.article

import com.few.api.domain.article.usecase.dto.ReadArticleUseCaseIn
import com.few.api.domain.article.usecase.ReadArticleUseCase
import com.few.api.domain.article.usecase.ReadArticlesUseCase
import com.few.api.domain.article.usecase.dto.ReadArticlesUseCaseIn
import com.few.api.web.controller.article.response.*
import com.few.api.web.support.ApiResponse
import com.few.api.web.support.ApiResponseGenerator
import com.few.data.common.code.CategoryType
import jakarta.validation.constraints.Min
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@Validated
@RestController
@RequestMapping(value = ["/api/v1/articles"], produces = [MediaType.APPLICATION_JSON_VALUE])
class ArticleController(
    private val readArticleUseCase: ReadArticleUseCase,
    private val readArticlesUseCase: ReadArticlesUseCase,
) {

    @GetMapping("/{articleId}")
    fun readArticle(
        @PathVariable(value = "articleId")
        @Min(value = 1, message = "{min.id}")
        articleId: Long,
    ): ApiResponse<ApiResponse.SuccessBody<ReadArticleResponse>> {
        val useCaseOut = ReadArticleUseCaseIn(
            articleId = articleId,
            memberId = 0L
        ).let { useCaseIn: ReadArticleUseCaseIn -> //TODO: membberId검토
            readArticleUseCase.execute(useCaseIn)
        }

        val response = ReadArticleResponse(
            id = useCaseOut.id,
            title = useCaseOut.title,
            writer = WriterInfo(
                useCaseOut.writer.id,
                useCaseOut.writer.name,
                useCaseOut.writer.url
            ),
            mainImageUrl = useCaseOut.mainImageUrl,
            content = useCaseOut.content,
            problemIds = useCaseOut.problemIds,
            category = useCaseOut.category,
            createdAt = useCaseOut.createdAt,
            views = useCaseOut.views
        )

        return ApiResponseGenerator.success(response, HttpStatus.OK)
    }

    @GetMapping
    fun readArticles(
        @RequestParam(
            required = false,
            defaultValue = "0"
        ) prevArticleId: Long,
        @RequestParam(
            required = false,
            defaultValue = "-1"
        ) categoryCd: Byte,
    ): ApiResponse<ApiResponse.SuccessBody<ReadArticlesResponse>> {
        val useCaseOut = readArticlesUseCase.execute(ReadArticlesUseCaseIn(prevArticleId, categoryCd))

        val articles: List<ReadArticleResponse> = useCaseOut.articles.map { a ->
            ReadArticleResponse(
                id = a.id,
                title = a.title,
                writer = WriterInfo(
                    a.writer.id,
                    a.writer.name,
                    a.writer.url
                ),
                mainImageUrl = a.mainImageUrl,
                content = a.content,
                problemIds = a.problemIds,
                category = a.category,
                createdAt = a.createdAt,
                views = a.views,
                workbooks = a.workbooks.map { WorkbookInfo(it.id, it.title) }
            )
        }.toList()

        val response = ReadArticlesResponse(articles, useCaseOut.isLast)

        return ApiResponseGenerator.success(response, HttpStatus.OK)
    }

    @GetMapping("/categories")
    fun browseArticleCategories(): ApiResponse<ApiResponse.SuccessBody<Map<String, Any>>> {
        return ApiResponseGenerator.success(
            mapOf(
                "categories" to CategoryType.entries.map {
                    mapOf(
                        "code" to it.code,
                        "name" to it.displayName
                    )
                }
            ),
            HttpStatus.OK
        )
    }
}