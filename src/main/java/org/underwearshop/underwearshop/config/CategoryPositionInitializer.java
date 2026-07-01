package org.underwearshop.underwearshop.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.underwearshop.underwearshop.entity.Category;
import org.underwearshop.underwearshop.repository.CategoryRepository;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CategoryPositionInitializer implements ApplicationRunner {
    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        Map<Long, List<Category>> byParent = categoryRepository.findAll().stream()
                .collect(Collectors.groupingBy(category ->
                        category.getParent() == null ? -1L : category.getParent().getId()));

        for (List<Category> siblings : byParent.values()) {
            siblings.sort(Comparator.comparing(Category::getId));

            int nextPosition = siblings.stream()
                    .map(Category::getPosition)
                    .filter(Objects::nonNull)
                    .max(Integer::compareTo)
                    .map(max -> max + 1)
                    .orElse(0);

            for (Category category : siblings) {
                if (category.getPosition() == null) {
                    category.setPosition(nextPosition++);
                    categoryRepository.save(category);
                }
            }
        }
    }
}
