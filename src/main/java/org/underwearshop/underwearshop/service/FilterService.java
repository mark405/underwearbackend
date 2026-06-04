package org.underwearshop.underwearshop.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.underwearshop.underwearshop.dto.FilterCreateDTO;
import org.underwearshop.underwearshop.dto.FilterUpdateDTO;
import org.underwearshop.underwearshop.entity.Filter;
import org.underwearshop.underwearshop.repository.FilterRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FilterService {
    private final FilterRepository filterRepository;

    @Transactional(readOnly = true)
    public List<Filter> findAll() {
        return filterRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Filter> findOne(Long id) {
        return filterRepository.findById(id);
    }

    @Transactional
    public Filter create(FilterCreateDTO dto) {
        Filter filter = Filter.builder().name(dto.getName()).build();

        return filterRepository.save(filter);
    }

    public Optional<Filter> update(Long id, FilterUpdateDTO dto) {
        return filterRepository.findById(id)
                .map(entity -> {
                    entity.setName(dto.getName());

                    return filterRepository.save(entity);
                });
    }
}
