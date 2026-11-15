package org.openpr.jira;

import org.junit.Test;
import java.util.List;
import static org.junit.Assert.*;

public class GitHubPrParserTest {
    @Test public void filtersAndReadsPullRequests() throws Exception {
        String json = "[{\"number\":42,\"title\":\"Fix DEMO-12\",\"html_url\":\"https://github.example/pr/42\",\"state\":\"open\",\"draft\":true,\"head\":{\"ref\":\"feature/demo-12\"},\"user\":{\"login\":\"sample\"}}, {\"number\":43,\"title\":\"Unrelated\",\"head\":{\"ref\":\"other\"}}]";
        List<GitHubPr> prs = GitHubPrParser.matching(json, "DEMO-12");
        assertEquals(1, prs.size());
        assertEquals(42, prs.get(0).number());
        assertTrue(prs.get(0).draft());
        assertEquals("sample", prs.get(0).author());
    }
    @Test public void rejectsNonArray() throws Exception {
        try { GitHubPrParser.matching("{}", "DEMO-12"); fail("Should reject object"); }
        catch (java.io.IOException expected) { assertTrue(expected.getMessage().contains("array")); }
    }
}
