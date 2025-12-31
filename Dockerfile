# Use Eclipse Temurin (official OpenJDK build) instead of the deprecated openjdk image
# ---- Builder stage: build the jar using Maven ----
FROM maven:3.9.4-eclipse-temurin-21 AS build
WORKDIR /workspace

# Copy pom and wrapper files first for better layer caching
COPY pom.xml mvnw ./
COPY .mvn .mvn

# Copy the source code
COPY src ./src

# Quick visible build marker so remote build logs show this Dockerfile was used
RUN echo "RENDER_BUILD_MARKER: multi-stage-dockerfile-2025-12-26"

# Build the project (skip tests to speed up builds; change if you want tests run in CI)
RUN mvn -DskipTests package -B


# ---- Runtime stage: smaller image with only the jar and Java runtime ----
FROM eclipse-temurin:21-jdk

# Add a volume pointing to /tmp (optional)
VOLUME /tmp

# Copy the built jar from the builder stage into the runtime image
COPY --from=build /workspace/target/*.jar /app.jar

# Run the jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
