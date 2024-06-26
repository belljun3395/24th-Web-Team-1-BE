import org.hidetake.gradle.swagger.generator.GenerateSwaggerUI

dependencies {
    /** module */
    implementation(project(":api-repo"))
    implementation(project(":batch"))

    /** spring starter */
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-actuator")

    /** swagger & restdocs */
    implementation("org.springdoc:springdoc-openapi-starter-webflux-ui:${DependencyVersion.SPRINGDOC}")
    implementation("org.springframework.restdocs:spring-restdocs-webtestclient")
    implementation("com.epages:restdocs-api-spec-mockmvc:${DependencyVersion.EPAGES_REST_DOCS_API_SPEC}")

    /** test container */
    implementation(platform("org.testcontainers:testcontainers-bom:${DependencyVersion.TEST_CONTAINER}"))
    testImplementation("org.testcontainers:mysql")
}

plugins {
    id("org.asciidoctor.jvm.convert") version DependencyVersion.ASCIIDOCTOR
    id("com.epages.restdocs-api-spec") version DependencyVersion.EPAGES_REST_DOCS_API_SPEC
    id("org.hidetake.swagger.generator") version DependencyVersion.SWAGGER_GENERATOR
}

val serverUrl = project.hasProperty("serverUrl").let {
    if (it) {
        project.property("serverUrl") as String
    } else {
        "http://localhost:8080"
    }
}

/** convert snippet to swagger */
openapi3 {
    this.setServer(serverUrl)
    title = project.name
    version = project.version.toString()
    format = "yaml"
    snippetsDirectory = "build/generated-snippets/"
    outputDirectory = "src/main/resources/static/"
    outputFileNamePrefix = "openapi3"
}

/** convert snippet to postman */
postman {
    title = project.name
    version = project.version.toString()
    baseUrl = serverUrl
    outputDirectory = "src/main/resources/static/"
    outputFileNamePrefix = "postman"
}

swaggerSources {
    register("api") {
        setInputFile(file("$projectDir/src/main/resources/static/openapi3.yaml"))
    }
}

tasks.withType(GenerateSwaggerUI::class) {
    dependsOn("openapi3")
}

tasks.register("generateApiSwaggerUI", Copy::class) {
    dependsOn("generateSwaggerUI")
    val generateSwaggerUISampleTask = tasks.named("generateSwaggerUIApi", GenerateSwaggerUI::class).get()
    from(generateSwaggerUISampleTask.outputDir)
    into("$projectDir/src/main/resources/static/docs/swagger-ui")
}

val imageName = project.hasProperty("imageName").let {
    if (it) {
        project.property("imageName") as String
    } else {
        "api"
    }
}
val releaseVersion = project.hasProperty("releaseVersion").let {
    if (it) {
        project.property("releaseVersion") as String
    } else {
        "latest"
    }
}

tasks.register("buildDockerImage") {
    dependsOn("bootJar")

    doLast {
        exec {
            workingDir(".")
            commandLine("docker", "run", "--privileged", "--rm", "tonistiigi/binfmt", "--install", "all")
        }

        exec {
            workingDir(".")
            commandLine("docker", "buildx", "create", "--use")
        }

        exec {
            workingDir(".")
            commandLine("docker", "buildx", "build", "--platform=linux/amd64,linux/arm64", "-t", "fewletter/$imageName", "--build-arg", "RELEASE_VERSION=$releaseVersion", ".", "--push")
        }
    }
}