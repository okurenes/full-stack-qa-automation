package com.qa.automation.ui.tests;

import com.qa.automation.base.BaseTest;
import com.qa.automation.ui.pages.CartPage;
import com.qa.automation.ui.pages.CheckoutPage;
import com.qa.automation.ui.pages.InventoryPage;
import com.qa.automation.ui.pages.LoginPage;
import io.qameta.allure.*;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

@Epic("E-Commerce Platform")
@Feature("Checkout Flow")
public class CheckoutTest extends BaseTest {

    private CartPage cartPage;

    @BeforeMethod
    public void addProductAndGoToCart() {
        cartPage = new LoginPage(driver)
                .open(getBaseUrl())
                .loginAs("standard_user", "secret_sauce")
                .addFirstProductToCart()
                .goToCart();
    }

    @Test(groups = {"smoke", "ui"})
    @Story("Complete purchase end-to-end")
    @Description("User can complete a full purchase from cart to order confirmation")
    @Severity(SeverityLevel.BLOCKER)
    public void testCompleteCheckoutFlow() {
        CheckoutPage checkoutPage = cartPage.proceedToCheckout();
        assertThat(checkoutPage.isOnCheckoutStep1()).isTrue();

        checkoutPage.completeCheckout("Ali", "Yilmaz", "34000");
        assertThat(checkoutPage.isOnConfirmationPage()).isTrue();
        assertThat(checkoutPage.isOrderConfirmed()).isTrue();
        assertThat(checkoutPage.getOrderConfirmationText())
                .isEqualTo("Thank you for your order!");
    }

    @Test(groups = {"regression", "ui"})
    @Story("Checkout fails with missing first name")
    @Severity(SeverityLevel.CRITICAL)
    public void testCheckoutWithMissingFirstName() {
        CheckoutPage checkoutPage = cartPage.proceedToCheckout();
        checkoutPage.fillInformation("", "Yilmaz", "34000").clickContinue();

        assertThat(checkoutPage.isErrorDisplayed()).isTrue();
        assertThat(checkoutPage.getErrorMessage()).contains("First Name is required");
    }

    @Test(groups = {"regression", "ui"})
    @Story("Checkout fails with missing postal code")
    @Severity(SeverityLevel.NORMAL)
    public void testCheckoutWithMissingPostalCode() {
        CheckoutPage checkoutPage = cartPage.proceedToCheckout();
        checkoutPage.fillInformation("Ali", "Yilmaz", "").clickContinue();

        assertThat(checkoutPage.isErrorDisplayed()).isTrue();
        assertThat(checkoutPage.getErrorMessage()).contains("Postal Code is required");
    }

    @Test(groups = {"regression", "ui"})
    @Story("Confirmation page URL is correct after order")
    @Severity(SeverityLevel.NORMAL)
    public void testConfirmationPageUrl() {
        CheckoutPage checkoutPage = cartPage.proceedToCheckout();
        checkoutPage.completeCheckout("Ali", "Yilmaz", "34000");
        assertThat(checkoutPage.getCurrentUrl()).contains("checkout-complete");
    }

    @Test(groups = {"regression", "ui"})
    @Story("Order summary shows total before confirming")
    @Severity(SeverityLevel.NORMAL)
    public void testOrderSummaryIsDisplayed() {
        CheckoutPage checkoutPage = cartPage.proceedToCheckout();
        checkoutPage.fillInformation("Ali", "Yilmaz", "34000").clickContinue();

        assertThat(checkoutPage.isOnCheckoutStep2()).isTrue();
        assertThat(checkoutPage.getTotalAmount()).contains("Total:");
    }
}
