package org.jenkinsci.plugins.saturatenodes;

import com.gargoylesoftware.htmlunit.WebAssert;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

public class PluginTest {
    @Rule
    public JenkinsRule j = new JenkinsRule();

    @Test
    public void testConfigExists() throws Exception {
        HtmlPage page = j.createWebClient().goTo("configure");
        WebAssert.assertTextPresent(page, "Saturate Nodes Load Balancer");
    }
}
