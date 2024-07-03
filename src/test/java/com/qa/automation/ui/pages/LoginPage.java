package com.qa.automation.ui.pages;

import io.qameta.allure.Step;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class LoginPage extends BasePage {

    @FindBy(id = "user-name")
    private WebElement usernameInput;

    @FindBy(id = "password")
    private WebElement passwordInput;

    @FindBy(id = "login-button")
    private WebElement loginButton;

    @FindBy(css = "[data-test='error']")
    private WebElement errorMessage;

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    @Step("Navigate to login page")
    public LoginPage open(String baseUrl) {
        driver.get(baseUrl);
        log.info("Navigated to: {}", baseUrl);
        return this;
    }

    @Step("Enter username: {username}")
    public LoginPage enterUsername(String username) {
        type(usernameInput, username);
        return this;
    }

    @Step("Enter password")
    public LoginPage enterPassword(String password) {
        type(passwordInput, password);
        return this;
    }

    @Step("Click login button")
    public InventoryPage clickLogin() {
        click(loginButton);
        return new InventoryPage(driver);
    }

    @Step("Login with credentials: {username}")
    public InventoryPage loginAs(String username, String password) {
        return enterUsername(username)
                .enterPassword(password)
                .clickLogin();
    }

    @Step("Attempt login with invalid credentials")
    public LoginPage loginWithInvalidCredentials(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        click(loginButton);
        return this;
    }

    public String getErrorMessage() {
        return getText(errorMessage);
    }

    public boolean isErrorDisplayed() {
        return isDisplayed(errorMessage);
    }

    public boolean isOnLoginPage() {
        return driver.getCurrentUrl().contains("saucedemo.com") && isDisplayed(loginButton);
    }
}
