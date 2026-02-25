package base;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.*;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.testng.ITestResult;
import org.testng.annotations.*;

import utils.ConfigReader;

import java.io.File;
import java.io.IOException;
import java.time.Duration;

import org.apache.commons.io.FileUtils;

public class BaseTest {

    private static ThreadLocal<WebDriver> driver = new ThreadLocal<>();

    // Getter for driver (IMPORTANT for parallel execution)
    public WebDriver getDriver() {
        return driver.get();
    }

    @BeforeMethod
    public void setup() {

        String browser = ConfigReader.get("browser");
        boolean headless = Boolean.parseBoolean(
                System.getProperty("headless",
                        ConfigReader.get("headless")));

        if (browser.equalsIgnoreCase("chrome")) {

            WebDriverManager.chromedriver().setup();
            ChromeOptions options = new ChromeOptions();

            if (headless) {
                options.addArguments("--headless=new");
                options.addArguments("--no-sandbox");
                options.addArguments("--disable-dev-shm-usage");
            }

            driver.set(new ChromeDriver(options));
        }

        else if (browser.equalsIgnoreCase("firefox")) {
            WebDriverManager.firefoxdriver().setup();
            driver.set(new FirefoxDriver());
        }

        else if (browser.equalsIgnoreCase("edge")) {
            WebDriverManager.edgedriver().setup();
            driver.set(new EdgeDriver());
        }

        // Use driver.get() always
        getDriver().manage().timeouts().implicitlyWait(
                Duration.ofSeconds(
                        Long.parseLong(ConfigReader.get("implicitWait"))));

        getDriver().manage().timeouts().pageLoadTimeout(
                Duration.ofSeconds(
                        Long.parseLong(ConfigReader.get("pageLoadTimeout"))));

        getDriver().manage().window().maximize();

        getDriver().get(ConfigReader.get("baseUrl"));
    }

    @AfterMethod
    public void tearDown(ITestResult result) {

        if (result.getStatus() == ITestResult.FAILURE) {
            takeScreenshot(result.getName());
        }

        if (getDriver() != null) {
            getDriver().quit();
            driver.remove();   // VERY IMPORTANT for parallel cleanup
        }
    }

    private void takeScreenshot(String testName) {

        TakesScreenshot ts = (TakesScreenshot) getDriver();
        File src = ts.getScreenshotAs(OutputType.FILE);

        try {
            FileUtils.copyFile(src,
                    new File("screenshots/" + testName + ".png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}