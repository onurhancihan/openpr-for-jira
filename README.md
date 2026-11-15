# OpenPR for Jira

Open-source, read-only GitHub pull request viewer for Jira Data Center.

> Status: early scaffold. Not yet a working or tested Jira plugin.

## Goals

- Display PRs related to Jira issue keys in issue view
- Support GitHub Enterprise Server and GitHub.com
- Show PR status, reviews and checks
- Never expose credentials to the browser
- Respect repository access permissions
- No external hosting or database requirement

## Development

Requires a compatible Java JDK, Maven and Atlassian's P2 build tooling. Target Jira versions and build compatibility must be verified before publishing a release.

This repository contains no corporate credentials, source code or real company data.

## Security

See [security notes](docs/SECURITY.md). Never commit tokens or private keys.

## License

Apache License 2.0. See [LICENSE](LICENSE).
