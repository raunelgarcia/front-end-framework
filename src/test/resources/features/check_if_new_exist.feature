Feature: Check if a new exist in Marca

  Scenario: Verify if a new exits
    Given I am on the Marca website
    When I look for a notice
    Then I should be able to see the notice