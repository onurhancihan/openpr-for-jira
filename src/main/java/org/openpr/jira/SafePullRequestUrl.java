package org.openpr.jira;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;

/** Rejects unsafe PR links before displaying them in Jira. */
public final class SafePullRequestUrl {
    private SafePullRequestUrl() {}

    /** Only absolute HTTPS URLs on the configured GitHub host are allowed. */
    public static boolean isAllowed(String rawUrl, String configuredHost) {
        if (rawUrl == null || configuredHost == null || configuredHost.isBlank()) return false;
        try {
            URI url = new URI(rawUrl);
            if (!"https".equalsIgnoreCase(url.getScheme()) || url.getHost() == null) return false;
            if (url.getUserInfo() != null || url.getFragment() != null) return false;
            if (url.getPort() != -1 && url.getPort() != 443) return false;
            String host = url.getHost().toLowerCase(Locale.ROOT);
            if (!host.equals(configuredHost.toLowerCase(Locale.ROOT))) return false;
            String path = url.getRawPath();
            return path != null && path.matches("/[^/]+/[^/]+/pull/[1-9][0-9]*/?");
        } catch (URISyntaxException e) {
            return false;
        }
    }
}
