package org.underwearshop.underwearshop.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.underwearshop.underwearshop.dto.ProductCreateDTO;
import org.underwearshop.underwearshop.dto.ProductFilter;
import org.underwearshop.underwearshop.dto.ProductUpdateDTO;
import org.underwearshop.underwearshop.dto.ProductVariantRequestDTO;
import org.underwearshop.underwearshop.entity.Category;
import org.underwearshop.underwearshop.entity.Product;
import org.underwearshop.underwearshop.entity.ProductImage;
import org.underwearshop.underwearshop.entity.ProductVariant;
import org.underwearshop.underwearshop.repository.CategoryRepository;
import org.underwearshop.underwearshop.repository.ProductImageRepository;
import org.underwearshop.underwearshop.repository.ProductRepository;
import org.underwearshop.underwearshop.repository.ProductSpecifications;
import org.underwearshop.underwearshop.repository.ProductVariantRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final ProductVariantRepository productVariantRepository;
    private final CategoryRepository categoryRepository;
    private final FileStorageService fileStorageService;

    @Transactional(readOnly = true)
    public Page<Product> findAll(ProductFilter filter, int page, int size) {
        return productRepository.findAll(
                ProductSpecifications.filter(filter),
                PageRequest.of(
                        page,
                        size,
                        Sort.by(Sort.Direction.DESC, "id")
                )
        );
    }

    @Transactional(readOnly = true)
    public Optional<Product> findOne(Long id) {
        return productRepository.findById(id);
    }

    @Transactional
    public Product create(
            ProductCreateDTO dto,
            MultipartFile mainImage,
            List<MultipartFile> images
    ) {
        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow();

        validateNoDuplicateVariants(dto.getVariants());

        Product product = Product.builder()
                .name(dto.getName())
                .price(dto.getPrice())
                .material(dto.getMaterial())
                .features(dto.getFeatures())
                .circumference(dto.getCircumference())
                .cup(dto.getCup())
                .category(category)
                .deleted(false)
                .build();

        if (mainImage != null && !mainImage.isEmpty()) {
            product.setImage(fileStorageService.save(mainImage));
        }

        Product savedProduct = productRepository.save(product);

        List<ProductVariant> variants = dto.getVariants().stream()
                .map(v -> ProductVariant.builder()
                        .product(savedProduct)
                        .size(v.getSize())
                        .color(v.getColor())
                        .quantity(v.getQuantity())
                        .inStock(v.getQuantity() > 0)
                        .active(true)
                        .build())
                .toList();

        productVariantRepository.saveAll(variants);
        savedProduct.setVariants(new ArrayList<>(variants));

        if (images != null && !images.isEmpty()) {
            List<ProductImage> productImages = images.stream()
                    .filter(file -> !file.isEmpty())
                    .map(file -> ProductImage.builder()
                            .product(savedProduct)
                            .image(fileStorageService.save(file))
                            .build())
                    .toList();

            productImageRepository.saveAll(productImages);
            if (savedProduct.getImages() == null) {
                savedProduct.setImages(new ArrayList<>());
            }
            savedProduct.getImages().addAll(productImages);        }

        return savedProduct;
    }

    @Transactional
    public Optional<Product> update(
            Long id,
            ProductUpdateDTO dto,
            MultipartFile mainImage,
            List<MultipartFile> images
    ) {
        return productRepository.findById(id)
                .map(entity -> {

                    entity.setName(dto.getName());
                    entity.setPrice(dto.getPrice());
                    entity.setMaterial(dto.getMaterial());
                    entity.setFeatures(dto.getFeatures());
                    entity.setCircumference(dto.getCircumference());
                    entity.setCup(dto.getCup());

                    Category category = categoryRepository.findById(dto.getCategoryId())
                            .orElseThrow();
                    entity.setCategory(category);

                    syncVariants(entity, dto.getVariants());

                    if (mainImage != null) {
                        if (entity.getImage() != null) {
                            fileStorageService.delete(entity.getImage());
                        }
                        String mainImagePath = fileStorageService.save(mainImage);
                        entity.setImage(mainImagePath);
                    }

                    Product savedProduct = productRepository.save(entity);

                    if (dto.getImagesToDelete() != null && !dto.getImagesToDelete().isEmpty()) {
                        List<ProductImage> productImagesToDelete = savedProduct.getImages().stream()
                                .filter(productImage -> dto.getImagesToDelete().contains(productImage.getImage()))
                                .toList();

                        savedProduct.getImages().removeAll(productImagesToDelete);

                        productImagesToDelete.forEach(productImage ->
                                fileStorageService.delete(productImage.getImage())
                        );
                    }

                    if (images != null && !images.isEmpty()) {
                        List<ProductImage> productImages = images.stream()
                                .filter(file -> file != null && !file.isEmpty())
                                .map(file -> ProductImage.builder()
                                        .product(savedProduct)
                                        .image(fileStorageService.save(file))
                                        .build())
                                .toList();

                        productImageRepository.saveAll(productImages);
                        if (savedProduct.getImages() == null) {
                            savedProduct.setImages(new ArrayList<>());
                        }
                        savedProduct.getImages().addAll(productImages);
                    }

                    return savedProduct;
                });
    }

    /**
     * Reconciles the product's variants with the requested list:
     * - entries with an id update the matching existing variant (and reactivate it if it was inactive);
     * - entries without an id either reactivate a matching inactive variant (same size+color) or create a new one;
     * - any currently active variant not referenced by the request is soft-deleted (active = false), so
     *   historical OrderItems that still reference it keep resolving correctly.
     */
    private void syncVariants(Product product, List<ProductVariantRequestDTO> requested) {
        validateNoDuplicateVariants(requested);

        Map<Long, ProductVariant> existingById = new HashMap<>();
        for (ProductVariant variant : product.getVariants()) {
            existingById.put(variant.getId(), variant);
        }

        Set<Long> keptIds = new HashSet<>();

        for (ProductVariantRequestDTO req : requested) {
            if (req.getId() != null) {
                ProductVariant existing = existingById.get(req.getId());
                if (existing == null) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Варіант не належить цьому товару");
                }

                existing.setSize(req.getSize());
                existing.setColor(req.getColor());
                existing.setQuantity(req.getQuantity());
                existing.setInStock(req.getQuantity() > 0);
                existing.setActive(true);
                keptIds.add(existing.getId());
            } else {
                ProductVariant reactivated = product.getVariants().stream()
                        .filter(v -> !Boolean.TRUE.equals(v.getActive()))
                        .filter(v -> matchesSizeColor(v, req))
                        .findFirst()
                        .orElse(null);

                if (reactivated != null) {
                    reactivated.setQuantity(req.getQuantity());
                    reactivated.setInStock(req.getQuantity() > 0);
                    reactivated.setActive(true);
                    keptIds.add(reactivated.getId());
                } else {
                    ProductVariant created = ProductVariant.builder()
                            .product(product)
                            .size(req.getSize())
                            .color(req.getColor())
                            .quantity(req.getQuantity())
                            .inStock(req.getQuantity() > 0)
                            .active(true)
                            .build();
                    productVariantRepository.save(created);
                    product.getVariants().add(created);
                    keptIds.add(created.getId());
                }
            }
        }

        for (ProductVariant existing : product.getVariants()) {
            if (Boolean.TRUE.equals(existing.getActive()) && !keptIds.contains(existing.getId())) {
                existing.setActive(false);
            }
        }
    }

    private boolean matchesSizeColor(ProductVariant variant, ProductVariantRequestDTO req) {
        return Objects.equals(variant.getSize(), req.getSize()) && Objects.equals(variant.getColor(), req.getColor());
    }

    private void validateNoDuplicateVariants(List<ProductVariantRequestDTO> variants) {
        Set<String> seen = new HashSet<>();
        for (ProductVariantRequestDTO v : variants) {
            String key = v.getSize() + "::" + v.getColor();
            if (!seen.add(key)) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Дублікат варіанту: розмір '" + v.getSize() + "', колір '" + v.getColor() + "'"
                );
            }
        }
    }

    @Transactional
    public Optional<Product> delete(Long id) {
        return productRepository.findById(id)
                .map(entity -> {
                    entity.setDeleted(true);

                    return productRepository.save(entity);
                });
    }
}
