package com.few.api.repo.dao.problem

import com.few.api.repo.dao.problem.command.InsertProblemsCommand
import com.few.api.repo.dao.problem.query.SelectProblemAnswerQuery
import com.few.api.repo.dao.problem.query.SelectProblemIdByArticleIdsQuery
import com.few.api.repo.dao.problem.query.SelectProblemQuery
import com.few.api.repo.dao.problem.query.SelectProblemsByArticleIdQuery
import com.few.api.repo.dao.problem.record.ProblemIdAndArticleIdRecord
import com.few.api.repo.dao.problem.record.ProblemIdsRecord
import com.few.api.repo.dao.problem.record.SelectProblemAnswerRecord
import com.few.api.repo.dao.problem.record.SelectProblemRecord
import com.few.api.repo.dao.problem.support.ContentsJsonMapper
import jooq.jooq_dsl.tables.Problem
import org.jooq.DSLContext
import org.jooq.JSON
import org.jooq.impl.DSL
import org.springframework.stereotype.Repository

@Repository
class ProblemDao(
    private val dslContext: DSLContext,
    private val contentsJsonMapper: ContentsJsonMapper,
) {
    fun selectProblemContents(query: SelectProblemQuery): SelectProblemRecord? {
        return selectProblemContentsQuery(query)
            .fetchOneInto(SelectProblemRecord::class.java)
    }
    fun selectProblemContentsQuery(query: SelectProblemQuery) =
        dslContext.select(
            Problem.PROBLEM.ID.`as`(SelectProblemRecord::id.name),
            Problem.PROBLEM.TITLE.`as`(SelectProblemRecord::title.name),
            DSL.field("JSON_UNQUOTE({0})", String::class.java, Problem.PROBLEM.CONTENTS)
                .`as`(SelectProblemRecord::contents.name),
            Problem.PROBLEM.ARTICLE_ID.`as`(SelectProblemRecord::articleId.name)
        )
            .from(Problem.PROBLEM)
            .where(Problem.PROBLEM.ID.eq(query.problemId))
            .and(Problem.PROBLEM.DELETED_AT.isNull)

    fun selectProblemAnswer(query: SelectProblemAnswerQuery): SelectProblemAnswerRecord? {
        return selectProblemAnswerQuery(query)
            .fetchOneInto(SelectProblemAnswerRecord::class.java)
    }

    fun selectProblemAnswerQuery(query: SelectProblemAnswerQuery) =
        dslContext.select(
            Problem.PROBLEM.ID.`as`(SelectProblemAnswerRecord::id.name),
            Problem.PROBLEM.ANSWER.`as`(SelectProblemAnswerRecord::answer.name),
            Problem.PROBLEM.EXPLANATION.`as`(SelectProblemAnswerRecord::explanation.name)
        )
            .from(Problem.PROBLEM)
            .where(Problem.PROBLEM.ID.eq(query.problemId))
            .and(Problem.PROBLEM.DELETED_AT.isNull)

    fun selectProblemsByArticleId(query: SelectProblemsByArticleIdQuery): ProblemIdsRecord? {
        return selectProblemsByArticleIdQuery(query)
            .fetch()
            .map { it[Problem.PROBLEM.ID] }
            .let { ProblemIdsRecord(it) }
    }

    fun selectProblemsByArticleIdQuery(query: SelectProblemsByArticleIdQuery) = dslContext.select()
        .from(Problem.PROBLEM)
        .where(Problem.PROBLEM.ARTICLE_ID.eq(query.articleId))
        .and(Problem.PROBLEM.DELETED_AT.isNull)

    fun insertProblems(command: List<InsertProblemsCommand>) {
        dslContext.batch(
            command.map {
                insertProblemCommand(it)
            }
        ).execute()
    }

    fun insertProblemCommand(it: InsertProblemsCommand) =
        dslContext.insertInto(Problem.PROBLEM)
            .set(Problem.PROBLEM.ARTICLE_ID, it.articleId)
            .set(Problem.PROBLEM.CREATOR_ID, it.createrId)
            .set(Problem.PROBLEM.TITLE, it.title)
            .set(Problem.PROBLEM.CONTENTS, JSON.valueOf(contentsJsonMapper.toJson(it.contents)))
            .set(Problem.PROBLEM.ANSWER, it.answer)
            .set(Problem.PROBLEM.EXPLANATION, it.explanation)

    fun selectProblemIdByArticleIds(query: SelectProblemIdByArticleIdsQuery): List<ProblemIdAndArticleIdRecord> {
        return selectProblemIdByArticleIdsQuery(query)
            .fetchInto(ProblemIdAndArticleIdRecord::class.java)
    }

    fun selectProblemIdByArticleIdsQuery(query: SelectProblemIdByArticleIdsQuery) = dslContext
        .select(
            Problem.PROBLEM.ID.`as`(ProblemIdAndArticleIdRecord::problemId.name),
            Problem.PROBLEM.ARTICLE_ID.`as`(ProblemIdAndArticleIdRecord::articleId.name)
        )
        .from(Problem.PROBLEM)
        .where(Problem.PROBLEM.ARTICLE_ID.`in`(query.articleIds))
        .and(Problem.PROBLEM.DELETED_AT.isNull)
}