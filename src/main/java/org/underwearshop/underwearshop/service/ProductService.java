package org.underwearshop.underwearshop.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.underwearshop.underwearshop.dto.ProductCreateDTO;
import org.underwearshop.underwearshop.dto.ProductUpdateDTO;
import org.underwearshop.underwearshop.entity.Product;
import org.underwearshop.underwearshop.repository.ProductRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final FileStorageService fileStorageService;

    @Transactional(readOnly = true)
    public Page<Product> findAll(int page, int size) {
        return productRepository.findAll(PageRequest.of(page, size));
    }

    @Transactional(readOnly = true)
    public Optional<Product> findOne(Long id) {
        return productRepository.findById(id);
    }

    @Transactional
    public Product create(ProductCreateDTO dto, MultipartFile file) {
        Product category = Product.builder().name(dto.getName()).build();

        String path = fileStorageService.save(file);
        category.setImage(path);

        return productRepository.save(category);
    }

    public Optional<Product> update(Long id, ProductUpdateDTO dto, MultipartFile file) {
        return productRepository.findById(id)
                .map(entity -> {

                    entity.setName(dto.getName());

                    String path = fileStorageService.save(file);
                    if (path != null) {
                        entity.setImage(path);
                    }

                    return productRepository.save(entity);
                });
    }
}
