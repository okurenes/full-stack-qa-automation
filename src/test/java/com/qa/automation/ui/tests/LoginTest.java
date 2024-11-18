package com.qa.automation.ui.tests;

import com.qa.automation.base.BaseTest;
import com.qa.automation.ui.pages.InventoryPage;
import com.qa.automation.ui.pages.LoginPage;
import io.qameta.allure.*;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

@Epic("E-Commerce Platform")
@Feature("Authentication")
public class LoginTest extends BaseTest {

    private LoginPage loginPage;

    @BeforeMethod
    public void openLoginPage() {
        loginPage = new LoginPage(driver);
        loginPage.open(getBaseUrl());
    }

    @Test(groups = {"smoke", "ui"})
    @Story("Successful login")
    @Description("Standard user can log in with valid credentials")
    @Severity(SeverityLevel.BLOCKER)
    public void testSuccessfulLogin() {
        InventoryPage inventoryPage = loginPage.loginAs("standard_user", "secret_sauce");
        assertThat(inventoryPage.isLoaded())
                .as("Inventory page should load after successful login")
                .isTrue();
        assertThat(inventoryPage.getPageHeading()).isEqualTo("Products");
    }

    @Test(groups = {"regression", "ui"})
    @Story("Failed login - wrong password")
    @Severity(SeverityLevel.CRITICAL)
    public void testLoginWithWrongPassword() {
        loginPage.loginWithInvalidCredentials("standard_user", "wrong_password");
        assertThat(loginPage.isErrorDisplayed()).isTrue();
        assertThat(loginPage.getErrorMessage())
                .contains("Username and password do not match");
    }

    @Test(groups = {"regression", "ui"})
    @Story("Failed login - empty credentials")
    @Severity(SeverityLevel.NORMAL)
    public void testLoginWithEmptyCredentials() {
        loginPage.loginWithInvalidCredentials("", "");
        assertThat(loginPage.isErrorDisplayed()).isTrue();
        assertThat(loginPage.getErrorMessage()).contains("Username is required");
    }

    @Test(groups = {"regression", "ui"})
    @Story("Locked out user cannot login")
    @Severity(SeverityLevel.CRITICAL)
    public void testLockedOutUserLogin() {
        loginPage.loginWithInvalidCredentials("locked_out_user", "secret_sauce");
        assertThat(loginPage.isErrorDisplayed()).isTrue();
        assertThat(loginPage.getErrorMessage()).contains("locked out");
    }

    @Test(dataProvider = "validUsers", groups = {"regression", "ui"})
    @Story("Multiple user types can log in")
    @Severity(SeverityLevel.NORMAL)
    public void testMultipleUserTypesCanLogin(String username, String password) {
        InventoryPage inventoryPage = loginPage.loginAs(username, password);
        assertThat(inventoryPage.isLoaded())
                .as("User '%s' should be able to log in", username)
                .isTrue();
    }

    @Test(groups = {"regression", "ui"})
    @Story("Login page title is correct")
    @Severity(SeverityLevel.MINOR)
    public void testLoginPageTitle() {
        assertThat(loginPage.getPageTitle()).isEqualTo("Swag Labs");
    }

    @Test(groups = {"smoke", "ui"})
    @Story("Login button is visible on page load")
    @Severity(SeverityLevel.NORMAL)
    public void testLoginButtonIsDisplayed() {
        assertThat(loginPage.isOnLoginPage()).isTrue();
    }

    @Test(groups = {"regression", "ui"})
    @Story("Username with leading/trailing spaces is rejected")
    @Severity(SeverityLevel.MINOR)
    public void testLoginWithWhitespaceUsername() {
        loginPage.loginWithInvalidCredentials("  standard_user  ", "secret_sauce");
        assertThat(loginPage.isErrorDisplayed())
                .as("Login with padded spaces should fail or display error")
                .isTrue();
    }

    @DataProvider(name = "validUsers")
    public Object[][] validUsers() {
        return new Object[][]{
                {"standard_user", "secret_sauce"},
                {"problem_user", "secret_sauce"},
                {"performance_glitch_user", "secret_sauce"}
        };
    }
}
