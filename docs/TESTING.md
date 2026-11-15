# Testing without corporate access

The project currently contains a deterministic issue-key matcher and unit tests.

Run `mvn test` with a compatible JDK and Maven installation. The GitHub Actions workflow runs the same tests on each push and pull request.

## Still required before any Jira installation

- Verify Atlassian P2 dependencies resolve for the selected Jira Data Center release.
- Compile and test the actual Jira web panel module against supported Jira versions.
- Test on an authorized Jira Data Center test instance, including clustered nodes.
- Implement GitHub App authentication and per-user repository authorization.
- Add HTTP mocking for Enterprise API responses, failures, pagination, and rate limits.
- Security review and administrator approval.

No corporate information, credentials or private repositories are needed for these initial tests.
