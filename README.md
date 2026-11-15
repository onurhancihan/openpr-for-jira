# OpenPR for Jira

Open-source, read-only GitHub pull request viewer for Jira Data Center.

> Status: experimental build candidate. Maven CI passes as reported; Jira Data Center installation and runtime behavior remain unverified.

## Goals

- Display PRs related to Jira issue keys in issue view
- Support GitHub Enterprise Server and GitHub.com
- Show PR status, reviews and checks
- Never expose credentials to the browser
- Allow Jira issue viewers to see PR metadata only after administrator opt-in, even if they cannot open the PR in GitHub; GitHub still enforces access when clicked
- No external hosting or database requirement

## Development

Requires a compatible Java JDK, Maven and Atlassian's P2 build tooling. Target Jira versions and build compatibility must be verified before publishing a release.

This repository contains no corporate credentials, source code or real company data.

## Security

See [security notes](docs/SECURITY.md). Never commit tokens or private keys.

## License

Apache License 2.0. See [LICENSE](LICENSE).


## Build candidate (not a release)

Open **Actions → Build installable plugin candidate → Run workflow** to attempt a Maven build with Java 21. A successful workflow uploads a temporary JAR artifact. This is not evidence of working Jira installation.

## Known blockers to a production release

- Jira issue browse permission and environment-variable opt-in are wired into the panel, but real Jira issue-security behavior has not been integration-tested.
- Secure GitHub credentials and administrator configuration are not implemented.
- The panel now attempts to render matching PR metadata; runtime behavior remains unverified.
- GitHub API transport is integrated with a per-node, 60-second bounded cache; full SSRF and operational hardening remain incomplete.
- Build success and installation on a licensed Jira Data Center test instance are unverified.
- The current Apache license file is a short notice, not the complete official license text; it must be corrected before a release.

Do not install this development scaffold on a production Jira instance.
