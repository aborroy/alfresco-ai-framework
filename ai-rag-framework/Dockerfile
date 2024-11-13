FROM maven:3.8.7-eclipse-temurin-17 AS build

WORKDIR /build

COPY pom.xml .
RUN mvn dependency:go-offline

COPY src ./src
RUN mvn package -DskipTests

# Use the official Alfresco base image with JRE 17 on Rocky Linux 8
FROM alfresco/alfresco-base-java:jre17-rockylinux8-202306121108

ARG JAR_FILE=/build/target/ai-rag-framework-*.jar

ARG UID=10001

RUN yum install -y passwd && \
    # Create a non-login user with no home directory
    adduser \
        --comment "App User" \
        --home "/nonexistent" \
        --shell "/sbin/nologin" \
        --no-create-home \
        --uid "${UID}" \
        appuser && \
    # Lock the user account for security
    passwd --lock appuser && \
    # Clean up unnecessary files to reduce image size
    yum clean all && \
    rm -rf /var/cache/yum

USER appuser

WORKDIR /opt

COPY --from=build ${JAR_FILE} app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]


