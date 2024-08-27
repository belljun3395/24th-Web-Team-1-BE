package com.few.repo.dao.subscription.command

data class UpdateDeletedAtInWorkbookSubscriptionCommand(
    val workbookId: Long,
    val memberId: Long,
    val opinion: String,
)