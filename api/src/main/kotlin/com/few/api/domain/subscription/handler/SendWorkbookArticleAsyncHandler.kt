package com.few.api.domain.subscription.handler

import com.few.api.config.DatabaseAccessThreadPoolConfig.Companion.DATABASE_ACCESS_POOL
import com.few.api.domain.subscription.service.SubscriptionArticleService
import com.few.api.domain.subscription.service.SubscriptionMemberService
import com.few.api.domain.subscription.service.SubscriptionEmailService
import com.few.api.domain.subscription.service.SubscriptionWorkbookService
import com.few.api.domain.subscription.service.dto.*
import com.few.api.exception.common.NotFoundException
import com.few.api.repo.dao.subscription.SubscriptionDao
import com.few.api.repo.dao.subscription.command.UpdateArticleProgressCommand
import com.few.api.repo.dao.subscription.command.UpdateLastArticleProgressCommand
import com.few.data.common.code.CategoryType
import com.few.email.service.article.dto.Content
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import java.time.LocalDate

@Component
class SendWorkbookArticleAsyncHandler(
    private val memberService: SubscriptionMemberService,
    private val articleService: SubscriptionArticleService,
    private val workbookService: SubscriptionWorkbookService,
    private val subscriptionDao: SubscriptionDao,
    private val emailService: SubscriptionEmailService,
) {
    private val log = KotlinLogging.logger {}

    @Async(value = DATABASE_ACCESS_POOL)
    fun sendWorkbookArticle(memberId: Long, workbookId: Long, articleDayCol: Byte) {
        val date = LocalDate.now()
        val memberEmail = memberService.readMemberEmail(ReadMemberEmailInDto(memberId))?.email
            ?: throw NotFoundException("member.notfound.id")
        val article = articleService.readArticleIdByWorkbookIdAndDay(
            ReadArticleIdByWorkbookIdAndDayDto(
                workbookId,
                articleDayCol.toInt()
            )
        )?.let { articleId ->
            articleService.readArticleContent(ReadArticleContentInDto(articleId))
        } ?: throw NotFoundException("article.notfound.id")

        val sendArticleInDto = SendArticleInDto(
            memberId = memberId,
            workbookId = workbookId,
            toEmail = memberEmail,
            articleDayCol = articleDayCol,
            articleTitle = article.articleTitle,
            articleContent = Content.create(
                memberEmail = memberEmail,
                workbookId = workbookId,
                articleId = article.id,
                currentDate = date,
                category = CategoryType.convertToDisplayName(article.category.toByte()),
                articleDay = articleDayCol.toInt(),
                articleTitle = article.articleTitle,
                writerName = article.writerName,
                writerLink = article.writerLink,
                articleContent = article.articleContent
            )
        )

        runCatching { emailService.sendArticleEmail(sendArticleInDto) }
            .onSuccess {
                val lastDayArticleId =
                    workbookService.readWorkbookLastArticleId(
                        ReadWorkbookLastArticleIdInDto(
                            workbookId
                        )
                    )?.lastArticleId ?: throw NotFoundException("workbook.notfound.id")

                if (article.id == lastDayArticleId) {
                    subscriptionDao.updateArticleProgress(
                        UpdateArticleProgressCommand(
                            workbookId,
                            memberId
                        )
                    )
                } else {
                    subscriptionDao.updateLastArticleProgress(
                        UpdateLastArticleProgressCommand(
                            workbookId,
                            memberId
                        )
                    )
                }
            }
            .onFailure {
                log.error(it) { "Failed to send article email" }
            }
    }
}