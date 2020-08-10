package ru.cft.coi.systemtests.api;

import com.jayway.restassured.response.Response;

import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.http.ContentType.JSON;

public class RestApiCoordinator {

    private RestApiCoordinator() {
    }

    public static Response getIncidents(String accessToken) {
        return given()
                .log().all()
                .headers("Authorization", "Bearer " + accessToken)
                .contentType(JSON)
                .when()
                .get(CoiEndpoints.GET_INCIDENTS);
    }

    public static Response getIncident(String accessToken, int incidentId) {
        return given()
                .log().all()
                .headers("Authorization", "Bearer " + accessToken)
                .contentType(JSON)
                .when()
                .get(CoiEndpoints.GET_INCIDENT, incidentId);
    }

    public static Response createIncident(String accessToken, String body) {
        return given()
                .log().all()
                .headers("Authorization", "Bearer " + accessToken)
                .contentType(JSON)
                .body(body)
                .when()
                .post(CoiEndpoints.CREATE_INCIDENT);
    }

    public static Response updateIncident(String accessToken, int incidentId, String body) {
        return given()
                .log().all()
                .headers("Authorization", "Bearer " + accessToken)
                .contentType(JSON)
                .body(body)
                .when()
                .put(CoiEndpoints.UPDATE_INCIDENT, incidentId);
    }

    public static Response sendIncident(String accessToken, int incidentId) {
        return given()
                .log().all()
                .headers("Authorization", "Bearer " + accessToken)
                .contentType(JSON)
                .when()
                .post(CoiEndpoints.SEND_INCIDENT, incidentId);
    }

    public static Response getMessagesByIncidentId(String accessToken, int incidentId) {
        return given()
                .log().all()
                .headers("Authorization", "Bearer " + accessToken)
                .contentType(JSON)
                .when()
                .get(CoiEndpoints.GET_MESSAGES_BY_INCIDENT_ID, incidentId);
    }
}
