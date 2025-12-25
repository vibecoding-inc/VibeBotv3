package com.profiluefter.vibebotv3;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Objects;

/**
 * Minimal GitHub Actions workflow_dispatch helper.
 */
public class GithubDispatcher {
    private static final Logger logger = LoggerFactory.getLogger(GithubDispatcher.class);

    public interface RequestSender {
        HttpResponse<String> send(HttpRequest request) throws Exception;
    }

    private final String repository; // owner/repo
    private final String ref; // branch or tag name
    private final String token; // GitHub token
    private final RequestSender sender;

    public record DispatchResult(boolean success, int statusCode, String body) {}

    public GithubDispatcher(String repository, String ref, String token, RequestSender sender) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.ref = Objects.requireNonNull(ref, "ref");
        this.token = Objects.requireNonNull(token, "token");
        this.sender = Objects.requireNonNull(sender, "sender");
    }

    public static GithubDispatcher fromEnv() {
        String token = envOrNull(
                // Preferred custom token var
                "JUNIE_GH_TOKEN",
                // Fallbacks commonly available
                "GH_TOKEN", "GITHUB_TOKEN"
        );
        if (token == null || token.isBlank()) {
            throw new IllegalStateException("Missing GitHub token. Set JUNIE_GH_TOKEN (or GH_TOKEN/GITHUB_TOKEN).");
        }

        String repository = firstNonBlank(
                System.getenv("JUNIE_REPOSITORY"),
                System.getenv("GITHUB_REPOSITORY"),
                "profiluefter/VibeBotv3"
        );

        String ref = firstNonBlank(
                System.getenv("JUNIE_REF"),
                System.getenv("GITHUB_REF_NAME"),
                "main"
        );

        HttpClient httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        RequestSender sender = request -> httpClient.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        return new GithubDispatcher(repository, ref, token, sender);
    }

    public DispatchResult dispatchJunieWorkflow(String prompt) throws Exception {
        HttpRequest request = buildDispatchRequest(repository, ref, token, prompt);

        logger.info("Dispatching junie.yml workflow to {}@{}", repository, ref);
        HttpResponse<String> response = sender.send(request);
        int status = response.statusCode();
        String body = response.body();
        boolean ok = status == 204 || status == 201 || status == 200;
        if (!ok) {
            logger.warn("GitHub dispatch failed: status={} body={}", status, body);
        }
        return new DispatchResult(ok, status, body);
    }

    // package-private for tests
    static HttpRequest buildDispatchRequest(String repository, String ref, String token, String prompt) {
        String url = "https://api.github.com/repos/" + repository + "/actions/workflows/junie.yml/dispatches";
        String payload = "{\n" +
                "  \"ref\": \"" + escapeJson(ref) + "\",\n" +
                "  \"inputs\": { \"prompt\": \"" + escapeJson(prompt) + "\" }\n" +
                "}";

        return HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(20))
                .header("Accept", "application/vnd.github+json")
                .header("X-GitHub-Api-Version", "2022-11-28")
                .header("Authorization", "Bearer " + token)
                .POST(HttpRequest.BodyPublishers.ofString(payload, StandardCharsets.UTF_8))
                .build();
    }

    private static String escapeJson(String s) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '"' -> sb.append("\\\"");
                case '\\' -> sb.append("\\\\");
                case '\n' -> sb.append("\\n");
                case '\r' -> sb.append("\\r");
                case '\t' -> sb.append("\\t");
                default -> {
                    if (c < 0x20) {
                        sb.append(String.format("\\u%04x", (int) c));
                    } else {
                        sb.append(c);
                    }
                }
            }
        }
        return sb.toString();
    }

    private static String envOrNull(String... names) {
        for (String n : names) {
            String v = System.getenv(n);
            if (v != null && !v.isBlank()) return v;
        }
        return null;
    }

    private static String firstNonBlank(String... values) {
        for (String v : values) {
            if (v != null && !v.isBlank()) return v;
        }
        return null;
    }
}
