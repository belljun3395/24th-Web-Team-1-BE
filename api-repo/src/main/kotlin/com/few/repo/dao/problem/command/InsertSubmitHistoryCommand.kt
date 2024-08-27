package com.few.repo.dao.problem.command

data class InsertSubmitHistoryCommand(
    val problemId: Long,
    val memberId: Long,
    val submitAns: String,
    val isSolved: Boolean,
)