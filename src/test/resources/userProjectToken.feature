Feature: Todo.ly - User and Project management with Token

  @Regression @Token
  Scenario: Create a user, CRUD a project using token authentication and delete the user
    # 1. Create user (no authentication)
    Given I generate a random email in "EMAIL"
    And I don't use authentication
    When send POST request "api/user.json" with
    """
    {
      "Email": "${EMAIL}",
      "FullName": "Arturo QA",
      "Password": "Pass12345"
    }
    """
    Then code should be 200
    And the response should not have errors
    And the "Email" should be "${EMAIL}"
    And the "FullName" should be "Arturo QA"

    # 2. Get token with the new user (basic authentication)
    Given I use basic authentication with "${EMAIL}" and "Pass12345"
    When send GET request "api/authentication/token.json" with
    """
    """
    Then code should be 200
    And the "UserEmail" should be "${EMAIL}"
    And save "TokenString" in "TOKEN"

    # 3. Create project (token)
    Given I use token authentication "${TOKEN}"
    When send POST request "api/projects.json" with
    """
    {
      "Content": "ProjectToken"
    }
    """
    Then code should be 200
    And the response should not have errors
    And the "Content" should be "ProjectToken"
    And save "Id" in "ID_PROJECT"

    # 4. Update project (token)
    When send PUT request "api/projects/${ID_PROJECT}.json" with
    """
    {
      "Content": "ProjectTokenUpdated"
    }
    """
    Then code should be 200
    And the response should not have errors
    And the "Content" should be "ProjectTokenUpdated"

    # 5. Delete project (token)
    When send DELETE request "api/projects/${ID_PROJECT}.json" with
    """
    """
    Then code should be 200
    And the response should not have errors
    And the "Deleted" should be true

    # 6. Delete user (token)
    When send DELETE request "api/user/0.json" with
    """
    """
    Then code should be 200
    And the response should not have errors
    And the "Email" should be "${EMAIL}"