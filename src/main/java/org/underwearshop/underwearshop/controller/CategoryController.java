package org.underwearshop.underwearshop.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.underwearshop.underwearshop.dto.CategoryCreateDTO;
import org.underwearshop.underwearshop.dto.CategoryDTO;
import org.underwearshop.underwearshop.dto.CategoryUpdateDTO;
import org.underwearshop.underwearshop.service.CategoryService;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/category")
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping
    public List<CategoryDTO> findAll(@RequestParam(required = false) Long parentId) {
        return categoryService.findAll(parentId).stream().map(CategoryDTO::new).toList();
    }

    @GetMapping("/{id}")
    public CategoryDTO findOne(@PathVariable Long id) {
        return categoryService.findOne(id).map(CategoryDTO::new).orElseThrow();
    }


    @PostMapping(value = "/admin/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDTO create(
            @RequestPart("data") @Valid CategoryCreateDTO dto,
            @RequestPart(value = "file", required = false) MultipartFile file
    ) {
        return new CategoryDTO(categoryService.create(dto, file));
    }

    @PutMapping(value = "/admin/update/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public CategoryDTO update(
            @PathVariable Long id,
            @RequestPart("data") @Valid CategoryUpdateDTO dto,
            @RequestPart(value = "file", required = false) MultipartFile file
    ) {
        return new CategoryDTO(categoryService.update(id, dto, file).orElseThrow());
    }
}
