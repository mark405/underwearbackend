package org.underwearshop.underwearshop.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.underwearshop.underwearshop.dto.InfoPageContentDTO;
import org.underwearshop.underwearshop.dto.InfoPageContentUpdateDTO;
import org.underwearshop.underwearshop.service.InfoPageContentService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/info-page")
public class InfoPageContentController {
    private final InfoPageContentService infoPageContentService;

    @GetMapping
    public InfoPageContentDTO get() {
        return new InfoPageContentDTO(infoPageContentService.get());
    }

    @PutMapping("/admin")
    public InfoPageContentDTO update(@RequestBody @Valid InfoPageContentUpdateDTO dto) {
        return new InfoPageContentDTO(infoPageContentService.update(dto.getContent()));
    }
}
