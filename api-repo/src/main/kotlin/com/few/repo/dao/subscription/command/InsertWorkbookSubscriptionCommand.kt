package com.few.repo.dao.subscription.command

data class InsertWorkbookSubscriptionCommand(
    val workbookId: Long,
    val memberId: Long,
)