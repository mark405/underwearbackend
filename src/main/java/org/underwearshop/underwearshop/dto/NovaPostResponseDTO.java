package org.underwearshop.underwearshop.dto;

import java.util.List;

public record NovaPostResponseDTO<T>(
        boolean success,
        List<T> data
) {}