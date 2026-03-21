package pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;
import java.time.Duration;
import java.util.List;

public class AmazonLogin {

    private WebDriver driver;
    private WebDriverWait wait;

    // Constructor
    public AmazonLogin(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(20));
    }

    // Locators
    private By searchBox = By.id("twotabsearchtextbox");
    private By searchButton = By.id("nav-search-submit-button");
    private By acceptButton = By.id("sp-cc-accept");
    private By resultsContainer = By.cssSelector("div.s-main-slot");
    private By firstProduct = By.cssSelector("div.s-main-slot [data-component-type='s-search-result'] h2 a");
    private By addToCartButton = By.id("submit.add-to-cart");

    // Actions
    public void openAmazon() {
        driver.get("https://www.amazon.in");
    }

    public void acceptCookiesIfPresent() {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(acceptButton)).click();
        } catch (Exception ignored) {}
    }

    public void search(String keyword) {
        WebElement box = wait.until(
                ExpectedConditions.visibilityOfElementLocated(searchBox));
        box.clear();
        box.sendKeys(keyword);

        // Robust handling of stale autocomplete elements.
        boolean clickedSuggestion = false;
        for (int attempt = 0; attempt < 3 && !clickedSuggestion; attempt++) {
            try {
                List<WebElement> suggestions = wait.until(driver -> {
                    List<WebElement> s = driver.findElements(By.cssSelector("div.autocomplete-results-container div.s-suggestion"));
                    return s.isEmpty() ? null : s;
                });

                if (suggestions != null && !suggestions.isEmpty()) {
                    WebElement first = suggestions.get(0);
                    wait.until(ExpectedConditions.elementToBeClickable(first));
                    first.click();
                    clickedSuggestion = true;
                }
            } catch (StaleElementReferenceException | ElementNotInteractableException | TimeoutException e) {
                // retry, then fallback to ENTER
            }
        }

        if (!clickedSuggestion) {
            box.sendKeys(Keys.ENTER);
        }

        wait.until(ExpectedConditions.visibilityOfElementLocated(resultsContainer));
    }

    public void clickFirstProduct() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(resultsContainer));

        List<WebElement> productCards = wait.until(
                ExpectedConditions.presenceOfAllElementsLocatedBy(
                        By.cssSelector("div.s-main-slot div[data-component-type='s-search-result'][data-asin]:not([data-asin=''])")));

        WebElement productLink = productCards.stream()
                .map(this::findProductLink)
                .filter(java.util.Objects::nonNull)
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("No visible product link found"));

        scrollIntoViewAndClick(productLink);
    }

    private WebElement findProductLink(WebElement productCard) {
        try {
            WebElement link = productCard.findElement(By.cssSelector("h2 a"));
            if (link.isDisplayed()) return link;
        } catch (NoSuchElementException ignored) {
        }

        try {
            WebElement link = productCard.findElement(By.cssSelector("a.a-link-normal[href*='/dp/']"));
            if (link.isDisplayed()) return link;
        } catch (NoSuchElementException ignored) {
        }

        return null;
    }

    private void scrollIntoViewAndClick(WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({behavior:'instant', block:'center'});", element);
        try {
            wait.until(ExpectedConditions.elementToBeClickable(element));
            element.click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
        }
    }

    private WebElement findCartButton() {
        By[] cartLocators = new By[]{
                By.id("add-to-cart-button"),
                By.id("submit.add-to-cart"),
                By.cssSelector("#add-to-cart-button, input[name='submit.add-to-cart'], button[name='submit.add-to-cart']"),
                By.id("add-to-cart"),
                By.id("buy-now-button"),
                By.cssSelector("#aod-buy-box-container input[name='submit.add-to-cart'], #aod-offer-list .a-button-input")
        };

        for (By locator : cartLocators) {
            try {
                WebElement button = driver.findElement(locator);
                if (button.isDisplayed() && button.isEnabled()) {
                    return button;
                }
            } catch (NoSuchElementException ignored) {
            }
        }
        return null;
    }

    private void clickBestOfferIfNeeded() {
        By buyBoxAllChoices = By.id("buybox-see-all-buying-choices");
        try {
            WebElement allChoices = driver.findElement(buyBoxAllChoices);
            if (allChoices.isDisplayed()) {
                allChoices.click();
                WebElement firstSeller = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".s-suggestion-container .s-buybox-vertical .a-button-input, .aod-offer:first-of-type .a-button-input")));
                if (firstSeller != null) {
                    firstSeller.click();
                }
            }
        } catch (NoSuchElementException | TimeoutException ignored) {
        }
    }

    public void addToCart() {
        WebElement cartButton = null;

        // wait for page load and possible busy screen
        wait.until(ExpectedConditions.or(
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#add-to-cart-button, #submit.add-to-cart, #buy-now-button")),
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#buybox-see-all-buying-choices"))
        ));

        cartButton = findCartButton();
        if (cartButton == null) {
            clickBestOfferIfNeeded();
            cartButton = findCartButton();
        }

        if (cartButton == null) {
            throw new NoSuchElementException("Add to Cart / Buy Now button not found on product page (URL=" + driver.getCurrentUrl() + ")");
        }

        try {
            wait.until(ExpectedConditions.elementToBeClickable(cartButton));
            cartButton.click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", cartButton);
        }
    }

    public boolean isTitleContains(String text) {
        wait.until(ExpectedConditions.titleContains(text));
        return driver.getTitle().contains(text);
    }

    public boolean isUrlContains(String text) {
        return driver.getCurrentUrl().contains(text);
    }
}