package com.qa.automation.ui.pages;

import io.qameta.allure.Step;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class CheckoutPage extends BasePage {

    @FindBy(id = "first-name")
    private WebElement firstNameInput;

    @FindBy(id = "last-name")
    private WebElement lastNameInput;

    @FindBy(id = "postal-code")
    private WebElement postalCodeInput;

    @FindBy(id = "continue")
    private WebElement continueButton;

    @FindBy(id = "finish")
    private WebElement finishButton;

    @FindBy(css = ".complete-header")
    private WebElement orderConfirmation;

    @FindBy(css = ".summary_total_label")
    private WebElement totalLabel;

    @FindBy(css = "[data-test='error']")
    private WebElement errorMessage;

    public CheckoutPage(WebDriver driver) {
        super(driver);
    }

    @Step("Fill checkout information: {firstName} {lastName}, zip: {postalCode}")
    public CheckoutPage fillInformation(String firstName, String lastName, String postalCode) {
        type(firstNameInput, firstName);
        type(lastNameInput, lastName);
        type(postalCodeInput, postalCode);
        log.info("Checkout info filled for: {} {}", firstName, lastName);
        return this;
    }

    @Step("Continue to order summary")
    public CheckoutPage clickContinue() {
        click(continueButton);
        return this;
    }

    @Step("Finish and place order")
    public CheckoutPage clickFinish() {
        click(finishButton);
        return this;
    }

    @Step("Complete full checkout flow")
    public CheckoutPage completeCheckout(String firstName, String lastName, String postalCode) {
        return fillInformation(firstName, lastName, postalCode)
                .clickContinue()
                .clickFinish();
    }

    @Step("Verify order confirmation")
    public boolean isOrderConfirmed() {
        return isDisplayed(orderConfirmation) &&
               getText(orderConfirmation).equalsIgnoreCase("Thank you for your order!");
    }

    public String getOrderConfirmationText() {
        return getText(orderConfirmation);
    }

    public String getTotalAmount() {
        return getText(totalLabel);
    }

    public boolean isErrorDisplayed() {
        return isDisplayed(errorMessage);
    }

    public String getErrorMessage() {
        return getText(errorMessage);
    }

    public boolean isOnCheckoutStep1() {
        return getCurrentUrl().contains("checkout-step-one");
    }

    public boolean isOnCheckoutStep2() {
        return getCurrentUrl().contains("checkout-step-two");
    }

    public boolean isOnConfirmationPage() {
        return getCurrentUrl().contains("checkout-complete");
    }
}
