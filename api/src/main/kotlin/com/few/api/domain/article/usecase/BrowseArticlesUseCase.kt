package com.few.api.domain.article.usecase

import com.few.api.domain.article.usecase.dto.*
import com.few.api.exception.common.NotFoundException
import com.few.api.repo.dao.article.ArticleDao
import com.few.api.repo.dao.article.ArticleMainCardDao
import com.few.api.repo.dao.article.ArticleViewCountDao
import com.few.api.repo.dao.article.query.SelectArticlesOrderByViewsQuery
import com.few.api.repo.dao.article.query.SelectRankByViewsQuery
import com.few.api.repo.dao.article.record.ArticleMainCardRecord
import com.few.api.repo.dao.article.record.SelectArticleContentsRecord
import com.few.api.repo.dao.article.record.SelectArticleViewsRecord
import com.few.data.common.code.CategoryType
import kotlinx.coroutines.*
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Component
class BrowseArticlesUseCase(
    private val articleViewCountDao: ArticleViewCountDao,
    private val articleMainCardDao: ArticleMainCardDao,
    private val articleDao: ArticleDao,
) {
    @Transactional(readOnly = true)
    suspend fun execute(useCaseIn: ReadArticlesUseCaseIn): ReadArticlesUseCaseOut {
        /**
         * 아티클 조회수 테이블에서 마지막 읽은 아티클 아이디, 카테고리를 기반으로 Offset(테이블 row 순위)을 구함
         */
        val offset =
            if (useCaseIn.prevArticleId <= 0) {
                0L
            } else {
                articleViewCountDao.selectRankByViewsAsync(
                    SelectRankByViewsQuery(useCaseIn.prevArticleId)
                ) ?: 0L
            }

        /**
         * 구한 Offset을 기준으로 이번 스크롤에서 보여줄 아티클 11개를 뽑아옴
         * 카테고리 별, 조회수 순 11개. 조회수가 같을 경우 최신 아티클이 우선순위를 가짐
         */
        val articleViewsRecords: MutableList<SelectArticleViewsRecord> =
            articleViewCountDao
                .selectArticlesOrderByViewsAsync(
                    SelectArticlesOrderByViewsQuery(
                        offset,
                        CategoryType.fromCode(useCaseIn.categoryCd) ?: CategoryType.All
                    )
                ).toMutableList()

        /**
         * 11개를 조회한 상황에서 11개가 조회되지 않았다면 마지막 스크롤로 판단
         */
        val isLast =
            if (articleViewsRecords.size == 11) {
                articleViewsRecords.removeAt(10)
                false
            } else {
                true
            }

        val recordViewIds = articleViewsRecords.map { it.articleId }.toSet()
        val articleMainCardRecords = articleMainCardDao.selectArticleMainCardsRecordAsync(recordViewIds)
        val coroutineScope = CoroutineScope(Dispatchers.IO)
        val deferredResults = mutableListOf<Deferred<SelectArticleContentsRecord>>()
        recordViewIds.map {
            coroutineScope.async { articleDao.selectArticleContentsAsync(it) }.let {
                deferredResults.add(it)
            }
        }

//        val recordViewIds = articleViewsRecords.map { it.articleId }.toSet()

//        val deferredResults =
//            recordViewIds.map { id ->
//                val articleMainCardRecord = articleMainCardDao.selectArticleMainCardsRecordAsync(id)
//                val selectArticleContentsRecord = articleDao.selectArticleContentsAsync(id)
//                articleMainCardRecord?.apply {
//                    this.content = selectArticleContentsRecord.content
//                }!!
//            }
//        val articleMainCardRecords = deferredResults.toMutableSet()
//        val coroutineScope = CoroutineScope(Dispatchers.IO)
//        val recordViewIds = articleViewsRecords.map { it.articleId }.toSet()
//        val deferredResults = mutableListOf<Deferred<ArticleMainCardRecord>>()
//        recordViewIds.map { id ->
//            val routine =
//                coroutineScope.async {
//                    val articleMainCardRecord = articleMainCardDao.selectArticleMainCardsRecordAsync(id)
//                    val selectArticleContentsRecord = articleDao.selectArticleContentsAsync(id)
//                    articleMainCardRecord?.apply {
//                        this.content = selectArticleContentsRecord.content
//                    }!!
//                }
//            deferredResults.add(routine)
//        }
//        val articleMainCardRecords = deferredResults.awaitAll().toMutableSet()

        /**
         * 아티클 조회수 순, 조회수가 같을 경우 최신 아티클이 우선순위를 가지도록 정렬 (TODO: 삭제시 양향도 파악 필요)
         */
        val sortedArticles = updateAndSortArticleViews(articleMainCardRecords, articleViewsRecords)
        val selectArticleContentsRecords = deferredResults.awaitAll().associateBy { it.articleId }
        sortedArticles.forEach { it.content = selectArticleContentsRecords[it.articleId]?.content ?: "" }

        val articleUseCaseOuts: List<ReadArticleUseCaseOut> =
            sortedArticles
                .map { a ->
                    ReadArticleUseCaseOut(
                        id = a.articleId,
                        writer =
                        WriterDetail(
                            id = a.writerId,
                            name = a.writerName,
                            imageUrl = a.writerImgUrl,
                            url = a.writerUrl
                        ),
                        mainImageUrl = a.mainImageUrl,
                        title = a.articleTitle,
                        content = a.content,
                        problemIds = emptyList(),
                        category =
                        CategoryType.fromCode(a.categoryCd)?.displayName
                            ?: throw NotFoundException("article.invalid.category"),
                        createdAt = a.createdAt,
                        views = a.views,
                        workbooks =
                        a.workbooks
                            .map { WorkbookDetail(it.id!!, it.title!!) }
                    )
                }.toList()

        return ReadArticlesUseCaseOut(articleUseCaseOuts, isLast)
    }

    private fun updateAndSortArticleViews(
        articleRecords: Set<ArticleMainCardRecord>,
        articleViewsRecords: List<SelectArticleViewsRecord>,
    ): List<ArticleMainCardRecord> {
        val viewsMap = articleViewsRecords.associateBy({ it.articleId }, { it.views ?: 0 })
        return articleRecords
            .map { article ->
                article.apply { views = viewsMap[articleId] ?: 0 }
            }.sortedWith(
                compareByDescending<ArticleMainCardRecord> { it.views ?: 0 }
                    .thenByDescending { it.articleId }
            )
    }
}