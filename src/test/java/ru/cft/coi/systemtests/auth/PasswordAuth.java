package ru.cft.coi.systemtests.auth;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.val;
import ru.cft.coi.systemtests.api.CoiEndpoints;
import ru.cft.coi.systemtests.dto.PasswordTokenRequest;

import static com.jayway.restassured.RestAssured.given;

@RequiredArgsConstructor
@Getter
public class PasswordAuth implements Auth {

    private final String username;
    private final String password;
    private final String realm;
    private final String clientId;

    @Override
    public String getToken() {
        val requestBody = new PasswordTokenRequest(clientId, password, username)
                .toUrlEncodedForm();

        return given()
                .contentType("application/x-www-form-urlencoded")
                .log().all()
                .body(requestBody)
                .when()
                .post(CoiEndpoints.GET_TOKEN, this.realm)
                .then()
                .assertThat().statusCode(200)
                .extract().jsonPath().getString("access_token");
    }
}
