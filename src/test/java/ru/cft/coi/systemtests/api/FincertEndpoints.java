package ru.cft.coi.systemtests.api;

public class FincertEndpoints {

    private static final String BASE_URI = "http://stunnel.front.ftc.ru";

    public static final String LOGIN = BASE_URI + "/api/v1/account/login";
    public static final String CREATE_REQUEST = BASE_URI + "/api/v1/requests";
    public static final String GET_API_RESPONSE = BASE_URI + "/api/v1/apiRequests/{id}";

    private FincertEndpoints() {
    }
}
