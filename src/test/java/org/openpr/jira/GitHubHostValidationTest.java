package org.openpr.jira;

import org.junit.Test;
import static org.junit.Assert.*;

public class GitHubHostValidationTest {
    @Test public void acceptsPublicAndEnterpriseDnsHosts() {
        assertTrue(GitHubApiClient.isTrustedHostname("github.com"));
        assertTrue(GitHubApiClient.isTrustedHostname("github.enterprise.example.com"));
    }
    @Test public void rejectsLocalAndIpHosts() {
        for (String host : new String[] {
                "localhost", "localhost.local", "127.0.0.1",
                "10.0.0.2", "169.254.169.254", "internal", "jira.internal",
                "host.local", "a..b.com", "host.test"
        }) {
            assertFalse(host, GitHubApiClient.isTrustedHostname(host));
        }
    }
    @Test(expected = IllegalArgumentException.class)
    public void rejectsLocalOriginBeforeRequest() {
        GitHubApiClient.endpoint("https://localhost", "org", "repo", 1);
    }
}
