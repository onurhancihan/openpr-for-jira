package org.openpr.jira;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/** Strict, immutable validation of administrator-supplied test configuration. */
public record OpenPrConfiguration(boolean enabled, String origin, String host,
                                  List<PullRequestLookup.Repository> repositories, String token) {
    public static OpenPrConfiguration from(Map<String, String> environment) {
        boolean enabled = "true".equalsIgnoreCase(environment.get("OPENPR_ENABLED"));
        if (!enabled) return new OpenPrConfiguration(false, "", "", List.of(), "");
        String origin = required(environment, "OPENPR_GITHUB_ORIGIN");
        String host = required(environment, "OPENPR_GITHUB_HOST");
        String token = required(environment, "OPENPR_GITHUB_TOKEN");
        String names = required(environment, "OPENPR_REPOSITORIES");
        if (!GitHubApiClient.isTrustedHostname(host)) throw new IllegalArgumentException("Invalid GitHub hostname");
        URI uri = GitHubApiClient.endpoint(origin, "validation", "validation", 1);
        String expected = host.equalsIgnoreCase("github.com") ? "api.github.com" : host;
        if (!expected.equalsIgnoreCase(uri.getHost())) throw new IllegalArgumentException("GitHub origin/host mismatch");
        if (token.indexOf('\r') >= 0 || token.indexOf('\n') >= 0) throw new IllegalArgumentException("Invalid token");
        List<PullRequestLookup.Repository> repos = new ArrayList<>();
        for (String name : names.split(",", -1)) {
            String[] parts = name.trim().split("/", -1);
            if (parts.length != 2 || !valid(parts[0]) || !valid(parts[1]))
                throw new IllegalArgumentException("Invalid repository configuration");
            PullRequestLookup.Repository repo = new PullRequestLookup.Repository(parts[0], parts[1]);
            if (repos.contains(repo)) throw new IllegalArgumentException("Duplicate repository");
            repos.add(repo);
        }
        if (repos.isEmpty() || repos.size() > 10) throw new IllegalArgumentException("Repository limit exceeded");
        return new OpenPrConfiguration(true, origin, host, List.copyOf(repos), token);
    }

    private static String required(Map<String, String> values, String key) {
        String value = values.get(key);
        if (value == null || value.isBlank()) throw new IllegalArgumentException("Missing " + key);
        return value;
    }

    private static boolean valid(String slug) {
        return slug.matches("[A-Za-z0-9][A-Za-z0-9_.-]{0,99}")
                && !slug.equals(".") && !slug.equals("..");
    }
}
