package org.underwearshop.underwearshop.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.underwearshop.underwearshop.entity.AttributeOption;
import org.underwearshop.underwearshop.entity.AttributeType;
import org.underwearshop.underwearshop.repository.AttributeOptionRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AttributeOptionService {
    private final AttributeOptionRepository attributeOptionRepository;

    @Transactional(readOnly = true)
    public List<AttributeOption> findAll(AttributeType type) {
        return attributeOptionRepository.findAllByTypeOrderBySortOrderAscIdAsc(type);
    }

    /**
     * Creates a new reusable option, or returns the existing one if this (type, value) pair already
     * exists (case-insensitive) - idempotent, so the "save for reuse" action never creates duplicates.
     */
    @Transactional
    public AttributeOption create(AttributeType type, String value) {
        String trimmed = value.trim();

        if (trimmed.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Значення не може бути порожнім");
        }

        return attributeOptionRepository.findByTypeAndValueIgnoreCase(type, trimmed)
                .orElseGet(() -> attributeOptionRepository.save(
                        AttributeOption.builder()
                                .type(type)
                                .value(trimmed)
                                .label(trimmed)
                                .sortOrder(null)
                                .build()
                ));
    }

    @Transactional
    public void delete(Long id) {
        if (!attributeOptionRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Значення не знайдено");
        }

        attributeOptionRepository.deleteById(id);
    }
}
