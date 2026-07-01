package org.underwearshop.underwearshop.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.underwearshop.underwearshop.entity.Banner;
import org.underwearshop.underwearshop.repository.BannerRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BannerService {
    private final BannerRepository bannerRepository;
    private final FileStorageService fileStorageService;

    @Transactional(readOnly = true)
    public List<Banner> findAll() {
        return bannerRepository.findAllByOrderByPositionAsc();
    }

    @Transactional
    public Banner create(MultipartFile file) {
        int nextPosition = bannerRepository.findTopByOrderByPositionDesc()
                .map(banner -> banner.getPosition() + 1)
                .orElse(0);

        Banner banner = Banner.builder()
                .image(fileStorageService.save(file))
                .position(nextPosition)
                .build();

        return bannerRepository.save(banner);
    }

    @Transactional
    public void delete(Long id) {
        bannerRepository.findById(id).ifPresent(banner -> {
            fileStorageService.delete(banner.getImage());
            bannerRepository.delete(banner);
        });
    }
}
