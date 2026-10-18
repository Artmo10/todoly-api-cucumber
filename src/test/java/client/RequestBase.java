package client;

import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

public class RequestBase {

    public static RequestSpecification spec(RequestInfo info) {
        RequestSpecification request = given().log().all();

        switch (info.getAuthType()) {
            case BASIC -> request.auth().preemptive().basic(info.getUser(), info.getPassword());
            case TOKEN -> request.header("Token", info.getToken());
            default -> { }
        }

        if (info.getBody() != null && !info.getBody().isBlank()) {
            request.contentType(io.restassured.http.ContentType.JSON).body(info.getBody());
        }
        return request;
    }
}