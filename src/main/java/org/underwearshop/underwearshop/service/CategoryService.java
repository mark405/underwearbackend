package org.underwearshop.underwearshop.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.underwearshop.underwearshop.dto.CategoryCreateDTO;
import org.underwearshop.underwearshop.dto.CategoryDTO;
import org.underwearshop.underwearshop.dto.CategoryUpdateDTO;
import org.underwearshop.underwearshop.entity.Category;
import org.underwearshop.underwearshop.repository.CategoryRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final FileStorageService fileStorageService;

    @Transactional(readOnly = true)
    public Page<Category> findAll(int page, int size) {
        return categoryRepository.findAll(PageRequest.of(page, size));
    }

    @Transactional(readOnly = true)
    public Optional<Category> findOne(Long id) {
        return categoryRepository.findById(id);
    }

    @Transactional
    public Category create(CategoryCreateDTO dto, MultipartFile file) {
        Category category = Category.builder().name(dto.getName()).build();

        String path = fileStorageService.save(file);
        category.setImage(path);

        return categoryRepository.save(category);
    }

    public Optional<Category> update(Long id, CategoryUpdateDTO dto, MultipartFile file) {
        return categoryRepository.findById(id)
                .map(entity -> {

                    entity.setName(dto.getName());

                    String path = fileStorageService.save(file);
                    if (path != null) {
                        entity.setImage(path);
                    }

                    return categoryRepository.save(entity);
                });
    }
}
