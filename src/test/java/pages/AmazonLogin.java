package pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;
import java.time.Duration;

public class AmazonLogin {

    private WebDriver driver;
    private WebDriverWait wait;

    // Constructor
    public AmazonLogin(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(20));
    }

    // Locators
    private By searchBox = By.name("field-keywords");
    private By searchButton = By.id("nav-search-submit-button");
    private By acceptButton = By.id("sp-cc-accept");

    // Actions
    public void openAmazon() {
        driver.get("https://www.amazon.com");
    }

    public void acceptCookiesIfPresent() {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(acceptButton)).click();
        } catch (Exception ignored) {}
    }

    public void search(String keyword) {
        WebElement box = wait.until(
                ExpectedConditions.visibilityOfElementLocated(searchBox));
        box.sendKeys(keyword);
        WebElement button = wait.until(
                ExpectedConditions.elementToBeClickable(searchButton));
        button.click();
    }

    public boolean isTitleContains(String text) {
        wait.until(ExpectedConditions.titleContains(text));
        return driver.getTitle().contains(text);
    }

    public boolean isUrlContains(String text) {
        return driver.getCurrentUrl().contains(text);
    }
}