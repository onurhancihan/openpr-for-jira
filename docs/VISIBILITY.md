# PR metadata visibility policy

## Product decision

OpenPR is designed to show PR **metadata** to anyone authorized to browse the related Jira issue, regardless of their personal GitHub repository permissions. Clicking a PR URL still uses GitHub's own authorization; OpenPR does not grant GitHub repository access.

Metadata may include PR titles, branch names, author names, state, and links. These fields can themselves be sensitive. Therefore the organization administrator must explicitly opt in to issue-viewer sharing; the default is disabled.

## Enforcement requirements (not yet implemented)

1. Jira server-side code must check the current user's Browse Projects / issue security permissions for the **specific issue**, not merely login state.
2. A trusted Jira administrator must configure and enable metadata sharing. Never accept this flag from a browser query parameter.
3. The GitHub token must be scoped to approved repositories with read-only permissions and stored securely, never sent to the browser.
4. Never render raw GitHub HTML. Escape title, branch and author fields. Validate links against the approved GitHub hostname.
5. Respect issue security restrictions, and avoid caching metadata across user or issue security boundaries.
6. The admin UI must disclose that users without GitHub access may still see PR metadata.

The `VisibilityPolicy` class expresses the intended authorization decision but is **not** wired to Jira yet. Do not treat it as an implemented access-control boundary.
