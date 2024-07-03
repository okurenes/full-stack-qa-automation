@api
Feature: User API
  As an API consumer
  I want to interact with the ReqRes User API
  So that I can manage user resources

  @smoke
  Scenario: Retrieve list of users
    When I send a GET request to "/api/users?page=1"
    Then the response status code should be 200
    And the response should contain field "data"

  @smoke
  Scenario: Retrieve a single user by ID
    When I send a GET request to "/api/users/2"
    Then the response status code should be 200
    And the response should contain field "data.email"
    And the response should contain field "data.first_name"

  @regression
  Scenario: Request for non-existent user returns 404
    When I send a GET request to "/api/users/9999"
    Then the response status code should be 404

  @regression
  Scenario: Create a new user via POST
    When I send a POST request to "/api/users" with name "Enes Okur" and job "QA Engineer"
    Then the response status code should be 201
    And the response should contain field "id"
    And the response field "name" should equal "Enes Okur"
    And the response field "job" should equal "QA Engineer"
