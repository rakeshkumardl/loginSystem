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
        google.search("Selenium WebDriver");

        Assert.assertTrue(google.isTitleContains("Selenium"));
    }
}