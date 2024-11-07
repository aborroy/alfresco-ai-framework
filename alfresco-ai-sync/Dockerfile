FROM alfresco/alfresco-base-java:jre17-rockylinux8-202302221525

ENV PORT 8081
EXPOSE 8081
ARG JAR_FILE=target/alfresco-ai-sync-*.jar

WORKDIR /opt/app

RUN groupadd -r alfrescoaisync && useradd -r -g alfrescoaisync alfrescoaisync

COPY ${JAR_FILE} app.jar
RUN chown alfrescoaisync:alfrescoaisync app.jar && chmod 400 app.jar

USER alfrescoaisync

ENTRYPOINT ["java", "-jar", "app.jar"]
