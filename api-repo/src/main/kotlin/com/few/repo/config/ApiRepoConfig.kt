package com.few.repo.config

import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration

@Configuration
@ComponentScan(basePackages = [ApiRepoConfig.BASE_PACKAGE])
class ApiRepoConfig {
    companion object {
        const val BASE_PACKAGE = "com.few.repo"
        const val SERVICE_NAME = "few"
        const val MODULE_NAME = "few-repo"
        const val BEAN_NAME_PREFIX = "fewRepo"
        const val PROPERTY_PREFIX = SERVICE_NAME + "." + MODULE_NAME
    }
}