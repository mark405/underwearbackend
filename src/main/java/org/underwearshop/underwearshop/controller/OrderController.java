package org.underwearshop.underwearshop.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.underwearshop.underwearshop.dto.*;
import org.underwearshop.underwearshop.entity.OrderStatus;
import org.underwearshop.underwearshop.service.CategoryService;
import org.underwearshop.underwearshop.service.OrderService;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/order")
public class OrderController {
    private final OrderService orderService;

    @GetMapping("/admin")
    public Page<OrderDTO> findAll(@RequestParam(required = false) OrderStatus status, @RequestParam int page, @RequestParam int size) {
        return orderService.findAll(status, page, size).map(OrderDTO::new);
    }

    @GetMapping("/admin/{id}")
    public OrderDTO findOne(@PathVariable Long id) {
        return orderService.findOne(id).map(OrderDTO::new).orElseThrow();
    }

    @PostMapping(value = "create")
    @ResponseStatus(HttpStatus.CREATED)
    public OrderDTO create(@RequestPart("data") @Valid OrderCreateDTO dto) {
        return new OrderDTO(orderService.create(dto));
    }

    @PutMapping(value = "/admin/update/{id}")
    public OrderDTO update(
            @PathVariable Long id,
            @RequestPart("data") @Valid OrderUpdateDTO dto
    ) {
        return new OrderDTO(orderService.update(id, dto).orElseThrow());
    }
}
