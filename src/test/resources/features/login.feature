@ui @smoke
Feature: User Authentication
  As a registered user
  I want to log into SauceDemo
  So that I can access the product catalog

  Background:
    Given I am on the SauceDemo login page

  @smoke
  Scenario: Successful login with valid credentials
    When I enter username "standard_user" and password "secret_sauce"
    And I click the login button
    Then I should be redirected to the inventory page
    And I should see the products page with heading "Products"

  @regression
  Scenario: Login fails with incorrect password
    When I attempt to login with username "standard_user" and password "wrong_password"
    Then I should see an error message containing "Username and password do not match"
    And I should remain on the login page

  @regression
  Scenario: Login fails with empty credentials
    When I attempt to login with username "" and password ""
    Then I should see an error message containing "Username is required"
    And I should remain on the login page

  @regression
  Scenario: Locked out user cannot access the platform
    When I attempt to login with username "locked_out_user" and password "secret_sauce"
    Then I should see an error message containing "locked out"
    And I should remain on the login page

  @regression
  Scenario Outline: Multiple user types can log in successfully
    When I enter username "<username>" and password "<password>"
    And I click the login button
    Then I should be redirected to the inventory page

    Examples:
      | username               | password     |
      | standard_user          | secret_sauce |
      | problem_user           | secret_sauce |
      | performance_glitch_user| secret_sauce |
