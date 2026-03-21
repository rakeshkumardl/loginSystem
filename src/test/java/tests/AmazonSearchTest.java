package tests;

import base.BaseTest;
import pages.AmazonLogin;
import org.testng.Assert;
import org.testng.annotations.Test;

public class AmazonSearchTest extends BaseTest {

    @Test
    public void testAmazonSearch() {

        AmazonLogin amazon = new AmazonLogin(getDriver());

        amazon.openAmazon();
        amazon.search("laptop");

        Assert.assertTrue(amazon.isUrlContains("k=laptop"));

    }
}