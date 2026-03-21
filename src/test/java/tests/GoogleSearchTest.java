package tests;

import base.BaseTest;
import pages.GoogleHomePage;
import org.testng.Assert;
import org.testng.annotations.Test;

public class GoogleSearchTest extends BaseTest {

    @Test
    public void testGoogleSearch() {

        GoogleHomePage google = new GoogleHomePage(getDriver());

        google.acceptCookiesIfPresent();
        String query = "AI testing tool";
        google.search(query);

        Assert.assertTrue(google.isSearchQueryInUrl(query), "Expected search query in URL (normalized), got=" + getDriver().getCurrentUrl());

    }
}