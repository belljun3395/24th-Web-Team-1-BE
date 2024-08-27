package com.few.repo.common

data class SlowQueryEvent(
    val slowQuery: String,
)