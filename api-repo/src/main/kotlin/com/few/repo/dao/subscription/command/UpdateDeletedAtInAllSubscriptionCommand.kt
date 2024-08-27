package com.few.repo.dao.subscription.command

data class UpdateDeletedAtInAllSubscriptionCommand(
    val memberId: Long,
    val opinion: String,
)