package org.openpr.jira;

import org.junit.Test;
import static org.junit.Assert.*;

public class SafePullRequestUrlTest {
    @Test public void acceptsConfiguredHost() {
        assertTrue(SafePullRequestUrl.isAllowed("https://github.example.com/org/repo/pull/12", "github.example.com"));
    }
    @Test public void rejectsHostSpoofing() {
        assertFalse(SafePullRequestUrl.isAllowed("https://github.example.com.attacker.org/org/repo/pull/12", "github.example.com"));
        assertFalse(SafePullRequestUrl.isAllowed("https://github.example.com@attacker.org/org/repo/pull/12", "github.example.com"));
    }
    @Test public void rejectsUnsafeSchemesAndPaths() {
        assertFalse(SafePullRequestUrl.isAllowed("javascript:alert(1)", "github.example.com"));
        assertFalse(SafePullRequestUrl.isAllowed("http://github.example.com/org/repo/pull/12", "github.example.com"));
        assertFalse(SafePullRequestUrl.isAllowed("https://github.example.com/other", "github.example.com"));
    }
}
