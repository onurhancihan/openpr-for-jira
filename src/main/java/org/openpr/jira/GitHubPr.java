package org.openpr.jira;

/** Minimal, immutable public PR metadata. */
public record GitHubPr(long number, String title, String url, String state,
                       boolean draft, String headBranch, String author) {}
