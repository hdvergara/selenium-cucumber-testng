Feature: Shopping cart management
  As a store customer,
  I want to search for a product, add it to the cart, and remove it,
  So that I can confirm the purchase flow works end-to-end.

  Scenario: Add and remove iPhone from the cart
    Given the user is on the store page
    When the user searches for "iPhone"
    And the user adds the first search result to the cart
    And the user opens the shopping cart menu
    And the user opens the shopping cart page
    Then the shopping cart should contain product "iPhone"
    When the user removes "iPhone" from the shopping cart
    Then product "iPhone" should no longer appear in the shopping cart
