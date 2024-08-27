package com.few.repo.dao.subscription.record

data class CountAllSubscriptionStatusRecord(
    val totalSubscriptions: Long,
    val activeSubscriptions: Long,
)