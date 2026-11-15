package org.openpr.jira;

import java.util.Locale;
import java.util.regex.Pattern;

/** Safe, case-insensitive issue-key matching with token boundaries. */
public final class IssueKeyMatcher {
    private IssueKeyMatcher() {}

    public static boolean matches(String issueKey, String pullRequestTitle, String branchName) {
        if (issueKey == null || !issueKey.matches("[A-Za-z][A-Za-z0-9_]*-[1-9][0-9]*")) return false;
        Pattern pattern = Pattern.compile("(?i)(?<![A-Za-z0-9])" + Pattern.quote(issueKey) + "(?![A-Za-z0-9])");
        return contains(pattern, pullRequestTitle) || contains(pattern, branchName);
    }

    private static boolean contains(Pattern pattern, String value) {
        return value != null && pattern.matcher(value).find();
    }
}
