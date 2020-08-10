package ru.cft.coi.systemtests.api;

public class CoiEndpoints {

    private static final String API_GATEWAY = "http://coi-api-gateway-release.front.ftc.ru";

    public static final String GET_TOKEN = API_GATEWAY + "/auth/realms/{realm}/protocol/openid-connect/token";

    public static final String GET_INCIDENTS = API_GATEWAY + "/coordinator/incident";
    public static final String GET_INCIDENT = API_GATEWAY + "/coordinator/incident/{id}";
    public static final String CREATE_INCIDENT = API_GATEWAY + "/coordinator/incident";
    public static final String UPDATE_INCIDENT = API_GATEWAY + "/coordinator/incident/{id}";
    public static final String SEND_INCIDENT = API_GATEWAY + "/coordinator/incident/{id}/send";
    public static final String ASSIGN_INCIDENT = API_GATEWAY + "/coordinator/incident/{id}/assign";
    public static final String GET_MESSAGES_BY_INCIDENT_ID = API_GATEWAY + "/coordinator/incident/{id}/messages";
    public static final String GET_INCIDENT_DRAFT = API_GATEWAY + "/coordinator/incident/{id}/draft";
    public static final String CREATE_INCIDENT_DRAFT = API_GATEWAY + "/coordinator/incident/{id}/draft";
    public static final String UPDATE_INCIDENT_DRAFT = API_GATEWAY + "/coordinator/incident/{id}/draft";
    public static final String GET_INCIDENT_COMMENTS = API_GATEWAY + "/coordinator/incident/{id}/comment";
    public static final String CREATE_INCIDENT_COMMENT = API_GATEWAY + "/coordinator/incident/{id}/comment";

    public static final String GET_FEEDS = API_GATEWAY + "/exchange/feed";
    public static final String GET_FEED_DATA = API_GATEWAY + "/exchange/feed/{id}/data";
    public static final String ACTIVATE_REALM_AGENT = API_GATEWAY + "/exchange/realm-agent/{id}/activate";
    public static final String DEACTIVATE_REALM_AGENT = API_GATEWAY + "/exchange/realm-agent/{id}/deactivate";

    private CoiEndpoints() {
    }
}
