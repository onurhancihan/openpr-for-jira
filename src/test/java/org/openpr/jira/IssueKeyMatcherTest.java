package org.openpr.jira;

import org.junit.Test;
import static org.junit.Assert.*;

public class IssueKeyMatcherTest {
    @Test public void matchesTitle() { assertTrue(IssueKeyMatcher.matches("WPLAT-123", "Fix WPLAT-123 bug", null)); }
    @Test public void matchesBranch() { assertTrue(IssueKeyMatcher.matches("WPLAT-123", null, "feature/wplat-123-fix")); }
    @Test public void rejectsPartialKeys() { assertFalse(IssueKeyMatcher.matches("WPLAT-12", "WPLAT-123", null)); }
    @Test public void rejectsInvalidKey() { assertFalse(IssueKeyMatcher.matches("../secret", "../secret", null)); }
    @Test public void handlesNull() { assertFalse(IssueKeyMatcher.matches(null, null, null)); }
}
