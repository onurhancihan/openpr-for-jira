package org.openpr.jira;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Objects;

/**
 * Server-side, read-only GitHub PR API transport.
 * This is a transport building block, NOT a Jira-facing authenticated service.
 * Never expose the supplied credential to issue viewers.
 */
public final class GitHubApiClient {
    private static final int MAX_BODY_CHARS = 2_000_000;
    private final HttpClient http;

    public GitHubApiClient() {
        this.http = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .followRedirects(HttpClient.Redirect.NEVER)
                .build();
    }

    /**
     * Fetch a single bounded page from GitHub.com or a GitHub Enterprise Server.
     * baseUrl must be an administrator-approved, trusted HTTPS GitHub origin.
     * The calling layer MUST enforce Jira and GitHub authorization.
     */
    public String listPullRequests(String baseUrl, String owner, String repository,
                                   String token, int page, String approvedHost) throws IOException, InterruptedException {
        if (!isTrustedHostname(approvedHost)) {
            throw new IllegalArgumentException("Approved GitHub hostname is required");
        }
        URI uri = endpoint(baseUrl, owner, repository, page);
        String expectedApiHost = approvedHost.equalsIgnoreCase("github.com") ? "api.github.com" : approvedHost;
        if (!uri.getHost().equalsIgnoreCase(expectedApiHost)) {
            throw new IllegalArgumentException("GitHub hostname is not approved");
        }
        if (token == null || token.isBlank() || token.indexOf('\r') >= 0 || token.indexOf('\n') >= 0) {
            throw new IllegalArgumentException("An approved GitHub token is required");
        }
        HttpRequest request = HttpRequest.newBuilder(uri)
                .timeout(Duration.ofSeconds(10))
                .header("Accept", "application/vnd.github+json")
                .header("Authorization", "Bearer " + token)
                .header("X-GitHub-Api-Version", "2022-11-28")
                .GET().build();
        HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            // Never log response bodies: some proxies include sensitive diagnostics.
            throw new IOException("GitHub API returned HTTP " + response.statusCode());
        }
        String body = response.body();
        if (body.length() > MAX_BODY_CHARS) throw new IOException("GitHub response too large");
        return body;
    }

    public static URI endpoint(String baseUrl, String owner, String repository, int page) {
        Objects.requireNonNull(baseUrl, "baseUrl");
        if (page < 1 || page > 10) throw new IllegalArgumentException("Page out of range");
        if (!validSlug(owner) || !validSlug(repository)) throw new IllegalArgumentException("Invalid repository name");
        URI origin = URI.create(baseUrl);
        if (!"https".equalsIgnoreCase(origin.getScheme()) || origin.getHost() == null
                || origin.getUserInfo() != null || origin.getQuery() != null || origin.getFragment() != null
                || origin.getPort() != -1 || (origin.getRawPath() != null && !origin.getRawPath().isEmpty()
                && !origin.getRawPath().equals("/"))) {
            throw new IllegalArgumentException("Expected an HTTPS GitHub origin");
        }
        String host = origin.getHost();
        if (!isTrustedHostname(host)) throw new IllegalArgumentException("Untrusted GitHub hostname");
        String apiHost = host.equalsIgnoreCase("github.com") ? "api.github.com" : host;
        String prefix = host.equalsIgnoreCase("github.com") ? "" : "/api/v3";
        return URI.create("https://" + apiHost + prefix + "/repos/" + owner + "/" + repository
                + "/pulls?state=all&per_page=100&page=" + page);
    }

    /**
     * Defense in depth: disallow loopback, local DNS names, literal IPs and
     * obvious internal targets. Administrators must additionally enforce
     * outbound network allowlisting and DNS rebinding protection.
     */
    static boolean isTrustedHostname(String host) {
        if (host == null || host.isBlank() || host.length() > 253
                || !host.matches("(?i)[a-z0-9](?:[a-z0-9.-]*[a-z0-9])?")) return false;
        String lower = host.toLowerCase(java.util.Locale.ROOT);
        if (lower.equals("localhost") || lower.endsWith(".localhost")
                || lower.endsWith(".local") || lower.endsWith(".internal")
                || lower.endsWith(".test") || lower.endsWith(".invalid")) return false;
        if (lower.matches("[0-9.]+") || lower.contains("..") || !lower.contains(".")) return false;
        return true;
    }

    private static boolean validSlug(String slug) {
        return slug != null && slug.matches("[A-Za-z0-9][A-Za-z0-9_.-]{0,99}")
                && !slug.equals(".") && !slug.equals("..");
    }
}
