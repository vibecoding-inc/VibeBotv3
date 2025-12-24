# VibeBotv3

A Discord bot built with JDA (Java Discord API) v6.2.0 and Java 21.

## Features

- Built with JDA 6.2.0
- Java 21 with Gradle build system
- Structured logging with SLF4J and Logback
- Dockerized for easy deployment
- CI/CD with GitHub Actions

## Configuration

The bot requires the following environment variable:

- `BOT_TOKEN`: Your Discord bot token

## Building

### Prerequisites

- Java 21
- Gradle 8.11.1 (or use the included Gradle wrapper)

### Build the project

```bash
./gradlew build
```

### Run locally

```bash
export BOT_TOKEN=your_discord_bot_token_here
java -jar build/libs/vibebotv3-1.0.0.jar
```

## Docker

### Build the Docker image

```bash
docker build -t vibebotv3 .
```

### Run the Docker container

```bash
docker run -e BOT_TOKEN=your_discord_bot_token_here vibebotv3
```

### Pull from GitHub Container Registry

```bash
docker pull ghcr.io/profiluefter/vibebotv3:latest
docker run -e BOT_TOKEN=your_discord_bot_token_here ghcr.io/profiluefter/vibebotv3:latest
```

## GitHub Actions CI/CD

The project includes GitHub Actions workflows that:

- Build and push a `latest` Docker image on every push to the `main` branch
- Build and push tagged Docker images on release creation (e.g., `v1.0.0`)

Images are published to: `ghcr.io/profiluefter/vibebotv3`

## Project Structure

```
vibebotv3/
├── src/main/java/com/profiluefter/vibebotv3/
│   └── VibeBotv3.java              # Main bot class
├── src/main/resources/
│   └── logback.xml                 # Logging configuration
├── build.gradle.kts                # Gradle build configuration
├── settings.gradle.kts             # Gradle settings
├── Dockerfile                      # Docker configuration
└── .github/workflows/
    └── docker-publish.yml          # CI/CD workflow
```

## License

This project is open source.