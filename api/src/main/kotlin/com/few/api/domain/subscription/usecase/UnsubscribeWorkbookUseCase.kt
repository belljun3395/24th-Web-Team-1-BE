package com.few.api.domain.subscription.usecase

import com.few.api.domain.subscription.service.MemberService
import com.few.api.domain.subscription.service.dto.ReadMemberIdInDto
import com.few.api.repo.dao.subscription.SubscriptionDao
import com.few.api.repo.dao.subscription.command.UpdateDeletedAtInWorkbookSubscriptionCommand
import com.few.api.domain.subscription.usecase.dto.UnsubscribeWorkbookUseCaseIn
import com.few.api.exception.common.NotFoundException
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class UnsubscribeWorkbookUseCase(
    private val subscriptionDao: SubscriptionDao,
    private val memberService: MemberService,
) {

    @Transactional
    fun execute(useCaseIn: UnsubscribeWorkbookUseCaseIn) {
        // TODO: request sending email

        val memberId =
            memberService.readMemberId(ReadMemberIdInDto(useCaseIn.email))?.memberId ?: throw NotFoundException("member.notfound.email")

        subscriptionDao.updateDeletedAtInWorkbookSubscription(
            UpdateDeletedAtInWorkbookSubscriptionCommand(
                memberId = memberId,
                workbookId = useCaseIn.workbookId,
                opinion = useCaseIn.opinion
            )
        )
    }
}