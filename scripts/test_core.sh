#!/usr/bin/env bash
set -euo pipefail
tmp="$(mktemp -d)"
trap 'rm -rf "$tmp"' EXIT
javac -d "$tmp" src/main/java/org/openpr/jira/IssueKeyMatcher.java src/main/java/org/openpr/jira/VisibilityPolicy.java src/main/java/org/openpr/jira/SafePullRequestUrl.java
cat > "$tmp/CoreSmoke.java" <<'JAVA'
import org.openpr.jira.IssueKeyMatcher;
import org.openpr.jira.VisibilityPolicy;
import org.openpr.jira.SafePullRequestUrl;
public class CoreSmoke {
  public static void main(String[] args) {
    check(IssueKeyMatcher.matches("DEMO-12", "Fix DEMO-12 bug", null));
    check(IssueKeyMatcher.matches("DEMO-12", null, "feature/demo-12-work"));
    check(!IssueKeyMatcher.matches("DEMO-12", "Fix DEMO-123 bug", null));
    check(!IssueKeyMatcher.matches("../secret", "../secret", null));
    check(!IssueKeyMatcher.matches(null, null, null));
    check(VisibilityPolicy.canShowMetadata(true, true, true));
    check(!VisibilityPolicy.canShowMetadata(false, true, true));
    check(!VisibilityPolicy.canShowMetadata(true, false, true));
    check(SafePullRequestUrl.isAllowed("https://github.example.org/team/repo/pull/42", "github.example.org"));
    check(!SafePullRequestUrl.isAllowed("https://github.example.org.attacker.com/team/repo/pull/42", "github.example.org"));
    System.out.println("Core security and issue-key smoke tests passed");
  }
  private static void check(boolean result) {
    if (!result) throw new AssertionError("Core smoke test failed");
  }
}
JAVA
javac -cp "$tmp" -d "$tmp" "$tmp/CoreSmoke.java"
java -cp "$tmp" CoreSmoke
