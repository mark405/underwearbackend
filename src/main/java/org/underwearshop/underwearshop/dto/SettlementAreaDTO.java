package org.underwearshop.underwearshop.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SettlementAreaDTO(
        @JsonProperty("Ref")
        String ref,

        @JsonProperty("Description")
        String description
) {}