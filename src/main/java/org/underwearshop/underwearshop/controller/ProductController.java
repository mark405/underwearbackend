package org.underwearshop.underwearshop.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.underwearshop.underwearshop.dto.*;
import org.underwearshop.underwearshop.service.ProductService;

import java.math.BigDecimal;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/product")
public class ProductController {
    private final ProductService productService;

    @GetMapping
    public Page<ProductShortDTO> findAll(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String circumference,
            @RequestParam(required = false) String cup,
            @RequestParam(required = false) String color,
            @RequestParam(required = false) String material,
            @RequestParam(required = false) String features,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Boolean inStock,
            @RequestParam(required = false) String bustModel,
            @RequestParam(required = false) String sizeFilter,
            @RequestParam(required = false) String briefStyle,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        ProductFilter filter = new ProductFilter(
                name,
                circumference,
                cup,
                color,
                material,
                features,
                minPrice,
                maxPrice,
                inStock,
                bustModel,
                sizeFilter,
                briefStyle,
                categoryId
        );

        return productService.findAll(filter, page, size)
                .map(ProductShortDTO::new);
    }

    @GetMapping("/{id}")
    public ProductDTO findOne(@PathVariable Long id) {
        return productService.findOne(id).map(ProductDTO::new).orElseThrow();
    }

    @PostMapping(
            value = "/admin/create",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    @ResponseStatus(HttpStatus.CREATED)
    public ProductDTO create(
            @RequestPart("data") @Valid ProductCreateDTO dto,
            @RequestPart(value = "mainImage", required = false) MultipartFile mainImage,
            @RequestPart(value = "images", required = false) List<MultipartFile> images
    ) {
        return new ProductDTO(productService.create(dto, mainImage, images));
    }

    @PutMapping(value = "/admin/update/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ProductDTO update(
            @PathVariable Long id,
            @RequestPart("data") @Valid ProductUpdateDTO dto,
            @RequestPart(value = "mainImage", required = false) MultipartFile mainImage,
            @RequestPart(value = "images", required = false) List<MultipartFile> images
    ) {
        return new ProductDTO(productService.update(id, dto, mainImage, images).orElseThrow());
    }
}
