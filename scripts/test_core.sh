#!/usr/bin/env bash
set -euo pipefail
tmp="$(mktemp -d)"
trap 'rm -rf "$tmp"' EXIT
javac -d "$tmp" src/main/java/org/openpr/jira/IssueKeyMatcher.java
cat > "$tmp/CoreSmoke.java" <<'JAVA'
import org.openpr.jira.IssueKeyMatcher;
public class CoreSmoke {
  public static void main(String[] args) {
    check(IssueKeyMatcher.matches("DEMO-12", "Fix DEMO-12 bug", null));
    check(IssueKeyMatcher.matches("DEMO-12", null, "feature/demo-12-work"));
    check(!IssueKeyMatcher.matches("DEMO-12", "Fix DEMO-123 bug", null));
    check(!IssueKeyMatcher.matches("../secret", "../secret", null));
    check(!IssueKeyMatcher.matches(null, null, null));
    System.out.println("Core issue-key smoke tests passed");
  }
  private static void check(boolean result) {
    if (!result) throw new AssertionError("Core smoke test failed");
  }
}
JAVA
javac -cp "$tmp" -d "$tmp" "$tmp/CoreSmoke.java"
java -cp "$tmp" CoreSmoke
