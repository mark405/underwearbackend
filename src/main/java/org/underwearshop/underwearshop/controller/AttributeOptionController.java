package org.underwearshop.underwearshop.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.underwearshop.underwearshop.dto.AttributeOptionCreateDTO;
import org.underwearshop.underwearshop.dto.AttributeOptionDTO;
import org.underwearshop.underwearshop.entity.AttributeType;
import org.underwearshop.underwearshop.service.AttributeOptionService;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/attribute-options")
public class AttributeOptionController {
    private final AttributeOptionService attributeOptionService;

    @GetMapping
    public List<AttributeOptionDTO> findAll(@RequestParam AttributeType type) {
        return attributeOptionService.findAll(type).stream().map(AttributeOptionDTO::new).toList();
    }

    @PostMapping("/admin")
    @ResponseStatus(HttpStatus.CREATED)
    public AttributeOptionDTO create(@RequestBody @Valid AttributeOptionCreateDTO dto) {
        return new AttributeOptionDTO(attributeOptionService.create(dto.getType(), dto.getValue()));
    }

    @DeleteMapping("/admin/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        attributeOptionService.delete(id);
    }
}
