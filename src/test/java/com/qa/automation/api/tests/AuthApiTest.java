package com.qa.automation.api.tests;

import com.qa.automation.api.endpoints.ApiEndpoints;
import com.qa.automation.base.BaseApiTest;
import io.qameta.allure.*;
import org.testng.annotations.Test;

import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@Epic("ReqRes REST API")
@Feature("Authentication")
public class AuthApiTest extends BaseApiTest {

    @Test(groups = {"smoke", "api"})
    @Story("Successful registration")
    @Description("POST /api/register returns token with valid credentials")
    @Severity(SeverityLevel.BLOCKER)
    public void testSuccessfulRegistration() {
        Map<String, String> body = Map.of(
            "email", "eve.holt@reqres.in",
            "password", "pistol"
        );

        given()
            .body(body)
        .when()
            .post(ApiEndpoints.REGISTER)
        .then()
            .statusCode(200)
            .body("id", notNullValue())
            .body("token", not(emptyString()));
    }

    @Test(groups = {"regression", "api"})
    @Story("Registration fails without password")
    @Severity(SeverityLevel.CRITICAL)
    public void testRegistrationWithoutPassword() {
        Map<String, String> body = Map.of("email", "sydney@fife");

        given()
            .body(body)
        .when()
            .post(ApiEndpoints.REGISTER)
        .then()
            .statusCode(400)
            .body("error", equalTo("Missing password"));
    }

    @Test(groups = {"smoke", "api"})
    @Story("Successful login returns token")
    @Severity(SeverityLevel.BLOCKER)
    public void testSuccessfulLogin() {
        Map<String, String> body = Map.of(
            "email", "eve.holt@reqres.in",
            "password", "cityslicka"
        );

        given()
            .body(body)
        .when()
            .post(ApiEndpoints.LOGIN)
        .then()
            .statusCode(200)
            .body("token", not(emptyString()));
    }

    @Test(groups = {"regression", "api"})
    @Story("Login fails with missing password")
    @Severity(SeverityLevel.CRITICAL)
    public void testLoginWithoutPassword() {
        Map<String, String> body = Map.of("email", "peter@klaven");

        given()
            .body(body)
        .when()
            .post(ApiEndpoints.LOGIN)
        .then()
            .statusCode(400)
            .body("error", equalTo("Missing password"));
    }
}
