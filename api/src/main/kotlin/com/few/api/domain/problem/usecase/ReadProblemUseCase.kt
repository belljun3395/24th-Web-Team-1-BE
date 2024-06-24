package com.few.api.domain.problem.usecase

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.few.api.repo.dao.problem.ProblemDao
import com.few.api.repo.dao.problem.query.SelectProblemQuery
import com.few.api.domain.problem.usecase.`in`.ReadProblemUseCaseIn
import com.few.api.domain.problem.usecase.out.ReadProblemContentsUseCaseOutDetail
import com.few.api.domain.problem.usecase.out.ReadProblemUseCaseOut
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class ReadProblemUseCase(
    private val problemDao: ProblemDao,
    private val objectMapper: ObjectMapper
) {

    @Transactional(readOnly = true)
    fun execute(useCaseIn: ReadProblemUseCaseIn): ReadProblemUseCaseOut {
        val problemId = useCaseIn.problemId

        val record = problemDao.selectProblemContents(SelectProblemQuery(problemId))

        val contents: List<ReadProblemContentsUseCaseOutDetail> = objectMapper.readValue(record.contents)

        return ReadProblemUseCaseOut(
            id = record.id,
            title = record.title,
            contents = contents
        )
    }
}