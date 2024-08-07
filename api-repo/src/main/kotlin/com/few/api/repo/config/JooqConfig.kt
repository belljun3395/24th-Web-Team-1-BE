package com.few.api.repo.config

import com.few.api.repo.common.ExceptionTranslator
import com.few.api.repo.common.NativeSQLLogger
import com.few.api.repo.common.PerformanceListener
import org.jooq.SQLDialect
import org.jooq.impl.DataSourceConnectionProvider
import org.jooq.impl.DefaultConfiguration
import org.jooq.impl.DefaultDSLContext
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator
import javax.sql.DataSource

@Configuration
class JooqConfig(
    private val dataSource: DataSource,
    private val applicationEventPublisher: ApplicationEventPublisher,
) {
    @Bean
    fun dsl(): DefaultDSLContext {
        return DefaultDSLContext(configuration())
    }

    @Bean
    fun configuration(): DefaultConfiguration {
        val jooqConfiguration = DefaultConfiguration()
        jooqConfiguration.set(connectionProvider())
        val translator =
            SQLErrorCodeSQLExceptionTranslator(SQLDialect.MYSQL.name)
        jooqConfiguration.set(ExceptionTranslator(translator), NativeSQLLogger(), PerformanceListener(applicationEventPublisher))
        jooqConfiguration.set(SQLDialect.MYSQL)
        return jooqConfiguration
    }

    @Bean
    fun connectionProvider(): DataSourceConnectionProvider {
        return DataSourceConnectionProvider(dataSource)
    }
}