package org.openpr.jira;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Bounded, read-only lookup across administrator-approved repositories.
 * The Jira adapter must check issue access and administrator consent BEFORE calling.
 * Credentials must remain server-side.
 */
public final class PullRequestLookup {
    private static final int MAX_REPOSITORIES = 10;
    private static final int MAX_PAGES_PER_REPOSITORY = 3;

    @FunctionalInterface
    public interface PageFetcher {
        String fetch(String origin, String owner, String repository, String token,
                     int page, String approvedHost) throws IOException, InterruptedException;
    }

    public record Repository(String owner, String name) {}

    private final PageFetcher fetcher;

    public PullRequestLookup(PageFetcher fetcher) {
        this.fetcher = java.util.Objects.requireNonNull(fetcher);
    }

    public List<GitHubPr> find(String origin, String approvedHost, String token,
                               List<Repository> repositories, String issueKey,
                               boolean issueBrowseAllowed, boolean sharingEnabled)
            throws IOException, InterruptedException {
        if (!VisibilityPolicy.canShowMetadata(issueBrowseAllowed, sharingEnabled,
                token != null && !token.isBlank() && repositories != null && !repositories.isEmpty())) {
            return List.of();
        }
        if (repositories.size() > MAX_REPOSITORIES) {
            throw new IllegalArgumentException("Too many repositories");
        }
        Map<String, GitHubPr> unique = new LinkedHashMap<>();
        for (Repository repo : repositories) {
            for (int page = 1; page <= MAX_PAGES_PER_REPOSITORY; page++) {
                String json = fetcher.fetch(origin, repo.owner(), repo.name(), token, page, approvedHost);
                for (GitHubPr pr : GitHubPrParser.matching(json, issueKey)) {
                    if (SafePullRequestUrl.isAllowed(pr.url(), approvedHost)) {
                        unique.putIfAbsent(repo.owner() + "/" + repo.name() + "#" + pr.number(), pr);
                    }
                }
                // A short page is the last page under GitHub's per_page=100 contract.
                if (com.fasterxml.jackson.databind.json.JsonMapper.builder().build().readTree(json).size() < 100) {
                    break;
                }
            }
        }
        return List.copyOf(unique.values());
    }
}
