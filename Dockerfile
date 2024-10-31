# Use the official Alfresco base image with JRE 17 on Rocky Linux 8
FROM alfresco/alfresco-base-java:jre17-rockylinux8-202306121108

# Set build-time variables for user ID
ARG UID=10001

# Install necessary packages, create a non-login user, and lock the user account
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

# Switch to the non-root user for improved security
USER appuser

# Set the working directory for the application
WORKDIR /opt

# Copy the built JAR file into the container
COPY target/ai-framework-*.jar app.jar

# Set the default command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
