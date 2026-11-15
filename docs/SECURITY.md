# Security model (planned)

This is an early scaffold, not a production-ready plugin.

- Never commit GitHub tokens, app keys or corporate information.
- Prefer GitHub App authentication with minimal read-only permissions.
- Do not expose secrets or GitHub API credentials to browser clients.
- Enforce Jira and GitHub repository authorization; a shared service token alone is not sufficient for per-user access control.
- Validate enterprise API URLs and prevent SSRF.
- Set timeouts, bounded caches, pagination and rate limits.
- Protect administrative configuration and secrets at rest.
- Review third-party dependencies and test on multi-node Jira Data Center before release.
