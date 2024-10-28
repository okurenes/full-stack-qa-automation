package com.qa.automation.bdd.steps;

import com.qa.automation.ui.pages.CartPage;
import com.qa.automation.ui.pages.CheckoutPage;
import com.qa.automation.ui.pages.InventoryPage;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.assertj.core.api.Assertions.assertThat;

public class CheckoutSteps {

    private InventoryPage inventoryPage;
    private CartPage cartPage;
    private CheckoutPage checkoutPage;

    @When("I add a product to the cart")
    public void iAddAProductToTheCart() {
        inventoryPage = new InventoryPage(SharedDriver.getDriver());
        inventoryPage.addFirstProductToCart();
    }

    @And("I navigate to the cart")
    public void iNavigateToTheCart() {
        cartPage = inventoryPage.goToCart();
        assertThat(cartPage.isLoaded()).isTrue();
    }

    @And("I click the checkout button")
    public void iClickTheCheckoutButton() {
        checkoutPage = cartPage.proceedToCheckout();
    }

    @Then("I should be on checkout step one")
    public void iShouldBeOnCheckoutStepOne() {
        assertThat(checkoutPage.isOnCheckoutStep1()).isTrue();
    }

    @And("I fill checkout info with first name {string} last name {string} postal code {string}")
    public void iFillCheckoutInfo(String firstName, String lastName, String postalCode) {
        checkoutPage.fillInformation(firstName, lastName, postalCode);
    }

    @And("I click continue on checkout")
    public void iClickContinueOnCheckout() {
        checkoutPage.clickContinue();
    }

    @And("I click finish on checkout")
    public void iClickFinishOnCheckout() {
        checkoutPage.clickFinish();
    }

    @Then("I should see a checkout error containing {string}")
    public void iShouldSeeACheckoutErrorContaining(String errorText) {
        assertThat(checkoutPage.isErrorDisplayed()).isTrue();
        assertThat(checkoutPage.getErrorMessage()).contains(errorText);
    }

    @Then("I should see the order confirmation message")
    public void iShouldSeeTheOrderConfirmationMessage() {
        assertThat(checkoutPage.isOrderConfirmed()).isTrue();
    }
}
