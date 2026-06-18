package org.underwearshop.underwearshop.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.underwearshop.underwearshop.dto.OrderCreateDTO;
import org.underwearshop.underwearshop.dto.OrderDTO;
import org.underwearshop.underwearshop.dto.OrderUpdateDTO;
import org.underwearshop.underwearshop.dto.ShortOrderDTO;
import org.underwearshop.underwearshop.entity.OrderStatus;
import org.underwearshop.underwearshop.service.OrderService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/order")
public class OrderController {
    private final OrderService orderService;

    @GetMapping("/admin")
    public Page<ShortOrderDTO> findAll(@RequestParam(required = false) OrderStatus status, @RequestParam int page, @RequestParam int size) {
        return orderService.findAll(status, page, size).map(ShortOrderDTO::new);
    }

    @GetMapping("/admin/{id}")
    public OrderDTO findOne(@PathVariable Long id) {
        return orderService.findOne(id).map(OrderDTO::new).orElseThrow();
    }

    @PostMapping(value = "create")
    @ResponseStatus(HttpStatus.CREATED)
    public ShortOrderDTO create(@RequestPart("data") @Valid OrderCreateDTO dto) {
        return new ShortOrderDTO(orderService.create(dto));
    }

    @PutMapping(value = "/admin/update/{id}")
    public ShortOrderDTO update(
            @PathVariable Long id,
            @RequestPart("data") @Valid OrderUpdateDTO dto
    ) {
        return new ShortOrderDTO(orderService.update(id, dto).orElseThrow());
    }
}
