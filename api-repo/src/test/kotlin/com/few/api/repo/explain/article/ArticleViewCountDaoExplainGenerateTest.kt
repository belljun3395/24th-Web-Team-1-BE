package com.few.api.repo.explain.article

import com.few.api.repo.dao.article.ArticleViewCountDao
import com.few.api.repo.dao.article.command.ArticleViewCountCommand
import com.few.api.repo.dao.article.query.ArticleViewCountQuery
import com.few.api.repo.dao.article.query.SelectArticlesOrderByViewsQuery
import com.few.api.repo.dao.article.query.SelectRankByViewsQuery
import com.few.api.repo.explain.InsertUpdateExplainGenerator
import com.few.api.repo.explain.ResultGenerator
import com.few.api.repo.jooq.JooqTestSpec
import com.few.data.common.code.CategoryType
import io.github.oshai.kotlinlogging.KotlinLogging
import jooq.jooq_dsl.tables.ArticleViewCount
import org.jooq.DSLContext
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

@Tag("explain")
class ArticleViewCountDaoExplainGenerateTest : JooqTestSpec() {

    private val log = KotlinLogging.logger {}

    @Autowired
    private lateinit var dslContext: DSLContext

    @Autowired
    private lateinit var articleViewCountDao: ArticleViewCountDao

    @BeforeEach
    fun setUp() {
        log.debug { "===== start setUp =====" }
        dslContext.deleteFrom(ArticleViewCount.ARTICLE_VIEW_COUNT).execute()
        dslContext.insertInto(ArticleViewCount.ARTICLE_VIEW_COUNT)
            .set(ArticleViewCount.ARTICLE_VIEW_COUNT.ARTICLE_ID, 1L)
            .set(ArticleViewCount.ARTICLE_VIEW_COUNT.VIEW_COUNT, 1)
            .set(ArticleViewCount.ARTICLE_VIEW_COUNT.CATEGORY_CD, CategoryType.fromCode(0)!!.code)
            .execute()
        log.debug { "===== finish setUp =====" }
    }

    @Test
    fun selectArticleViewCountQueryExplain() {
        val query = ArticleViewCountCommand(1L).let {
            articleViewCountDao.selectArticleViewCountQuery(it)
        }

        val explain = dslContext.explain(query).toString()
        ResultGenerator.execute(query, explain, "selectArticleViewCountQueryExplain")
    }

    @Test
    fun upsertArticleViewCountQueryExplain() {
        val command = ArticleViewCountQuery(
            articleId = 1L,
            categoryType = CategoryType.fromCode(0)!!
        ).let {
            articleViewCountDao.upsertArticleViewCountQuery(it)
        }

        val explain = InsertUpdateExplainGenerator.execute(dslContext, command.sql, command.bindValues)

        ResultGenerator.execute(command, explain, "upsertArticleViewCountQueryExplain")
    }

    @Test
    fun insertArticleViewCountToZeroQueryExplain() {
        val command = ArticleViewCountQuery(
            articleId = 1L,
            categoryType = CategoryType.fromCode(0)!!
        ).let {
            articleViewCountDao.insertArticleViewCountToZeroQuery(it)
        }

        val explain = InsertUpdateExplainGenerator.execute(dslContext, command.sql, command.bindValues)

        ResultGenerator.execute(command, explain, "insertArticleViewCountToZeroQueryExplain")
    }

    @Test
    fun selectRankByViewsQueryExplain() {
        val query = SelectRankByViewsQuery(1L).let {
            articleViewCountDao.selectRankByViewsQuery(it)
        }

        val explain = dslContext.explain(query).toString()
        ResultGenerator.execute(query, explain, "selectRankByViewsQueryExplain")
    }

    @Test
    fun selectArticlesOrderByViewsQueryExplain() {
        val query = SelectArticlesOrderByViewsQuery(
            offset = 0,
            category = CategoryType.fromCode(0)!!
        ).let {
            articleViewCountDao.selectArticlesOrderByViewsQuery(it)
        }

        val explain = dslContext.explain(query).toString()
        ResultGenerator.execute(query, explain, "selectArticlesOrderByViewsQueryExplain")
    }
}