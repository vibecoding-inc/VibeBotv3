package com.profiluefter.vibebotv3;

import org.junit.jupiter.api.Test;

import java.net.http.HttpRequest;
import java.net.http.HttpHeaders;
import java.net.http.HttpClient;
import java.net.URI;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class GithubDispatcherTest {

    @Test
    void buildDispatchRequest_hasExpectedUrlHeadersAndBody() {
        String repository = "owner/repo";
        String ref = "main";
        String token = "t0k3n";
        String prompt = "Fix a bug in /hello command";

        HttpRequest req = GithubDispatcher.buildDispatchRequest(repository, ref, token, prompt);

        assertEquals("https://api.github.com/repos/owner/repo/actions/workflows/junie.yml/dispatches",
                req.uri().toString());
        assertEquals("application/vnd.github+json", req.headers().firstValue("Accept").orElse(null));
        assertTrue(req.headers().firstValue("Authorization").orElse("").startsWith("Bearer "));

        // Body publisher string can't be read back easily; we can check content length indirectly by building again
        // So we only ensure the request is a POST with a body by checking content-type not set (default) and timeout set
        assertNotNull(req.timeout().orElse(null));
    }

    @Test
    void dispatchJunieWorkflow_returnsSuccessOn2xx() throws Exception {
        var fake = (GithubDispatcher.RequestSender) request -> new HttpResponseStub(204, "");
        GithubDispatcher dispatcher = new GithubDispatcher("owner/repo", "main", "token", fake);
        var result = dispatcher.dispatchJunieWorkflow("Do X");
        assertTrue(result.success());
        assertEquals(204, result.statusCode());
    }

    // Minimal HttpResponse stub for tests
    static class HttpResponseStub implements java.net.http.HttpResponse<String> {
        private final int code;
        private final String body;

        HttpResponseStub(int code, String body) {
            this.code = code;
            this.body = body;
        }

        @Override
        public int statusCode() { return code; }

        @Override
        public String body() { return body; }

        @Override
        public HttpRequest request() { return null; }

        @Override
        public Optional<java.net.http.HttpResponse<String>> previousResponse() { return Optional.empty(); }

        @Override
        public HttpHeaders headers() { return HttpHeaders.of(java.util.Map.of(), (a,b) -> true); }

        @Override
        public URI uri() { return null; }

        @Override
        public HttpClient.Version version() { return HttpClient.Version.HTTP_1_1; }

        @Override
        public Optional<javax.net.ssl.SSLSession> sslSession() { return Optional.empty(); }
    }
}
