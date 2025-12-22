# Use Eclipse Temurin (official OpenJDK build) instead of the deprecated openjdk image
FROM eclipse-temurin:21-jdk

# Add a volume pointing to /tmp (optional, but good for Tomcat)
VOLUME /tmp

# Argument for the jar file
ARG JAR_FILE=target/*.jar

# Copy the jar
COPY ${JAR_FILE} app.jar

# Run the jar
ENTRYPOINT ["java","-jar","/app.jar"]
