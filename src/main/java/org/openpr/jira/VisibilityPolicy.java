package org.openpr.jira;

/**
 * Explicit organization-wide PR metadata sharing policy.
 *
 * The Jira integration MUST independently check the current user's permission
 * to browse the specific issue before calling this method.
 * The administrator must explicitly opt in to sharing metadata.
 * GitHub itself remains responsible for access to the PR destination.
 */
public final class VisibilityPolicy {
    private VisibilityPolicy() {}

    public static boolean canShowMetadata(boolean jiraIssueBrowsePermitted,
                                          boolean adminEnabledSharing,
                                          boolean githubConnectionConfigured) {
        return jiraIssueBrowsePermitted && adminEnabledSharing && githubConnectionConfigured;
    }
}
