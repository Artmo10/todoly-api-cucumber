package runner;

import client.FactoryRequest;
import client.RequestInfo;
import config.Configuration;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;

public class TodoStepdefs {

    private Response response;
    private final Map<String, String> variables = new HashMap<>();

    private RequestInfo.AuthType authType = RequestInfo.AuthType.NONE;
    private String user;
    private String password;
    private String token;

    @Given("I generate a random email in {string}")
    public void generateRandomEmail(String variable) {
        variables.put(variable, "qa" + System.currentTimeMillis() + "@arturo.com");
        System.out.println("Variable " + variable + " = " + variables.get(variable));
    }

    @Given("I don't use authentication")
    public void noAuthentication() {
        authType = RequestInfo.AuthType.NONE;
    }

    @Given("I use basic authentication with {string} and {string}")
    public void basicAuthentication(String user, String password) {
        authType = RequestInfo.AuthType.BASIC;
        this.user = replaceVar(user);
        this.password = replaceVar(password);
    }

    @Given("I use token authentication {string}")
    public void tokenAuthentication(String token) {
        authType = RequestInfo.AuthType.TOKEN;
        this.token = replaceVar(token);
    }

    @When("send {} request {string} with")
    public void sendRequestWith(String method, String url, String body) {
        RequestInfo info = new RequestInfo(Configuration.BASE_URL + replaceVar(url), replaceVar(body));
        if (authType == RequestInfo.AuthType.BASIC) info.basic(user, password);
        if (authType == RequestInfo.AuthType.TOKEN) info.token(token);

        response = FactoryRequest.make(method).send(info);
        response.then().log().all();
    }

    @Then("code should be {int}")
    public void codeShouldBe(int expectedCode) {
        response.then().statusCode(expectedCode);
    }

    @And("the {string} should be {string}")
    public void attributeShouldBe(String attribute, String value) {
        response.then().body(attribute, equalTo(replaceVar(value)));
    }

    @And("the {string} should be true")
    public void attributeShouldBeTrue(String attribute) {
        response.then().body(attribute, equalTo(true));
    }

    @And("the response should not have errors")
    public void responseShouldNotHaveErrors() {
        response.then().body("ErrorMessage", nullValue());
    }

    @And("save {string} in {string}")
    public void saveAttribute(String attribute, String variable) {
        Object value = response.then().extract().path(attribute);
        variables.put(variable, String.valueOf(value));
        System.out.println("Variable " + variable + " = " + value);
    }

    private String replaceVar(String value) {
        if (value == null) return null;
        for (Map.Entry<String, String> entry : variables.entrySet()) {
            value = value.replace("${" + entry.getKey() + "}", entry.getValue());
        }
        return value;
    }
}