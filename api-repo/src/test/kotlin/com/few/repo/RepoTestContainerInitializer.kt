package com.few.repo

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.testcontainers.containers.DockerComposeContainer
import java.io.File

class RepoTestContainerInitializer : ApplicationContextInitializer<ConfigurableApplicationContext> {
    private val log = KotlinLogging.logger {}

    companion object {
        private const val MYSQL = "mysql"
        private const val MYSQL_PORT = 3306

        private val dockerCompose =
            DockerComposeContainer(File("src/test/resources/docker-compose.yml"))
                .withExposedService(
                    com.few.repo.RepoTestContainerInitializer.Companion.MYSQL,
                    com.few.repo.RepoTestContainerInitializer.Companion.MYSQL_PORT
                )
    }

    override fun initialize(applicationContext: ConfigurableApplicationContext) {
        log.debug { "===== set up test containers =====" }

        com.few.repo.RepoTestContainerInitializer.Companion.dockerCompose.start()

        log.debug { "===== success set up test containers =====" }
    }
}