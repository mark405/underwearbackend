package org.underwearshop.underwearshop.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.underwearshop.underwearshop.dto.SettlementAreaDTO;
import org.underwearshop.underwearshop.dto.SettlementDTO;
import org.underwearshop.underwearshop.dto.WareHouseDTO;
import org.underwearshop.underwearshop.service.NovaPostClientService;

import java.util.List;

@RestController
@RequestMapping("/api/nova-post")
@RequiredArgsConstructor
public class NovaPostController {

    private final NovaPostClientService novaPostClientService;

    @GetMapping("/getSettlementAreas")
    public List<SettlementAreaDTO> getSettlementAreas() {
        return novaPostClientService.getSettlementAreas();
    }

    @GetMapping("/getSettlements")
    public Page<SettlementDTO> getSettlements(@RequestParam String area, @RequestParam(required = false) String search, @RequestParam int page) {
        return novaPostClientService.getSettlements(area, search, page);
    }

    @GetMapping("/getWarehouses")
    public Page<WareHouseDTO> getWarehouses(@RequestParam String city, @RequestParam(required = false) String search, @RequestParam int page, @RequestParam int limit) {
        return novaPostClientService.getWarehouses(city, search, page, limit);
    }
}