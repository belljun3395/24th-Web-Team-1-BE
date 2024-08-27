package com.few.repo.config

import com.few.repo.common.PerformanceListener
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
        jooqConfiguration.set(
            com.few.repo.common.ExceptionTranslator(translator),
            com.few.repo.common.NativeSQLLogger(),
            PerformanceListener(applicationEventPublisher)
        )
        jooqConfiguration.set(SQLDialect.MYSQL)
        return jooqConfiguration
    }

    @Bean
    fun connectionProvider(): DataSourceConnectionProvider {
        return DataSourceConnectionProvider(dataSource)
    }
}