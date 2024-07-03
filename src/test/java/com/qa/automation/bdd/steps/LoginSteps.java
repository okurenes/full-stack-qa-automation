package com.qa.automation.bdd.steps;

import com.qa.automation.ui.pages.InventoryPage;
import com.qa.automation.ui.pages.LoginPage;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import static org.assertj.core.api.Assertions.assertThat;

public class LoginSteps {

    private WebDriver driver;
    private LoginPage loginPage;
    private InventoryPage inventoryPage;

    @Before
    public void setUp() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new", "--no-sandbox", "--disable-dev-shm-usage");
        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
    }

    @After
    public void tearDown() {
        if (driver != null) driver.quit();
    }

    @Given("I am on the SauceDemo login page")
    public void iAmOnTheSauceDemoLoginPage() {
        loginPage = new LoginPage(driver);
        loginPage.open("https://www.saucedemo.com");
        assertThat(loginPage.isOnLoginPage()).isTrue();
    }

    @When("I enter username {string} and password {string}")
    public void iEnterUsernameAndPassword(String username, String password) {
        loginPage.enterUsername(username).enterPassword(password);
    }

    @When("I click the login button")
    public void iClickTheLoginButton() {
        inventoryPage = loginPage.clickLogin();
    }

    @When("I attempt to login with username {string} and password {string}")
    public void iAttemptToLoginWith(String username, String password) {
        loginPage.loginWithInvalidCredentials(username, password);
    }

    @Then("I should be redirected to the inventory page")
    public void iShouldBeRedirectedToTheInventoryPage() {
        assertThat(inventoryPage.isLoaded())
                .as("Should land on inventory page after successful login")
                .isTrue();
    }

    @Then("I should see the products page with heading {string}")
    public void iShouldSeeTheProductsPage(String heading) {
        assertThat(inventoryPage.getPageHeading()).isEqualTo(heading);
    }

    @Then("I should see an error message containing {string}")
    public void iShouldSeeAnErrorMessageContaining(String errorText) {
        assertThat(loginPage.isErrorDisplayed()).isTrue();
        assertThat(loginPage.getErrorMessage()).contains(errorText);
    }

    @Then("I should remain on the login page")
    public void iShouldRemainOnTheLoginPage() {
        assertThat(loginPage.isOnLoginPage()).isTrue();
    }
}
