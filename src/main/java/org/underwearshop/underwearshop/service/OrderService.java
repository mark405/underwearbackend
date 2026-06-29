package org.underwearshop.underwearshop.service;

import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.underwearshop.underwearshop.dto.OrderCreateDTO;
import org.underwearshop.underwearshop.dto.OrderItemCreateDTO;
import org.underwearshop.underwearshop.dto.OrderUpdateDTO;
import org.underwearshop.underwearshop.entity.Order;
import org.underwearshop.underwearshop.entity.OrderItem;
import org.underwearshop.underwearshop.entity.OrderStatus;
import org.underwearshop.underwearshop.entity.Product;
import org.underwearshop.underwearshop.event.OrderCreatedEvent;
import org.underwearshop.underwearshop.event.OrderStatusChangedEvent;
import org.underwearshop.underwearshop.repository.OrderItemRepository;
import org.underwearshop.underwearshop.repository.OrderRepository;
import org.underwearshop.underwearshop.repository.ProductRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    private final ApplicationEventPublisher eventPublisher;

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

    @Transactional(readOnly = true)
    public List<Order> findAllPending() {
        return orderRepository.findAllByStatusIsOrderByIdDesc(OrderStatus.PENDING);
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

        Map<Long, Product> productsById = dto.getOrderItems().stream()
                .map(OrderItemCreateDTO::getProductId)
                .distinct()
                .collect(Collectors.toMap(id -> id, id -> productRepository.findById(id).orElseThrow()));

        List<OrderItem> orderItems = dto.getOrderItems().stream().map(it -> {
            Product product = productsById.get(it.getProductId());
            return OrderItem.builder().order(order).product(product).price(product.getPrice()).quantity(it.getQuantity()).build();
        }).toList();

        orderItemRepository.saveAll(orderItems);

        for (OrderItem orderItem : orderItems) {
            Product product = orderItem.getProduct();

            product.setQuantity(product.getQuantity() - orderItem.getQuantity());
            product.setInStock(product.getQuantity() > 0);
        }

        productRepository.saveAll(productsById.values());

        eventPublisher.publishEvent(new OrderCreatedEvent(order.getId()));

        return order;
    }

    @Transactional
    public Optional<Order> update(Long id, OrderUpdateDTO dto) {
        return orderRepository.findById(id).map(order -> {
            OrderStatus oldStatus = order.getStatus();

            order.setUsername(dto.getUsername());
            order.setTelephone(dto.getTelephone());
            order.setDeliveryAddress(dto.getDeliveryAddress());
            order.setStatus(dto.getStatus());

            Order saved = orderRepository.save(order);

            if (oldStatus != dto.getStatus()) {
                eventPublisher.publishEvent(new OrderStatusChangedEvent(id, oldStatus, dto.getStatus()));
            }

            return saved;
        });
    }

    @Transactional
    public Optional<Order> updateStatus(Long id, OrderStatus status) {
        return orderRepository.findById(id).map(order -> {
            OrderStatus oldStatus = order.getStatus();

            if (oldStatus == status) {
                return order;
            }

            order.setStatus(status);

            Order saved = orderRepository.save(order);

            eventPublisher.publishEvent(new OrderStatusChangedEvent(id, oldStatus, status));

            return saved;
        });
    }
}
