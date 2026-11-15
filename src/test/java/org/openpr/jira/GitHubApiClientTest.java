package org.openpr.jira;

import org.junit.Test;
import static org.junit.Assert.*;

public class GitHubApiClientTest {
    @Test public void githubComUsesApiHost() {
        assertEquals("https://api.github.com/repos/team/repo/pulls?state=all&per_page=100&page=1",
                GitHubApiClient.endpoint("https://github.com", "team", "repo", 1).toString());
    }
    @Test public void enterpriseUsesLocalApiPath() {
        assertEquals("https://github.example.org/api/v3/repos/team/repo/pulls?state=all&per_page=100&page=2",
                GitHubApiClient.endpoint("https://github.example.org", "team", "repo", 2).toString());
    }
    @Test public void blocksBadOrigins() {
        for (String origin : new String[]{"http://github.example.org", "https://user@github.example.org",
                "https://github.example.org/evil", "https://github.example.org?x=1"}) {
            try { GitHubApiClient.endpoint(origin, "team", "repo", 1); fail(origin); }
            catch (IllegalArgumentException expected) { /* expected */ }
        }
    }
    @Test public void blocksPathInjectionAndUnboundedPagination() {
        try { GitHubApiClient.endpoint("https://github.com", "../secrets", "repo", 1); fail(); }
        catch (IllegalArgumentException expected) { /* expected */ }
        try { GitHubApiClient.endpoint("https://github.com", "team", "repo", 11); fail(); }
        catch (IllegalArgumentException expected) { /* expected */ }
    }
}
