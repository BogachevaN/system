package ru.cft.coi.systemtests.dto;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PasswordTokenRequest {

    private final String clientId;
    private final String password;
    private final String username;

    public String toUrlEncodedForm() {
        return new StringBuilder()
                .append("client_id=").append(clientId)
                .append("&grant_type=password")
                .append("&password=").append(password)
                .append("&username=").append(username)
                .toString();
    }
}
