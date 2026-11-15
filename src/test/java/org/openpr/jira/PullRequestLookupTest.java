package org.openpr.jira;

import org.junit.Test;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import static org.junit.Assert.*;

public class PullRequestLookupTest {
    private static final List<PullRequestLookup.Repository> REPOS =
            List.of(new PullRequestLookup.Repository("team", "service"));
    private static final String JSON = "[{\"number\":7,\"title\":\"Fix DEMO-42\",\"html_url\":\"https://github.com/team/service/pull/7\",\"state\":\"open\",\"head\":{\"ref\":\"DEMO-42-fix\"},\"user\":{\"login\":\"dev\"}}]";

    @Test public void deniesBeforeNetworkRequest() throws Exception {
        AtomicInteger calls = new AtomicInteger();
        PullRequestLookup lookup = new PullRequestLookup((a,b,c,d,e,f) -> {
            calls.incrementAndGet(); return JSON;
        });
        assertTrue(lookup.find("https://github.com", "github.com", "secret", REPOS,
                "DEMO-42", false, true).isEmpty());
        assertTrue(lookup.find("https://github.com", "github.com", "secret", REPOS,
                "DEMO-42", true, false).isEmpty());
        assertEquals(0, calls.get());
    }

    @Test public void findsMatchingSafePr() throws Exception {
        PullRequestLookup lookup = new PullRequestLookup((a,b,c,d,e,f) -> JSON);
        List<GitHubPr> prs = lookup.find("https://github.com", "github.com",
                "secret", REPOS, "DEMO-42", true, true);
        assertEquals(1, prs.size());
        assertEquals(7, prs.get(0).number());
    }

    @Test public void filtersUnsafeLinks() throws Exception {
        PullRequestLookup lookup = new PullRequestLookup((a,b,c,d,e,f) ->
                JSON.replace("https://github.com/", "https://evil.example/"));
        assertTrue(lookup.find("https://github.com", "github.com",
                "secret", REPOS, "DEMO-42", true, true).isEmpty());
    }

    @Test(expected = IllegalArgumentException.class)
    public void limitsRepositories() throws Exception {
        PullRequestLookup lookup = new PullRequestLookup((a,b,c,d,e,f) -> "[]");
        lookup.find("https://github.com", "github.com", "secret",
                java.util.Collections.nCopies(11, REPOS.get(0)), "DEMO-42", true, true);
    }
}
