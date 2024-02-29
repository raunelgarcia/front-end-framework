Feature: Check if a news article on Marca has an image

  Scenario: Verify if a news article on Marca contains an image
    Given I am on the Marca website
    When I navigate to a news article
    Then I should be able to see if the article contains an image