package com.qa.automation.ui.tests;

import com.qa.automation.base.BaseTest;
import com.qa.automation.ui.pages.CartPage;
import com.qa.automation.ui.pages.InventoryPage;
import com.qa.automation.ui.pages.LoginPage;
import io.qameta.allure.*;
import java.util.List;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

@Epic("E-Commerce Platform")
@Feature("Product Inventory")
public class InventoryTest extends BaseTest {

    private InventoryPage inventoryPage;

    @BeforeMethod
    public void loginAndGoToInventory() {
        inventoryPage = new LoginPage(driver)
                .open(getBaseUrl())
                .loginAs("standard_user", "secret_sauce");
    }

    @Test(groups = {"smoke", "ui"})
    @Story("View product catalog")
    @Description("Inventory page displays all available products")
    @Severity(SeverityLevel.CRITICAL)
    public void testInventoryPageLoads() {
        assertThat(inventoryPage.isLoaded()).isTrue();
        assertThat(inventoryPage.getProductCount())
                .as("Should display 6 products")
                .isEqualTo(6);
    }

    @Test(groups = {"regression", "ui"})
    @Story("Add product to cart")
    @Severity(SeverityLevel.BLOCKER)
    public void testAddProductToCart() {
        inventoryPage.addFirstProductToCart();
        assertThat(inventoryPage.getCartItemCount())
                .as("Cart should have 1 item")
                .isEqualTo(1);
    }

    @Test(groups = {"regression", "ui"})
    @Story("Add multiple products to cart")
    @Severity(SeverityLevel.CRITICAL)
    public void testAddMultipleProductsToCart() {
        inventoryPage.addProductToCart(0)
                     .addProductToCart(1)
                     .addProductToCart(2);
        assertThat(inventoryPage.getCartItemCount()).isEqualTo(3);
    }

    @Test(groups = {"regression", "ui"})
    @Story("Cart reflects added items")
    @Severity(SeverityLevel.CRITICAL)
    public void testCartContainsAddedProduct() {
        String productName = inventoryPage.getProductName(0);
        inventoryPage.addProductToCart(0);
        CartPage cartPage = inventoryPage.goToCart();

        assertThat(cartPage.isLoaded()).isTrue();
        assertThat(cartPage.getItemNames()).contains(productName);
    }

    @Test(groups = {"regression", "ui"})
    @Story("All product names are non-empty")
    @Severity(SeverityLevel.NORMAL)
    public void testProductNamesAreNotEmpty() {
        List<String> names = inventoryPage.getAllProductNames();
        assertThat(names).isNotEmpty();
        names.forEach(name -> assertThat(name).as("Product name should not be blank").isNotBlank());
    }

    @Test(groups = {"regression", "ui"})
    @Story("Remove product from cart")
    @Severity(SeverityLevel.NORMAL)
    public void testRemoveItemFromCart() {
        inventoryPage.addProductToCart(0);
        CartPage cartPage = inventoryPage.goToCart();
        int initialCount = cartPage.getItemCount();

        cartPage.removeItem(0);
        assertThat(cartPage.getItemCount())
                .as("Item count should decrease by 1")
                .isEqualTo(initialCount - 1);
    }
}
