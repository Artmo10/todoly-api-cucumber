package client;

import io.restassured.response.Response;

public class RequestGet implements IRequest {
    @Override
    public Response send(RequestInfo info) {
        return RequestBase.spec(info)
                .when()
                .get(info.getUrl());
    }
}