package ru.cft.coi.systemtests.api;

import com.jayway.restassured.response.Response;

import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.http.ContentType.JSON;

public class RestApiFincert {

    private static final String HOST = "zoe-api.fincert.cbr.ru";

    private RestApiFincert() {
    }

    public static Response createRequest(String accessToken, String body) {
        return given()
                .log().all()
                .headers("Authorization", "Bearer " + accessToken)
                .header("Host", HOST)
                .contentType(JSON)
                .body(body)
                .when()
                .post(FincertEndpoints.CREATE_REQUEST);
    }

    public static Response getApiResponse(String token, String requestId) {
        return given()
                .log().all()
                .headers("Authorization", "Bearer " + token)
                .header("Host", HOST)
                .contentType(JSON)
                .when()
                .get(FincertEndpoints.GET_API_RESPONSE, requestId);
    }
}
