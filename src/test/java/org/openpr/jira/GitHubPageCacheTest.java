package org.openpr.jira;

import org.junit.Test;
import java.util.concurrent.atomic.AtomicInteger;
import static org.junit.Assert.*;

public class GitHubPageCacheTest {
    @Test public void reusesPagesAndSeparatesCredentials() throws Exception {
        AtomicInteger requests = new AtomicInteger();
        GitHubPageCache cache = new GitHubPageCache((origin, owner, repo, token, page, host) -> {
            requests.incrementAndGet();
            return "[]";
        });
        assertEquals("[]", cache.fetch("https://github.com", "team", "repo", "token-one", 1, "github.com"));
        assertEquals("[]", cache.fetch("https://github.com", "team", "repo", "token-one", 1, "github.com"));
        assertEquals(1, requests.get());
        cache.fetch("https://github.com", "team", "repo", "token-two", 1, "github.com");
        assertEquals(2, requests.get());
    }
    @Test public void separatesRepositoriesAndPages() throws Exception {
        AtomicInteger requests = new AtomicInteger();
        GitHubPageCache cache = new GitHubPageCache((a,b,c,d,e,f) -> {
            requests.incrementAndGet(); return "[]";
        });
        cache.fetch("https://github.com", "team", "first", "token", 1, "github.com");
        cache.fetch("https://github.com", "team", "second", "token", 1, "github.com");
        cache.fetch("https://github.com", "team", "first", "token", 2, "github.com");
        assertEquals(3, requests.get());
    }
    @Test public void doesNotCacheErrors() throws Exception {
        AtomicInteger requests = new AtomicInteger();
        GitHubPageCache cache = new GitHubPageCache((a,b,c,d,e,f) -> {
            if (requests.incrementAndGet() == 1) throw new java.io.IOException("offline");
            return "[]";
        });
        try {
            cache.fetch("https://github.com", "team", "repo", "token", 1, "github.com");
            fail("Expected network error");
        } catch (java.io.IOException expected) {
            assertEquals("offline", expected.getMessage());
        }
        assertEquals("[]", cache.fetch("https://github.com", "team", "repo", "token", 1, "github.com"));
        assertEquals(2, requests.get());
    }
}
