# OpenPR architecture and release gates

## Current state

The repository contains a P2 plugin descriptor, an issue-key matcher, a GitHub REST PR JSON parser, and unit tests. It does **not** yet contain a working Jira issue web panel, an authentication/configuration UI, or a tested installable release. Never install this scaffold on a production Jira instance.

## Target architecture

Jira issue view -> permission-checked server-side web panel -> authorized GitHub API client -> per-user permitted repository PR metadata. No external database or hosting is required. All requests must remain server-side. A GitHub App installation or another IT-approved read-only authentication model is required. An installation-wide token must not expose private repository metadata to users who lack access.

## Mandatory gates before first release

1. Confirm supported Jira Data Center and Java versions and Atlassian P2 dependencies.
2. Implement Jira web panel and admin configuration with permission checks.
3. Implement GitHub Enterprise Server and GitHub.com API connectivity with HTTPS, host allowlisting, SSRF protection, bounded pagination, timeouts, and rate limits.
4. Implement secret encryption and per-user GitHub repository authorization; no credentials in HTML, logs, or repository.
5. Add mocked integration tests for GitHub API and Jira panel, then test installation on an authorized Jira Data Center test environment (including multi-node behavior).
6. Perform dependency/security review, publish signed/tagged release artifacts and clear installation guidance.

## Cost model

Development source and GitHub public repository can be free under current GitHub plan limits. GitHub Actions usage is subject to GitHub quotas and policy changes. This project does not provision cloud resources, paid APIs, or databases. Jira Data Center itself requires the organization's existing license/infrastructure. No claim of guaranteed zero cost is made.
