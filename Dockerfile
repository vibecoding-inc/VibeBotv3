# Build stage
FROM eclipse-temurin:21-jdk AS build

WORKDIR /app

# Copy all project files
COPY . .

# Make gradlew executable
RUN chmod +x gradlew

# Build the application with gradle wrapper
RUN ./gradlew jar --no-daemon --info

# Runtime stage
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copy the jar from build stage
COPY --from=build /app/build/libs/*.jar app.jar

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
