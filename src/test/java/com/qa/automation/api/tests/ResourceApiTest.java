package com.qa.automation.api.tests;

import com.qa.automation.api.endpoints.ApiEndpoints;
import com.qa.automation.base.BaseApiTest;
import io.qameta.allure.*;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@Epic("ReqRes REST API")
@Feature("Resource Catalog")
public class ResourceApiTest extends BaseApiTest {

    @Test(groups = {"smoke", "api"})
    @Story("List all resources")
    @Severity(SeverityLevel.CRITICAL)
    public void testGetAllResources() {
        given()
        .when()
            .get(ApiEndpoints.RESOURCES)
        .then()
            .statusCode(200)
            .body("data", not(empty()))
            .body("data[0].id", notNullValue())
            .body("data[0].name", not(emptyString()))
            .body("data[0].color", matchesPattern("^#[0-9A-Fa-f]{6}$"));
    }

    @Test(groups = {"regression", "api"})
    @Story("Retrieve single resource by ID")
    @Severity(SeverityLevel.NORMAL)
    public void testGetResourceById() {
        given()
            .pathParam("id", 2)
        .when()
            .get(ApiEndpoints.RESOURCE_BY_ID)
        .then()
            .statusCode(200)
            .body("data.id", equalTo(2))
            .body("data.name", equalTo("fuchsia rose"))
            .body("data.year", equalTo(2001))
            .body("data.color", equalTo("#C74375"));
    }

    @Test(groups = {"regression", "api"})
    @Story("Non-existent resource returns 404")
    @Severity(SeverityLevel.NORMAL)
    public void testGetNonExistentResource() {
        given()
            .pathParam("id", 9999)
        .when()
            .get(ApiEndpoints.RESOURCE_BY_ID)
        .then()
            .statusCode(404);
    }

    @Test(groups = {"regression", "api"})
    @Story("Response time is within SLA")
    @Description("API response time must be under 3000ms")
    @Severity(SeverityLevel.CRITICAL)
    public void testResponseTimeIsWithinSla() {
        given()
        .when()
            .get(ApiEndpoints.RESOURCES)
        .then()
            .statusCode(200)
            .time(lessThan(3000L));
    }

    @Test(groups = {"regression", "api"})
    @Story("Resource color value is a valid hex code")
    @Severity(SeverityLevel.NORMAL)
    public void testResourceColorIsValidHex() {
        given()
        .when()
            .get(ApiEndpoints.RESOURCES)
        .then()
            .statusCode(200)
            .body("data[0].color", matchesPattern("^#[0-9A-Fa-f]{6}$"))
            .body("data[1].color", matchesPattern("^#[0-9A-Fa-f]{6}$"));
    }
}
