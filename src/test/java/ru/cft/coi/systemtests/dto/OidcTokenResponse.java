package ru.cft.coi.systemtests.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class OidcTokenResponse {
    @JsonProperty("access_token")
    private String accessToken;
}
