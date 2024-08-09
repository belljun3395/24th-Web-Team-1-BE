package com.few.api.web.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.few.api.ApiMain
import com.few.api.domain.admin.document.usecase.*
import com.few.api.domain.article.usecase.ReadArticleUseCase
import com.few.api.domain.article.usecase.ReadArticlesUseCase
import com.few.api.domain.log.AddApiLogUseCase
import com.few.api.domain.member.usecase.SaveMemberUseCase
import com.few.api.domain.member.usecase.TokenUseCase
import com.few.api.domain.problem.usecase.BrowseProblemsUseCase
import com.few.api.domain.problem.usecase.CheckProblemUseCase
import com.few.api.domain.problem.usecase.ReadProblemUseCase
import com.few.api.domain.subscription.usecase.BrowseSubscribeWorkbooksUseCase
import com.few.api.domain.subscription.usecase.SubscribeWorkbookUseCase
import com.few.api.domain.subscription.usecase.UnsubscribeAllUseCase
import com.few.api.domain.subscription.usecase.UnsubscribeWorkbookUseCase
import com.few.api.domain.workbook.article.usecase.ReadWorkBookArticleUseCase
import com.few.api.domain.workbook.usecase.BrowseWorkbooksUseCase
import com.few.api.domain.workbook.usecase.ReadWorkbookUseCase
import com.few.api.security.token.TokenResolver
import com.few.api.web.handler.ApiControllerExceptionHandler
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.restdocs.RestDocumentationExtension
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.servlet.MockMvc

@ActiveProfiles(value = ["test", "new"])
@AutoConfigureRestDocs
@AutoConfigureMockMvc(addFilters = false)
@SpringBootTest(classes = [ApiMain::class])
@ExtendWith(RestDocumentationExtension::class)
@ContextConfiguration(initializers = [ControllerTestContainerInitializer::class])
abstract class ControllerTestSpec {

    /** Common */
    @Autowired
    lateinit var apiControllerExceptionHandler: ApiControllerExceptionHandler

    @Autowired
    lateinit var objectMapper: ObjectMapper

    lateinit var webTestClient: WebTestClient

    @Autowired
    lateinit var mockMvc: MockMvc

    @MockBean
    lateinit var tokenResolver: TokenResolver

    /** AdminControllerTest */
    @MockBean
    lateinit var addArticleUseCase: AddArticleUseCase

    @MockBean
    lateinit var addWorkbookUseCase: AddWorkbookUseCase

    @MockBean
    lateinit var mapArticleUseCase: MapArticleUseCase

    @MockBean
    lateinit var convertContentUseCase: ConvertContentUseCase

    @MockBean
    lateinit var putImageUseCase: PutImageUseCase

    /** ApiLogControllerTest */
    @MockBean
    lateinit var addApiLogUseCase: AddApiLogUseCase

    /** ArticleControllerTest */
    @MockBean
    lateinit var readArticleUseCase: ReadArticleUseCase

    @MockBean
    lateinit var readArticlesUseCase: ReadArticlesUseCase

    /** MemberControllerTest */
    @MockBean
    lateinit var saveMemberUseCase: SaveMemberUseCase

    @MockBean
    lateinit var tokenUseCase: TokenUseCase

    /** ProblemControllerTest */
    @MockBean
    lateinit var browseProblemsUseCase: BrowseProblemsUseCase

    @MockBean
    lateinit var readProblemUseCase: ReadProblemUseCase

    @MockBean
    lateinit var checkProblemUseCase: CheckProblemUseCase

    /** SubscriptionControllerTest */
    @MockBean
    lateinit var subscribeWorkbookUseCase: SubscribeWorkbookUseCase

    @MockBean
    lateinit var unsubscribeWorkbookUseCase: UnsubscribeWorkbookUseCase

    @MockBean
    lateinit var unsubscribeAllUseCase: UnsubscribeAllUseCase

    @MockBean
    lateinit var browseSubscribeWorkbooksUseCase: BrowseSubscribeWorkbooksUseCase

    /** WorkBookArticleControllerTest */
    @MockBean
    lateinit var readWorkBookArticleUseCase: ReadWorkBookArticleUseCase

    /** WorkBookControllerTest */
    @MockBean
    lateinit var readWorkbookUseCase: ReadWorkbookUseCase

    @MockBean
    lateinit var browseWorkBooksUseCase: BrowseWorkbooksUseCase
}