@ui @regression
Feature: Checkout Flow
  As a logged-in user
  I want to complete the checkout process
  So that I can purchase products

  Background:
    Given I am on the SauceDemo login page
    When I enter username "standard_user" and password "secret_sauce"
    And I click the login button

  @smoke
  Scenario: User can proceed to checkout from cart
    When I add a product to the cart
    And I navigate to the cart
    And I click the checkout button
    Then I should be on checkout step one

  @regression
  Scenario: Checkout fails when first name is missing
    When I add a product to the cart
    And I navigate to the cart
    And I click the checkout button
    And I fill checkout info with first name "" last name "Yilmaz" postal code "34000"
    And I click continue on checkout
    Then I should see a checkout error containing "First Name is required"

  @regression
  Scenario: Complete order shows confirmation message
    When I add a product to the cart
    And I navigate to the cart
    And I click the checkout button
    And I fill checkout info with first name "Ali" last name "Yilmaz" postal code "34000"
    And I click continue on checkout
    And I click finish on checkout
    Then I should see the order confirmation message
