package org.underwearshop.underwearshop.service;

import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.underwearshop.underwearshop.dto.OrderCreateDTO;
import org.underwearshop.underwearshop.dto.OrderUpdateDTO;
import org.underwearshop.underwearshop.entity.Order;
import org.underwearshop.underwearshop.entity.OrderItem;
import org.underwearshop.underwearshop.entity.OrderStatus;
import org.underwearshop.underwearshop.entity.Product;
import org.underwearshop.underwearshop.repository.OrderItemRepository;
import org.underwearshop.underwearshop.repository.OrderRepository;
import org.underwearshop.underwearshop.repository.ProductRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
    public Page<Order> findAll(@Nullable OrderStatus status, int page, int size) {
        if (status == null) {
            return orderRepository.findAll(PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id")));
        }

        return orderRepository.findAllByStatusIs(status, PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id")));
    }

    @Transactional(readOnly = true)
    public Optional<Order> findOne(Long id) {
        return orderRepository.findById(id);
    }

    @Transactional
    public Order create(OrderCreateDTO dto) {
        Order order = Order.builder()
                .username(dto.getUsername())
                .telephone(dto.getTelephone())
                .deliveryAddress(dto.getDeliveryAddress())
                .deliveryType(dto.getDeliveryType())
                .status(OrderStatus.PENDING)
                .build();

        orderRepository.save(order);

        List<OrderItem> orderItems = dto.getOrderItems().stream().map(it -> {
            Product product = productRepository.findById(it.getProductId()).orElseThrow();
            return OrderItem.builder().order(order).product(product).price(product.getPrice()).quantity(it.getQuantity()).build();
        }).toList();

        orderItemRepository.saveAll(orderItems);

        return order;
    }

    @Transactional
    public Optional<Order> update(Long id, OrderUpdateDTO dto) {
        return orderRepository.findById(id).map(order -> {
            order.setUsername(dto.getUsername());
            order.setTelephone(dto.getTelephone());
            order.setDeliveryAddress(dto.getDeliveryAddress());
            order.setStatus(dto.getStatus());

            return orderRepository.save(order);
        });
    }
}
