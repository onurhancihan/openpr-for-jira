package org.openpr.jira;

import org.junit.Test;
import java.util.HashMap;
import java.util.Map;
import static org.junit.Assert.*;

public class OpenPrConfigurationTest {
    private Map<String,String> valid() {
        Map<String,String> config = new HashMap<>();
        config.put("OPENPR_ENABLED", "true");
        config.put("OPENPR_GITHUB_ORIGIN", "https://github.com");
        config.put("OPENPR_GITHUB_HOST", "github.com");
        config.put("OPENPR_GITHUB_TOKEN", "test-only");
        config.put("OPENPR_REPOSITORIES", "team/service,team/another");
        return config;
    }
    @Test public void disabledDoesNotRequireSecrets() {
        assertFalse(OpenPrConfiguration.from(Map.of()).enabled());
    }
    @Test public void acceptsApprovedRepositories() {
        assertEquals(2, OpenPrConfiguration.from(valid()).repositories().size());
    }
    @Test(expected = IllegalArgumentException.class)
    public void rejectsOriginMismatch() {
        Map<String,String> config = valid();
        config.put("OPENPR_GITHUB_ORIGIN", "https://other.example.com");
        OpenPrConfiguration.from(config);
    }
    @Test(expected = IllegalArgumentException.class)
    public void rejectsInvalidRepository() {
        Map<String,String> config = valid();
        config.put("OPENPR_REPOSITORIES", "team/../repo");
        OpenPrConfiguration.from(config);
    }
    @Test(expected = IllegalArgumentException.class)
    public void rejectsDuplicateRepository() {
        Map<String,String> config = valid();
        config.put("OPENPR_REPOSITORIES", "team/service,team/service");
        OpenPrConfiguration.from(config);
    }
    @Test(expected = IllegalArgumentException.class)
    public void rejectsNewlineInToken() {
        Map<String,String> config = valid();
        config.put("OPENPR_GITHUB_TOKEN", "token\nunsafe");
        OpenPrConfiguration.from(config);
    }
}
