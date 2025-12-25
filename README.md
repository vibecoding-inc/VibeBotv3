# VibeBotv3

A Discord bot built with JDA (Java Discord API) v6.2.0 and Java 21.

## Features

- Built with JDA 6.2.0
- Java 21 with Gradle build system
- Structured logging with SLF4J and Logback
- Dockerized for easy deployment
- CI/CD with GitHub Actions
- New /vibe slash command to trigger a GitHub Actions workflow that can make changes to the bot functionality

## Configuration

The bot requires the following environment variable:

- `BOT_TOKEN`: Your Discord bot token

Optional environment variables for the /vibe command (GitHub workflow dispatch):

- `JUNIE_GH_TOKEN` (preferred) or `GH_TOKEN`/`GITHUB_TOKEN`: A GitHub token with permissions to trigger workflow_dispatch on this repo
- `JUNIE_REPOSITORY` (optional): Target repository in the form `owner/repo`. Defaults to `GITHUB_REPOSITORY` if present, otherwise `profiluefter/VibeBotv3`.
- `JUNIE_REF` (optional): Branch or tag to run the workflow on. Defaults to `GITHUB_REF_NAME` if present, otherwise `main`.

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
# Optional but recommended for /vibe
export JUNIE_GH_TOKEN=ghp_your_github_token_here
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
docker run -e BOT_TOKEN=your_discord_bot_token_here \
           -e JUNIE_GH_TOKEN=ghp_your_github_token_here \
           ghcr.io/profiluefter/vibebotv3:latest
```

## Slash Commands

- /vibe prompt: Triggers the junie.yml GitHub Actions workflow via workflow_dispatch, passing your prompt as input. This allows making changes to the bot functionality through an external automation workflow.

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