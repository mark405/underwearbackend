package org.underwearshop.underwearshop.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.underwearshop.underwearshop.dto.CategoryCreateDTO;
import org.underwearshop.underwearshop.dto.CategoryUpdateDTO;
import org.underwearshop.underwearshop.entity.Category;
import org.underwearshop.underwearshop.repository.CategoryRepository;
import org.underwearshop.underwearshop.repository.ProductRepository;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final FileStorageService fileStorageService;

    @Transactional(readOnly = true)
    public List<Category> findAll(Long parentId) {
        if (parentId == null) {
            return categoryRepository.findByParentIsNullOrderByPositionAscIdAsc();
        }

        return categoryRepository.findByParentIdOrderByPositionAscIdAsc(parentId);
    }

    @Transactional(readOnly = true)
    public Optional<Category> findOne(Long id) {
        return categoryRepository.findById(id);
    }

    @Transactional
    public Category create(CategoryCreateDTO dto, MultipartFile file) {
        Category parent = null;

        if (dto.getParentId() != null) {
            parent = categoryRepository.findById(dto.getParentId()).orElseThrow();
        }

        int nextPosition = (dto.getParentId() == null
                ? categoryRepository.findTopByParentIsNullOrderByPositionDesc()
                : categoryRepository.findTopByParentIdOrderByPositionDesc(dto.getParentId()))
                .map(category -> category.getPosition() + 1)
                .orElse(0);

        Category category = Category.builder()
                .name(dto.getName())
                .parent(parent)
                .position(nextPosition)
                .build();

        String path = fileStorageService.save(file);
        category.setImage(path);

        return categoryRepository.save(category);
    }

    @Transactional
    public Optional<Category> delete(Long id) {
        return categoryRepository.findById(id)
                .map(entity -> {
                    if (categoryRepository.existsByParentId(id)) {
                        throw new ResponseStatusException(
                                HttpStatus.CONFLICT,
                                "Спочатку видаліть підкатегорії цієї категорії"
                        );
                    }

                    if (productRepository.existsByCategoryId(id)) {
                        throw new ResponseStatusException(
                                HttpStatus.CONFLICT,
                                "Спочатку видаліть або перенесіть товари цієї категорії"
                        );
                    }

                    if (entity.getImage() != null) {
                        fileStorageService.delete(entity.getImage());
                    }

                    categoryRepository.delete(entity);

                    return entity;
                });
    }

    @Transactional
    public void reorder(List<Long> orderedIds) {
        for (int index = 0; index < orderedIds.size(); index++) {
            Category category = categoryRepository.findById(orderedIds.get(index)).orElseThrow();
            category.setPosition(index);
            categoryRepository.save(category);
        }
    }

    @Transactional
    public Optional<Category> update(Long id, CategoryUpdateDTO dto, MultipartFile file) {
        return categoryRepository.findById(id)
                .map(entity -> {

                    entity.setName(dto.getName());

                    if (file != null && !file.isEmpty()) {
                        if (entity.getImage() != null) {
                            fileStorageService.delete(entity.getImage());
                        }

                        entity.setImage(fileStorageService.save(file));
                    }

                    return categoryRepository.save(entity);
                });
    }

    @Transactional
    public Optional<Category> deleteImage(Long id) {
        return categoryRepository.findById(id)
                .map(entity -> {
                    if (entity.getImage() != null) {
                        fileStorageService.delete(entity.getImage());
                        entity.setImage(null);
                    }

                    return categoryRepository.save(entity);
                });
    }
}
