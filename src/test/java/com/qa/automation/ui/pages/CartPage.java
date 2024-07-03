package com.qa.automation.ui.pages;

import io.qameta.allure.Step;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.List;

public class CartPage extends BasePage {

    @FindBy(css = ".cart_item_label .inventory_item_name")
    private List<WebElement> cartItemNames;

    @FindBy(css = ".cart_item_label .inventory_item_price")
    private List<WebElement> cartItemPrices;

    @FindBy(css = "button[data-test^='remove']")
    private List<WebElement> removeButtons;

    @FindBy(id = "checkout")
    private WebElement checkoutButton;

    @FindBy(id = "continue-shopping")
    private WebElement continueShoppingButton;

    @FindBy(css = ".title")
    private WebElement pageTitle;

    public CartPage(WebDriver driver) {
        super(driver);
    }

    @Step("Verify cart page is loaded")
    public boolean isLoaded() {
        return getCurrentUrl().contains("/cart") && isDisplayed(pageTitle);
    }

    @Step("Get cart item count")
    public int getItemCount() {
        return cartItemNames.size();
    }

    @Step("Get cart item names")
    public List<String> getItemNames() {
        return cartItemNames.stream().map(WebElement::getText).toList();
    }

    @Step("Remove item at index {index}")
    public CartPage removeItem(int index) {
        click(removeButtons.get(index));
        return this;
    }

    @Step("Proceed to checkout")
    public CheckoutPage proceedToCheckout() {
        click(checkoutButton);
        return new CheckoutPage(driver);
    }

    @Step("Continue shopping")
    public InventoryPage continueShopping() {
        click(continueShoppingButton);
        return new InventoryPage(driver);
    }

    public boolean isCartEmpty() {
        return cartItemNames.isEmpty();
    }
}
