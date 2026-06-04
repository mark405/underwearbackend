package org.underwearshop.underwearshop.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.underwearshop.underwearshop.dto.FilterCreateDTO;
import org.underwearshop.underwearshop.dto.FilterDTO;
import org.underwearshop.underwearshop.dto.FilterUpdateDTO;
import org.underwearshop.underwearshop.service.FilterService;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/filter")
public class FilterController {
    private final FilterService filterService;

    @GetMapping
    public List<FilterDTO> findAll() {
        return filterService.findAll().stream().map(FilterDTO::new).toList();
    }

    @GetMapping("/{id}")
    public FilterDTO findOne(@PathVariable Long id) {
        return filterService.findOne(id).map(FilterDTO::new).orElseThrow();
    }

    @PostMapping(value = "/admin/create")
    @ResponseStatus(HttpStatus.CREATED)
    public FilterDTO create(@RequestPart("data") @Valid FilterCreateDTO dto) {
        return new FilterDTO(filterService.create(dto));
    }

    @PutMapping(value = "/admin/update/{id}")
    public FilterDTO update(@PathVariable Long id, @RequestPart("data") @Valid FilterUpdateDTO dto) {
        return new FilterDTO(filterService.update(id, dto).orElseThrow());
    }
}
