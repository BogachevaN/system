package ru.cft.coi.systemtests.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RealmRequest {

    @JsonProperty("apiBaseUri")
    private String apiBaseUri;

    @JsonProperty("lastEncountered")
    private String lastEncountered;

    @JsonProperty("active")
    private Boolean active;
}
