package org.underwearshop.underwearshop.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.underwearshop.underwearshop.dto.ProductCreateDTO;
import org.underwearshop.underwearshop.dto.ProductDTO;
import org.underwearshop.underwearshop.dto.ProductUpdateDTO;
import org.underwearshop.underwearshop.service.ProductService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/product")
public class ProductController {
    private final ProductService productService;

    @GetMapping
    public Page<ProductDTO> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return productService.findAll(page, size).map(ProductDTO::new);
    }

    @GetMapping("/{id}")
    public ProductDTO findOne(@PathVariable Long id) {
        return productService.findOne(id).map(ProductDTO::new).orElseThrow();
    }

    @PostMapping(value = "/admin/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ProductDTO create(
            @RequestPart("data") @Valid ProductCreateDTO dto,
            @RequestPart(value = "file", required = false) MultipartFile file
    ) {
        return new ProductDTO(productService.create(dto, file));
    }

    @PutMapping(value = "/admin/update/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ProductDTO update(
            @PathVariable Long id,
            @RequestPart("data") @Valid ProductUpdateDTO dto,
            @RequestPart(value = "file", required = false) MultipartFile file
    ) {
        return new ProductDTO(productService.update(id, dto, file).orElseThrow());
    }
}
