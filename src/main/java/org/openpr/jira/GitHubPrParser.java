package org.openpr.jira;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/** Parse public PR metadata only; no tokens or arbitrary GitHub payloads are retained. */
public final class GitHubPrParser {
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private GitHubPrParser() {}

    public static List<GitHubPr> matching(String json, String issueKey) throws IOException {
        JsonNode root = MAPPER.readTree(json);
        if (!root.isArray()) throw new IOException("Expected PR array");
        List<GitHubPr> result = new ArrayList<>();
        for (JsonNode pr : root) {
            String title = value(pr, "title");
            String branch = value(pr.path("head"), "ref");
            if (!IssueKeyMatcher.matches(issueKey, title, branch)) continue;
            String state = pr.path("merged_at").isNull() || pr.path("merged_at").isMissingNode()
                    ? value(pr, "state") : "merged";
            result.add(new GitHubPr(pr.path("number").asLong(), title,
                    value(pr, "html_url"), state, pr.path("draft").asBoolean(false),
                    branch, value(pr.path("user"), "login")));
        }
        return List.copyOf(result);
    }

    private static String value(JsonNode node, String field) {
        return node.path(field).asText("");
    }
}
