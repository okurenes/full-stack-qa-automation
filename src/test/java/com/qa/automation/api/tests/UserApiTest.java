package com.qa.automation.api.tests;

import com.qa.automation.api.endpoints.ApiEndpoints;
import com.qa.automation.base.BaseApiTest;
import io.qameta.allure.*;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.response.Response;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;

@Epic("ReqRes REST API")
@Feature("User Management")
public class UserApiTest extends BaseApiTest {

    @Test(groups = {"smoke", "api"})
    @Story("Retrieve paginated user list")
    @Description("GET /api/users returns 200 with paginated user data")
    @Severity(SeverityLevel.CRITICAL)
    public void testGetUsersReturns200() {
        given()
            .queryParam("page", 1)
        .when()
            .get(ApiEndpoints.USERS)
        .then()
            .statusCode(200)
            .body("page", equalTo(1))
            .body("data", not(empty()))
            .body("data.size()", greaterThan(0));
    }

    @Test(groups = {"regression", "api"})
    @Story("Retrieve single user")
    @Severity(SeverityLevel.CRITICAL)
    public void testGetUserById() {
        Response response = given()
            .pathParam("id", 2)
        .when()
            .get(ApiEndpoints.USER_BY_ID)
        .then()
            .statusCode(200)
            .body("data.id", equalTo(2))
            .body("data.email", not(emptyString()))
            .extract().response();

        String email = response.jsonPath().getString("data.email");
        assertThat(email).contains("@");
        log.info("Fetched user: {}", email);
    }

    @Test(groups = {"regression", "api"})
    @Story("User not found returns 404")
    @Severity(SeverityLevel.NORMAL)
    public void testGetNonExistentUserReturns404() {
        given()
            .pathParam("id", 9999)
        .when()
            .get(ApiEndpoints.USER_BY_ID)
        .then()
            .statusCode(404)
            .body(equalTo("{}"));
    }

    @Test(groups = {"regression", "api"})
    @Story("Create a new user")
    @Severity(SeverityLevel.BLOCKER)
    public void testCreateUser() {
        Map<String, String> body = Map.of(
            "name", "Ali Yilmaz",
            "job", "QA Engineer"
        );

        Response response = given()
            .body(body)
        .when()
            .post(ApiEndpoints.USERS)
        .then()
            .statusCode(201)
            .body("name", equalTo("Ali Yilmaz"))
            .body("job", equalTo("QA Engineer"))
            .body("id", not(emptyString()))
            .body("createdAt", not(emptyString()))
            .extract().response();

        String userId = response.jsonPath().getString("id");
        assertThat(userId).isNotBlank();
        log.info("Created user with id: {}", userId);
    }

    @Test(groups = {"regression", "api"})
    @Story("Update user with PUT")
    @Severity(SeverityLevel.CRITICAL)
    public void testUpdateUserWithPut() {
        Map<String, String> body = Map.of(
            "name", "Enes Okur",
            "job", "Senior QA Engineer"
        );

        given()
            .body(body)
            .pathParam("id", 2)
        .when()
            .put(ApiEndpoints.USER_BY_ID)
        .then()
            .statusCode(200)
            .body("name", equalTo("Enes Okur"))
            .body("job", equalTo("Senior QA Engineer"))
            .body("updatedAt", not(emptyString()));
    }

    @Test(groups = {"regression", "api"})
    @Story("Partial update user with PATCH")
    @Severity(SeverityLevel.NORMAL)
    public void testUpdateUserWithPatch() {
        Map<String, String> body = Map.of("job", "Lead QA Automation Engineer");

        given()
            .body(body)
            .pathParam("id", 2)
        .when()
            .patch(ApiEndpoints.USER_BY_ID)
        .then()
            .statusCode(200)
            .body("job", equalTo("Lead QA Automation Engineer"))
            .body("updatedAt", not(emptyString()));
    }

    @Test(groups = {"regression", "api"})
    @Story("Delete user returns 204")
    @Severity(SeverityLevel.CRITICAL)
    public void testDeleteUser() {
        given()
            .pathParam("id", 2)
        .when()
            .delete(ApiEndpoints.USER_BY_ID)
        .then()
            .statusCode(204);
    }

    @Test(dataProvider = "pageNumbers", groups = {"regression", "api"})
    @Story("Paginated user list returns correct page")
    @Severity(SeverityLevel.NORMAL)
    public void testUserPagination(int page, int expectedPerPage) {
        given()
            .queryParam("page", page)
        .when()
            .get(ApiEndpoints.USERS)
        .then()
            .statusCode(200)
            .body("page", equalTo(page))
            .body("per_page", equalTo(expectedPerPage));
    }

    @Test(groups = {"regression", "api"})
    @Story("Second page of users is accessible")
    @Severity(SeverityLevel.NORMAL)
    public void testGetUsersOnPage2() {
        given()
            .queryParam("page", 2)
        .when()
            .get(ApiEndpoints.USERS)
        .then()
            .statusCode(200)
            .body("page", equalTo(2))
            .body("data", not(empty()))
            .body("total_pages", greaterThanOrEqualTo(2));
    }

    @DataProvider(name = "pageNumbers")
    public Object[][] pageNumbers() {
        return new Object[][]{{1, 6}, {2, 6}};
    }

    @Test(groups = {"regression", "api"})
    @Story("Users list matches JSON schema")
    @Severity(SeverityLevel.CRITICAL)
    public void testUsersListMatchesSchema() {
        given()
            .queryParam("page", 1)
        .when()
            .get(ApiEndpoints.USERS)
        .then()
            .statusCode(200)
            .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("schemas/users-list-schema.json"));
    }

    @Test(groups = {"regression", "api"})
    @Story("User avatar URL is a non-empty string")
    @Severity(SeverityLevel.MINOR)
    public void testUserAvatarIsNotEmpty() {
        given()
            .pathParam("id", 1)
        .when()
            .get(ApiEndpoints.USER_BY_ID)
        .then()
            .statusCode(200)
            .body("data.avatar", not(emptyString()))
            .body("data.avatar", containsString("https://"));
    }

    @Test(groups = {"regression", "api"})
    @Story("User response matches JSON schema")
    @Severity(SeverityLevel.CRITICAL)
    public void testUserResponseSchema() {
        given()
            .pathParam("id", 2)
        .when()
            .get(ApiEndpoints.USER_BY_ID)
        .then()
            .statusCode(200)
            .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("schemas/user-schema.json"));
    }
}
