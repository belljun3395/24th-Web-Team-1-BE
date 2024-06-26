package com.few.api.repo.dao.problem

import com.few.api.repo.dao.problem.command.InsertSubmitHistoryCommand
import jooq.jooq_dsl.Tables.SUBMIT_HISTORY
import org.jooq.DSLContext
import org.springframework.stereotype.Component

@Component
class SubmitHistoryDao(
    private val dslContext: DSLContext
) {

    fun insertSubmitHistory(command: InsertSubmitHistoryCommand): Long {
        val result = dslContext.insertInto(SUBMIT_HISTORY)
            .set(SUBMIT_HISTORY.PROBLEM_ID, command.problemId)
            .set(SUBMIT_HISTORY.MEMBER_ID, command.memberId)
            .set(SUBMIT_HISTORY.SUBMIT_ANS, command.submitAns)
            .set(SUBMIT_HISTORY.IS_SOLVED, command.isSolved)
            .returning(SUBMIT_HISTORY.ID)
            .fetchOne()

        return result?.getValue(SUBMIT_HISTORY.ID)
            ?: throw RuntimeException("Submit History with ID ${command.problemId} insertion fail") // TODO: 에러 표준화
    }
}