FROM  openjdk:18-oracle

RUN mkdir -p /logs

ENV	PROFILE default
ENV TZ=Asia/Seoul
EXPOSE 8080

ARG JAVA_OPTS

ARG RELEASE_VERSION
ENV DD_VERSION=${RELEASE_VERSION}

ARG JAR_FILE="build/libs/api.jar"
COPY ${JAR_FILE} app.jar

ENTRYPOINT java -jar -javaagent:/pinpoint-agent/pinpoint-bootstrap.jar -Dpinpoint.profiler.profiles.active=$PROFILE -Dpinpoint.applicationName=$APP_NAME app.jar
