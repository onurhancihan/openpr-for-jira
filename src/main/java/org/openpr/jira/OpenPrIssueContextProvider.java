package org.openpr.jira;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.permission.ProjectPermissions;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.web.ContextProvider;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Jira server-side issue panel. No GitHub token or raw API response is sent to the browser.
 *
 * Experimental deployment configuration: environment variables on EACH Jira node:
 * OPENPR_ENABLED=true, OPENPR_GITHUB_ORIGIN=https://github.com,
 * OPENPR_GITHUB_HOST=github.com, OPENPR_REPOSITORIES=owner/repo[,owner/repo],
 * OPENPR_GITHUB_TOKEN=<read-only token>.
 * This is not a replacement for a secure Jira administrator configuration screen.
 */
public final class OpenPrIssueContextProvider implements ContextProvider {
    @Override public void init(Map<String, String> params) throws PluginParseException {}

    @Override public Map<String, Object> getContextMap(Map<String, Object> context) {
        Map<String, Object> result = new HashMap<>();
        result.put("openprRows", List.of());
        result.put("openprStatus", "Not configured");
        Object candidate = context.get("issue");
        if (!(candidate instanceof Issue issue)) return result;
        ApplicationUser user = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser();
        boolean allowed = user != null && ComponentAccessor.getPermissionManager()
                .hasPermission(ProjectPermissions.BROWSE_PROJECTS, issue, user);
        boolean enabled = "true".equalsIgnoreCase(System.getenv("OPENPR_ENABLED"));
        String origin = System.getenv("OPENPR_GITHUB_ORIGIN");
        String host = System.getenv("OPENPR_GITHUB_HOST");
        String token = System.getenv("OPENPR_GITHUB_TOKEN");
        String repoNames = System.getenv("OPENPR_REPOSITORIES");
        if (!allowed || !enabled || origin == null || host == null || token == null || repoNames == null) {
            return result;
        }
        try {
            List<PullRequestLookup.Repository> repos = new ArrayList<>();
            for (String entry : repoNames.split(",")) {
                String[] parts = entry.trim().split("/", -1);
                if (parts.length != 2 || parts[0].isBlank() || parts[1].isBlank()) {
                    throw new IllegalArgumentException("Invalid repository configuration");
                }
                repos.add(new PullRequestLookup.Repository(parts[0], parts[1]));
            }
            PullRequestLookup lookup = new PullRequestLookup(new GitHubApiClient()::listPullRequests);
            List<Map<String, String>> rows = new ArrayList<>();
            for (GitHubPr pr : lookup.find(origin, host, token, repos, issue.getKey(), allowed, enabled)) {
                Map<String, String> row = new HashMap<>();
                row.put("title", escape(pr.title()));
                row.put("url", escape(pr.url()));
                row.put("state", escape(pr.state()));
                row.put("author", escape(pr.author()));
                row.put("branch", escape(pr.headBranch()));
                row.put("number", Long.toString(pr.number()));
                rows.add(row);
            }
            result.put("openprRows", rows);
            result.put("openprStatus", rows.isEmpty() ? "No matching pull requests" : "");
        } catch (Exception ex) {
            // Do not disclose configuration, tokens, response bodies, or internal hostnames.
            result.put("openprStatus", "Pull requests temporarily unavailable");
        }
        return result;
    }

    static String escape(String value) {
        if (value == null) return "";
        return value.replace("&", "&amp;").replace("<", "&lt;")
                .replace(">", "&gt;").replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}
