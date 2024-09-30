package com.qa.automation.ui.pages;

import io.qameta.allure.Step;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.List;

public class InventoryPage extends BasePage {

    @FindBy(css = ".inventory_item_name")
    private List<WebElement> productNames;

    @FindBy(css = ".inventory_item_price")
    private List<WebElement> productPrices;

    @FindBy(css = "button[data-test^='add-to-cart']")
    private List<WebElement> addToCartButtons;

    @FindBy(css = ".shopping_cart_badge")
    private WebElement cartBadge;

    @FindBy(css = ".shopping_cart_link")
    private WebElement cartIcon;

    @FindBy(css = "[data-test='product_sort_container']")
    private WebElement sortDropdown;

    @FindBy(css = ".title")
    private WebElement pageTitle;

    public InventoryPage(WebDriver driver) {
        super(driver);
    }

    @Step("Verify inventory page is loaded")
    public boolean isLoaded() {
        return getCurrentUrl().contains("/inventory") && isDisplayed(pageTitle);
    }

    @Step("Get product count")
    public int getProductCount() {
        return productNames.size();
    }

    @Step("Add first product to cart")
    public InventoryPage addFirstProductToCart() {
        if (!addToCartButtons.isEmpty()) {
            click(addToCartButtons.get(0));
            log.info("Added first product to cart");
        }
        return this;
    }

    @Step("Add product at index {index} to cart")
    public InventoryPage addProductToCart(int index) {
        click(addToCartButtons.get(index));
        log.info("Added product at index {} to cart", index);
        return this;
    }

    @Step("Get cart item count")
    public int getCartItemCount() {
        try {
            return Integer.parseInt(getText(cartBadge));
        } catch (Exception e) {
            return 0;
        }
    }

    @Step("Navigate to cart")
    public CartPage goToCart() {
        click(cartIcon);
        return new CartPage(driver);
    }

    @Step("Get product name at index {index}")
    public String getProductName(int index) {
        return getText(productNames.get(index));
    }

    @Step("Get all product names")
    public List<String> getAllProductNames() {
        return productNames.stream().map(WebElement::getText).toList();
    }

    @Step("Get product price at index {index}")
    public String getProductPrice(int index) {
        return getText(productPrices.get(index));
    }

    @Step("Get all product prices")
    public List<String> getAllProductPrices() {
        return productPrices.stream().map(WebElement::getText).toList();
    }

    public String getPageHeading() {
        return getText(pageTitle);
    }
}
