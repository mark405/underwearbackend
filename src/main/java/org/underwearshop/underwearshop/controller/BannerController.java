package org.underwearshop.underwearshop.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.underwearshop.underwearshop.dto.BannerDTO;
import org.underwearshop.underwearshop.service.BannerService;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/banner")
public class BannerController {
    private final BannerService bannerService;

    @GetMapping
    public List<BannerDTO> findAll() {
        return bannerService.findAll().stream().map(BannerDTO::new).toList();
    }

    @PostMapping(value = "/admin/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public BannerDTO create(@RequestPart("file") MultipartFile file) {
        return new BannerDTO(bannerService.create(file));
    }

    @DeleteMapping("/admin/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        bannerService.delete(id);
    }
}
