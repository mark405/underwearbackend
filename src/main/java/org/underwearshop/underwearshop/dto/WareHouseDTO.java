package org.underwearshop.underwearshop.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record WareHouseDTO(
        @JsonProperty("SiteKey")
        String SiteKey,

        @JsonProperty("Description")
        String description,

        @JsonProperty("SettlementDescription")
        String SettlementDescription,

        @JsonProperty("SettlementAreaDescription")
        String SettlementAreaDescription
) {}