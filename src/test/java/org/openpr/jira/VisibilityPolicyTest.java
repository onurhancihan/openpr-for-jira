package org.openpr.jira;

import org.junit.Test;
import static org.junit.Assert.*;

public class VisibilityPolicyTest {
    @Test public void sharesWithAnyIssueViewerWhenAdminOptsIn() {
        assertTrue(VisibilityPolicy.canShowMetadata(true, true, true));
    }
    @Test public void neverShowsToUsersWithoutIssueAccess() {
        assertFalse(VisibilityPolicy.canShowMetadata(false, true, true));
    }
    @Test public void defaultsToNoSharingUntilAdminOptIn() {
        assertFalse(VisibilityPolicy.canShowMetadata(true, false, true));
    }
    @Test public void requiresConfiguredConnection() {
        assertFalse(VisibilityPolicy.canShowMetadata(true, true, false));
    }
}
