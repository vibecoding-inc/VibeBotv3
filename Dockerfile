# Build stage
FROM gradle:8.11.1-jdk21 AS build

WORKDIR /app

# Copy Gradle configuration files first for better caching
COPY build.gradle.kts settings.gradle.kts ./

# Download dependencies (this layer will be cached)
RUN gradle dependencies --no-daemon

# Copy source code
COPY src ./src

# Build the application
RUN gradle jar --no-daemon

# Runtime stage
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copy the jar from build stage
COPY --from=build /app/build/libs/*.jar app.jar

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
