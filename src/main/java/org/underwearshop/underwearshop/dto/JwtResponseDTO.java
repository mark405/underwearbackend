package org.underwearshop.underwearshop.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record JwtResponseDTO(
        String jwt
) { }
