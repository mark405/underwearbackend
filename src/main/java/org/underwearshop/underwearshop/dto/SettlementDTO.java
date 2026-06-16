package org.underwearshop.underwearshop.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SettlementDTO(
        @JsonProperty("Ref")
        String ref,

        @JsonProperty("Description")
        String description
) {}