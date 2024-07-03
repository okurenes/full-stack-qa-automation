package com.qa.automation.bdd.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;

import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

public class UserApiSteps {

    private Response response;
    private final String BASE_URI = "https://reqres.in";

    @Given("the API base URL is {string}")
    public void theApiBaseUrlIs(String url) {
        // base URL is set per-test in this context
    }

    @When("I send a GET request to {string}")
    public void iSendAGetRequestTo(String endpoint) {
        response = given()
                .baseUri(BASE_URI)
                .contentType("application/json")
                .when()
                .get(endpoint);
    }

    @When("I send a POST request to {string} with name {string} and job {string}")
    public void iSendAPostRequestWithBody(String endpoint, String name, String job) {
        response = given()
                .baseUri(BASE_URI)
                .contentType("application/json")
                .body(Map.of("name", name, "job", job))
                .when()
                .post(endpoint);
    }

    @Then("the response status code should be {int}")
    public void theResponseStatusCodeShouldBe(int statusCode) {
        assertThat(response.getStatusCode())
                .as("Expected status code %d", statusCode)
                .isEqualTo(statusCode);
    }

    @Then("the response should contain field {string}")
    public void theResponseShouldContainField(String field) {
        assertThat(response.jsonPath().getString(field))
                .as("Response should contain field: %s", field)
                .isNotNull();
    }

    @Then("the response field {string} should equal {string}")
    public void theResponseFieldShouldEqual(String field, String value) {
        assertThat(response.jsonPath().getString(field)).isEqualTo(value);
    }
}
