package ru.cft.coi.systemtests.auth;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.val;
import ru.cft.coi.systemtests.api.FincertEndpoints;

import java.util.HashMap;

import static com.jayway.restassured.RestAssured.given;

@RequiredArgsConstructor
@Getter
public class FincertAuth implements Auth {

    private final String login;
    private final String password;

    @Override
    public String getToken() {
        val map = new HashMap<Object, Object>();
        map.put("login", login);
        map.put("password", password);

        return given()
                .contentType("application/json")
                .log().all()
                .header("Host", "zoe-api.fincert.cbr.ru")
                .body(map)
                .when()
                .post(FincertEndpoints.LOGIN)
                .body().asString();
    }
}
