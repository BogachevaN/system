package ru.cft.coi.systemtests.dto;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ClientCredentialsTokenRequest {

    private final String clientId;
    private final String clientSecret;

    public String toUrlEncodedForm() {
        return new StringBuilder()
                .append("client_id=").append(clientId)
                .append("&grant_type=client_credentials")
                .append("&client_secret=").append(clientSecret)
                .toString();
    }
}
