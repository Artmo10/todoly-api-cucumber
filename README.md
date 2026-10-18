# Todo.ly API Testing – Cucumber + RestAssured

Automated API testing for the [Todo.ly REST API](http://todo.ly/ApiWiki/) built with **Java, Gradle, Cucumber and RestAssured**, following a clean architecture based on the **Factory pattern** and generic, reusable step definitions.

---

## Test flow

| # | Action | Method & Endpoint | Authentication |
|---|--------|-------------------|----------------|
| 1 | Create a new user | `POST /api/user.json` | None |
| 2 | Get authentication token | `GET /api/authentication/token.json` | Basic (new user) |
| 3 | Create a project | `POST /api/projects.json` | **Token** |
| 4 | Update the project | `PUT /api/projects/{id}.json` | **Token** |
| 5 | Delete the project | `DELETE /api/projects/{id}.json` | **Token** |
| 6 | Delete the user (cleanup) | `DELETE /api/user/0.json` | **Token** |

Each step validates the **status code**, the **absence of `ErrorMessage`** (Todo.ly returns `200 OK` even on business errors) and **key response fields** (`Email`, `Content`, `Deleted`, etc.).

---

## Tech stack

| Tool | Version | Purpose |
|------|---------|---------|
| Java | 21+ | Language |
| Gradle | 9.x (wrapper) | Build & execution |
| Cucumber | 8.0.4 | BDD – Gherkin scenarios |
| RestAssured | 6.0.1 | HTTP client & assertions |
| Jackson | 2.22.0 | JSON serialization (RestAssured / Cucumber reports) |
| JUnit Platform | 6.x | Test engine |

---

## Architecture

```
 userProjectToken.feature        (Gherkin: WHAT is tested)
            │
            ▼
 runner/TodoStepdefs             (generic steps + variable storage ${VAR})
            │
            ▼
 client/FactoryRequest           (returns the right request by HTTP method)
            │
            ▼
 RequestGet | RequestPost | RequestPut | RequestDelete
            │
            ▼
 client/RequestBase              (builds the request: auth + headers + body)
            │
            ▼
        Todo.ly API
```

- **Generic steps:** a single step `send {METHOD} request "{url}" with` covers every HTTP call, so new scenarios need only Gherkin, no new Java code.
- **Pluggable authentication:** each request carries its auth type (`NONE`, `BASIC`, `TOKEN`), resolved in one place (`RequestBase`).
- **Dynamic variables:** values from responses (token, IDs, email) are saved and reused in later steps with the `${VARIABLE}` syntax.

---

## Project structure

```
todoly-api-cucumber/
├── build.gradle                     # Dependencies + custom task "cucumberCli" (run by tags)
├── settings.gradle                  # Project name
├── gradlew / gradlew.bat            # Gradle wrapper (no local Gradle install needed)
├── gradle/wrapper/                  # Wrapper configuration
├── .gitignore
├── README.md
└── src/test/
    ├── java/
    │   ├── config/
    │   │   └── Configuration.java   # Base URL of the API
    │   │
    │   ├── client/                  # HTTP layer (Factory pattern)
    │   │   ├── IRequest.java        # Interface: send(RequestInfo) → Response
    │   │   ├── FactoryRequest.java  # Returns GET/POST/PUT/DELETE implementation
    │   │   ├── RequestBase.java     # Common spec: logging, auth (None/Basic/Token), JSON body
    │   │   ├── RequestInfo.java     # Request data: URL, body, auth type & credentials
    │   │   ├── RequestGet.java      # GET implementation
    │   │   ├── RequestPost.java     # POST implementation
    │   │   ├── RequestPut.java      # PUT implementation
    │   │   └── RequestDelete.java   # DELETE implementation
    │   │
    │   └── runner/                  # Cucumber glue code
    │       ├── TodoStepdefs.java    # Step definitions: auth, requests, assertions, variables
    │       └── Runner.java          # Hooks @Before / @After (scenario start/end logging)
    │
    └── resources/
        └── userProjectToken.feature # Scenario: user → token → project CRUD → delete user
```

---

## Available steps

| Step | Description |
|------|-------------|
| `I generate a random email in "VAR"` | Creates a unique email and saves it |
| `I don't use authentication` | Next requests are sent without auth |
| `I use basic authentication with "user" and "pass"` | Next requests use Basic Auth |
| `I use token authentication "token"` | Next requests send the `Token` header |
| `send METHOD request "url" with """body"""` | Sends any HTTP request |
| `code should be N` | Validates status code |
| `the response should not have errors` | Validates no `ErrorMessage` in body |
| `the "attr" should be "value"` | Validates a string field |
| `the "attr" should be true` | Validates a boolean field |
| `save "attr" in "VAR"` | Saves a response value as `${VAR}` |

---

## How to run

**Prerequisites:** JDK 21+ installed. No Gradle installation needed (wrapper included).

```bash
# Clone
git clone https://github.com/Artmo10/todoly-api-cucumber.git
cd todoly-api-cucumber

# Run by tag (macOS / Linux)
./gradlew cucumberCli -Psuite="@Token"

# Windows
gradlew.bat cucumberCli -Psuite="@Token"

# Default tag (@Regression)
./gradlew cucumberCli
```

From **IntelliJ IDEA**: open the `.feature` file and click ▶ next to the scenario (requires the *Cucumber for Java* plugin).

---

## Report

After running with Gradle, an HTML report is generated at:

```
report/cucumber-report.html
```
