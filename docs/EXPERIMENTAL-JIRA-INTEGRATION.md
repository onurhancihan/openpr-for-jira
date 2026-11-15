# Experimental Jira integration

This is an **unverified development build**, not production-ready.

The issue panel now uses a server-side Jira ContextProvider and an opt-in GitHub lookup. To configure an isolated test instance, set the following environment variables **on every Jira Data Center node** and restart Jira:

- `OPENPR_ENABLED=true` — explicit opt-in to exposing PR metadata to all Jira issue viewers.
- `OPENPR_GITHUB_ORIGIN=https://github.com` — trusted GitHub origin.
- `OPENPR_GITHUB_HOST=github.com` — approved PR URL host.
- `OPENPR_REPOSITORIES=owner/repo,owner/another-repo` — maximum 10.
- `OPENPR_GITHUB_TOKEN` — read-only token, supplied via an approved secret-management mechanism, never committed.

**Security warnings:** Jira issue access is checked before lookup, but the exact Jira issue-context and permission behavior must be verified against a real Jira DC instance. This experimental configuration does not provide a Jira administrator UI, credential encryption at rest, a cache, cluster-wide settings, or robust outbound SSRF controls. It can trigger GitHub requests on every issue view. Do not enable it on production Jira. Restrict outbound network destinations at the firewall/proxy and use only trusted GitHub hosts. No corporate token or repository names should be put in the public repository.

PR metadata is intentionally visible to every Jira user who may browse the issue when the administrator opts in, even if they cannot access the linked GitHub repository. GitHub controls access to the destination.
