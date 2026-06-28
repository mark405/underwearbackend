package org.underwearshop.underwearshop.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.underwearshop.underwearshop.dto.ProductCreateDTO;
import org.underwearshop.underwearshop.dto.ProductFilter;
import org.underwearshop.underwearshop.dto.ProductUpdateDTO;
import org.underwearshop.underwearshop.entity.Category;
import org.underwearshop.underwearshop.entity.Product;
import org.underwearshop.underwearshop.entity.ProductImage;
import org.underwearshop.underwearshop.repository.CategoryRepository;
import org.underwearshop.underwearshop.repository.ProductImageRepository;
import org.underwearshop.underwearshop.repository.ProductRepository;
import org.underwearshop.underwearshop.repository.ProductSpecifications;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
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

        Product product = Product.builder()
                .name(dto.getName())
                .price(dto.getPrice())
                .material(dto.getMaterial())
                .color(dto.getColor())
                .features(dto.getFeatures())
                .circumference(dto.getCircumference())
                .cup(dto.getCup())
                .size(dto.getSize())
                .category(category)
                .inStock(true)
                .deleted(false)
                .build();

        if (mainImage != null && !mainImage.isEmpty()) {
            product.setImage(fileStorageService.save(mainImage));
        }

        Product savedProduct = productRepository.save(product);

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
                    entity.setColor(dto.getColor());
                    entity.setFeatures(dto.getFeatures());
                    entity.setCircumference(dto.getCircumference());
                    entity.setCup(dto.getCup());
                    entity.setSize(dto.getSize());
                    entity.setInStock(dto.getInStock());

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

    @Transactional
    public Optional<Product> delete(Long id) {
        return productRepository.findById(id)
                .map(entity -> {
                    entity.setDeleted(true);

                    return productRepository.save(entity);
                });
    }
}
