package org.underwearshop.underwearshop.service;

import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.underwearshop.underwearshop.dto.NovaPostResponseDTO;
import org.underwearshop.underwearshop.dto.SettlementAreaDTO;
import org.underwearshop.underwearshop.dto.SettlementDTO;
import org.underwearshop.underwearshop.dto.WareHouseDTO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class NovaPostClientService {

    private final RestClient restClient;

    @Value("${novapost.api-key}")
    private String apiKey;
    @Value("${novapost.base-url}")
    private String baseUrl;

    public List<SettlementAreaDTO> getSettlementAreas() {
        NovaPostResponseDTO<SettlementAreaDTO> response =
                restClient.post()
                        .uri(baseUrl + "/getSettlementAreas")
                        .body(Map.of(
                                "apiKey", apiKey,
                                "modelName", "AddressGeneral",
                                "calledMethod", "getSettlementAreas",
                                "methodProperties", Map.of()
                        ))
                        .retrieve()
                        .body(new ParameterizedTypeReference<>() {
                        });

        if (response == null || response.data() == null) {
            return List.of();
        }

        return response.data();
    }

    public Page<SettlementDTO> getSettlements(String area, @Nullable String search, int page) {
        Map<String, Object> methodProperties = new HashMap<>();
        methodProperties.put("Page", page);
        methodProperties.put("AreaRef", area);

        if (search != null && !search.isBlank()) {
            methodProperties.put("FindByString", search);
        }
        NovaPostResponseDTO<SettlementDTO> response =
                restClient.post()
                        .uri(baseUrl + "/getSettlements")
                        .body(Map.of(
                                "apiKey", apiKey,
                                "modelName", "AddressGeneral",
                                "calledMethod", "getSettlements",
                                "methodProperties", methodProperties
                        ))
                        .retrieve()
                        .body(new ParameterizedTypeReference<>() {
                        });

        if (response == null || response.data() == null) {
            return new PageImpl<>(List.of());
        }

        return new PageImpl<>(response.data());
    }

    public Page<WareHouseDTO> getWarehouses(String city, @Nullable String search, int page, int limit) {
        Map<String, Object> methodProperties = new HashMap<>();
        methodProperties.put("Page", page);
        methodProperties.put("Limit", limit);
        methodProperties.put("CityName", city);
        if (search != null && !search.isBlank()) {
            methodProperties.put("FindByString", search);
        }

        NovaPostResponseDTO<WareHouseDTO> response =
                restClient.post()
                        .uri(baseUrl + "/getWarehouses")
                        .body(Map.of(
                                "apiKey", apiKey,
                                "modelName", "AddressGeneral",
                                "calledMethod", "getWarehouses",
                                "methodProperties", methodProperties
                        ))
                        .retrieve()
                        .body(new ParameterizedTypeReference<>() {
                        });

        if (response == null || response.data() == null) {
            return new PageImpl<>(List.of());
        }

        return new PageImpl<>(response.data());
    }
}