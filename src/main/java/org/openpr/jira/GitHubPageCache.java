package org.openpr.jira;

import java.io.IOException;
import java.time.Clock;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Short-lived in-memory cache for GitHub PR pages. No tokens are retained in
 * cache keys or values. Callers must use a stable, secret-free connection ID
 * that changes whenever the connection credentials/configuration change.
 * Cache is per JVM; Jira Data Center nodes do not share entries.
 */
public final class GitHubPageCache implements PullRequestLookup.PageFetcher {
    private static final long TTL_MILLIS = 60_000;
    private static final int MAX_ENTRIES = 100;
    private final PullRequestLookup.PageFetcher delegate;
    private final Clock clock;
    private final Map<String, Entry> entries = new LinkedHashMap<>(16, 0.75f, true);
    private final Map<String, Object> inFlight = new java.util.concurrent.ConcurrentHashMap<>();
    private record Entry(String json, long expiresAt) {}

    public GitHubPageCache(PullRequestLookup.PageFetcher delegate) {
        this(delegate, Clock.systemUTC());
    }

    GitHubPageCache(PullRequestLookup.PageFetcher delegate, Clock clock) {
        this.delegate = Objects.requireNonNull(delegate);
        this.clock = Objects.requireNonNull(clock);
    }

    @Override public String fetch(String origin, String owner, String repository, String token,
                                  int page, String approvedHost) throws IOException, InterruptedException {
        // Credential changes invalidate the cache via a one-way SHA-256 digest.
        // The digest is never logged or exposed to users.
        String key = origin + "|" + approvedHost + "|" + owner + "|" + repository + "|" + page
                + "|" + tokenDigest(token);
        Object lock = inFlight.computeIfAbsent(key, ignored -> new Object());
        synchronized (lock) {
            synchronized (entries) {
                Entry cached = entries.get(key);
                if (cached != null && cached.expiresAt > clock.millis()) return cached.json;
                entries.remove(key);
            }
            String json = delegate.fetch(origin, owner, repository, token, page, approvedHost);
            synchronized (entries) {
                entries.put(key, new Entry(json, clock.millis() + TTL_MILLIS));
                while (entries.size() > MAX_ENTRIES) entries.remove(entries.keySet().iterator().next());
            }
            return json;
        }
    }

    private static String tokenDigest(String token) {
        try {
            byte[] bytes = java.security.MessageDigest.getInstance("SHA-256")
                    .digest(token.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            return java.util.HexFormat.of().formatHex(bytes);
        } catch (java.security.NoSuchAlgorithmException ex) {
            throw new IllegalStateException("SHA-256 unavailable", ex);
        }
    }
}
