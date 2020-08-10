package ru.cft.coi.systemtests.auth;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.val;
import ru.cft.coi.systemtests.api.CoiEndpoints;
import ru.cft.coi.systemtests.dto.ClientCredentialsTokenRequest;

import static com.jayway.restassured.RestAssured.given;

@RequiredArgsConstructor
@Getter
public class ClientCredentialsAuth implements Auth {

    private final String clientId;
    private final String clientSecret;
    private final String realm;

    @Override
    public String getToken() {
        val requestBody = new ClientCredentialsTokenRequest(clientId, clientSecret)
                .toUrlEncodedForm();

        return given()
                .contentType("application/x-www-form-urlencoded")
                .body(requestBody)
                .when()
                .post(CoiEndpoints.GET_TOKEN, realm)
                .then()
                .assertThat().statusCode(200)
                .extract().jsonPath().getString("access_token");
    }
}
