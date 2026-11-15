# Release checklist — no paid services

A GitHub Actions `workflow_dispatch` build can create a short-lived JAR artifact using the repository's existing free GitHub Actions allowance. A successful workflow proves packaging only, not Jira Data Center compatibility or security.

1. Open GitHub Actions > **Build installable plugin candidate** > Run workflow.
2. Confirm all checks pass; download the `openpr-jira-plugin-candidate` artifact.
3. Inspect JAR and dependencies, and run a clean authorized Jira Data Center test installation.
4. Verify the issue panel, Jira permissions, issue security, GitHub authentication and clustered behavior.
5. Publish a release only after the mandatory checks in `docs/ARCHITECTURE.md` pass.

**Do not install this scaffold in production.** The panel is still a placeholder; administrator configuration, credential storage, GitHub connection wiring and Jira permission enforcement are not implemented.

Cost: no hosting or database is provisioned. GitHub Actions is subject to account quota. No paid Jira test license or infrastructure is provisioned by this workflow.
