FROM maven:3.8.7-eclipse-temurin-17 AS build

WORKDIR /build

COPY pom.xml .
RUN mvn dependency:go-offline

COPY src ./src
RUN mvn package -DskipTests

FROM alfresco/alfresco-base-java:jre17-rockylinux8-202302221525

ARG JAR_FILE=/build/target/alfresco-ai-sync-*.jar

WORKDIR /opt/app

RUN groupadd -r alfrescoaisync && useradd -r -g alfrescoaisync alfrescoaisync

COPY --from=build ${JAR_FILE} app.jar
RUN chown alfrescoaisync:alfrescoaisync app.jar && chmod 400 app.jar

USER alfrescoaisync

ENTRYPOINT ["java", "-jar", "app.jar"]
