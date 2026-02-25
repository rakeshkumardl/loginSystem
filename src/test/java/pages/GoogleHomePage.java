package pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;
import java.time.Duration;

public class GoogleHomePage {

    private WebDriver driver;
    private WebDriverWait wait;

    // Constructor
    public GoogleHomePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    // Locators
    private By searchBox = By.name("q");
    private By acceptButton = By.xpath("//button[contains(.,'Accept')]");

    // Actions
//    public void openGoogle() {
//        driver.get("https://www.google.com");
//    }

    public void acceptCookiesIfPresent() {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(acceptButton)).click();
        } catch (Exception ignored) {}
    }

    public void search(String keyword) {
        WebElement box = wait.until(
                ExpectedConditions.visibilityOfElementLocated(searchBox));
        box.sendKeys(keyword);
        box.submit();
    }

    public boolean isTitleContains(String text) {
        wait.until(ExpectedConditions.titleContains(text));
        return driver.getTitle().contains(text);
    }
}