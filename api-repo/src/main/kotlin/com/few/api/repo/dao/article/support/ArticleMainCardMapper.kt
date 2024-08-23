package com.few.api.repo.dao.article.support

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.few.api.repo.dao.article.command.WorkbookCommand
import com.few.api.repo.dao.article.record.ArticleMainCardRecord
import com.few.api.repo.dao.article.record.WorkbookRecord
import com.few.api.repo.dao.member.support.WriterDescriptionJsonMapper
import org.jooq.JSON
import org.jooq.RecordMapper
import org.jooq.Record
import org.springframework.stereotype.Component
import java.net.URL
import java.time.LocalDateTime

@Component
class ArticleMainCardMapper(
    private val objectMapper: ObjectMapper,
    private val writerDescriptionJsonMapper: WriterDescriptionJsonMapper,
) : RecordMapper<Record, ArticleMainCardRecord> {
    override fun map(record: Record) = ArticleMainCardRecord(
        articleId = record.get(ArticleMainCardRecord::articleId.name, Long::class.java),
        articleTitle = record.get(ArticleMainCardRecord::articleTitle.name, String::class.java),
        mainImageUrl = record.get(ArticleMainCardRecord::mainImageUrl.name, URL::class.java),
        categoryCd = record.get(ArticleMainCardRecord::categoryCd.name, Byte::class.java),
        createdAt = record.get(ArticleMainCardRecord::createdAt.name, LocalDateTime::class.java),
        writerId = record.get(ArticleMainCardRecord::writerId.name, Long::class.java),
        writerEmail = record.get(ArticleMainCardRecord::writerEmail.name, String::class.java),
        writerDescription = writerDescriptionJsonMapper.toObject(
            record.get(
                ArticleMainCardRecord::writerDescription.name,
                String::class.java
            )
        ),
        workbooks = record.get(ArticleMainCardRecord::workbooks.name, JSON::class.java)?.data()
            ?.let {
                if ("{}".equals(it)) {
                    emptyList()
                } else {
                    val workbookRecords = objectMapper.readValue<List<WorkbookRecord>>(it)
                    workbookRecords.filter { w -> w.id != null && w.title != null }
                        .toList()
                }
            } ?: run {
            emptyList()
        }
    )

    fun toJsonStr(workbooks: List<WorkbookCommand>) = objectMapper.writeValueAsString(workbooks)
}