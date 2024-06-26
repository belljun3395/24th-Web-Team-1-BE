package com.few.api.web.controller.article

import com.epages.restdocs.apispec.ResourceDocumentation
import com.epages.restdocs.apispec.ResourceDocumentation.parameterWithName
import com.epages.restdocs.apispec.ResourceSnippetParameters
import com.epages.restdocs.apispec.Schema
import com.fasterxml.jackson.databind.ObjectMapper
import com.few.api.domain.article.dto.ReadArticleUseCaseIn
import com.few.api.domain.article.dto.ReadArticleUseCaseOut
import com.few.api.domain.article.dto.WriterDetail
import com.few.api.domain.article.usecase.ReadArticleUseCase
import com.few.api.web.controller.ControllerTestSpec
import com.few.api.web.controller.description.Description
import com.few.api.web.controller.helper.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.http.codec.json.Jackson2JsonDecoder
import org.springframework.http.codec.json.Jackson2JsonEncoder
import org.springframework.restdocs.RestDocumentationContextProvider
import org.springframework.restdocs.payload.PayloadDocumentation
import org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.util.UriComponentsBuilder
import java.net.URL
import java.time.LocalDateTime

class ArticleControllerTest : ControllerTestSpec() {

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    private lateinit var webTestClient: WebTestClient

    @Autowired
    private lateinit var articleController: ArticleController

    @MockBean
    private lateinit var readArticleUseCase: ReadArticleUseCase

    companion object {
        private val BASE_URL = "/api/v1/articles"
        private val TAG = "ArticleController"
    }

    @BeforeEach
    fun setUp(restDocumentation: RestDocumentationContextProvider) {
        webTestClient = WebTestClient.bindToController(articleController)
            .controllerAdvice(super.apiControllerExceptionHandler)
            .httpMessageCodecs {
                it.defaultCodecs().jackson2JsonEncoder(Jackson2JsonEncoder(objectMapper))
                it.defaultCodecs().jackson2JsonDecoder(Jackson2JsonDecoder(objectMapper))
            }
            .configureClient()
            .filter(WebTestClientRestDocumentation.documentationConfiguration(restDocumentation))
            .build()
    }

    @Test
    @DisplayName("[GET] /api/v1/articles/{articleId}")
    fun readArticle() {
        // given
        val api = "ReadArticle"
        val uri = UriComponentsBuilder.newInstance().path("$BASE_URL/{articleId}").build().toUriString()
        // set usecase mock
        val articleId = 1L
        `when`(readArticleUseCase.execute(ReadArticleUseCaseIn(articleId))).thenReturn(
            ReadArticleUseCaseOut(
                id = 1L,
                writer = WriterDetail(
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
        )

        // when
        this.webTestClient.get().uri(uri, articleId).accept(MediaType.APPLICATION_JSON)
            .exchange().expectStatus().isOk().expectBody().consumeWith(
                WebTestClientRestDocumentation.document(
                    api.toIdentifier(),
                    ResourceDocumentation.resource(
                        ResourceSnippetParameters.builder().description("아티클 Id로 아티클 조회")
                            .summary(api.toIdentifier()).privateResource(false).deprecated(false)
                            .tag(TAG).requestSchema(Schema.schema(api.toRequestSchema()))
                            .pathParameters(parameterWithName("articleId").description("학습지 Id"))
                            .responseSchema(Schema.schema(api.toResponseSchema())).responseFields(
                                *Description.describe(
                                    arrayOf(
                                        PayloadDocumentation.fieldWithPath("data")
                                            .fieldWithObject("data"),
                                        PayloadDocumentation.fieldWithPath("data.id")
                                            .fieldWithNumber("아티클 Id"),
                                        PayloadDocumentation.fieldWithPath("data.writer")
                                            .fieldWithObject("아티클 작가"),
                                        PayloadDocumentation.fieldWithPath("data.writer.id")
                                            .fieldWithNumber("아티클 작가 Id"),
                                        PayloadDocumentation.fieldWithPath("data.writer.name")
                                            .fieldWithString("아티클 작가 이름"),
                                        PayloadDocumentation.fieldWithPath("data.writer.url")
                                            .fieldWithString("아티클 작가 링크"),
                                        PayloadDocumentation.fieldWithPath("data.title")
                                            .fieldWithString("아티클 제목"),
                                        PayloadDocumentation.fieldWithPath("data.content")
                                            .fieldWithString("아티클 내용"),
                                        PayloadDocumentation.fieldWithPath("data.problemIds")
                                            .fieldWithArray("아티클 문제 목록"),
                                        PayloadDocumentation.fieldWithPath("data.category")
                                            .fieldWithString("아티클 카테고리"),
                                        PayloadDocumentation.fieldWithPath("data.createdAt")
                                            .fieldWithString("아티클 생성일")
                                    )
                                )
                            ).build()
                    )
                )
            )
    }
}