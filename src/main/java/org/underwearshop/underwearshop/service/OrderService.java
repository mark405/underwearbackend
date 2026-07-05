package org.underwearshop.underwearshop.service;

import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.underwearshop.underwearshop.dto.OrderCreateDTO;
import org.underwearshop.underwearshop.dto.OrderItemCreateDTO;
import org.underwearshop.underwearshop.dto.OrderUpdateDTO;
import org.underwearshop.underwearshop.email.EmailService;
import org.underwearshop.underwearshop.entity.Order;
import org.underwearshop.underwearshop.entity.OrderItem;
import org.underwearshop.underwearshop.entity.OrderStatus;
import org.underwearshop.underwearshop.entity.Product;
import org.underwearshop.underwearshop.entity.ProductVariant;
import org.underwearshop.underwearshop.event.OrderCreatedEvent;
import org.underwearshop.underwearshop.event.OrderStatusChangedEvent;
import org.underwearshop.underwearshop.repository.OrderItemRepository;
import org.underwearshop.underwearshop.repository.OrderRepository;
import org.underwearshop.underwearshop.repository.ProductVariantRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {
    private static final String PICKUP_DELIVERY_TYPE = "pickup";

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductVariantRepository productVariantRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final ShopSettingsService shopSettingsService;
    private final EmailService emailService;

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
        if (PICKUP_DELIVERY_TYPE.equalsIgnoreCase(dto.getDeliveryType()) && !shopSettingsService.get().isPickupEnabled()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Самовивіз наразі недоступний");
        }

        Order order = Order.builder()
                .username(dto.getUsername())
                .email(dto.getEmail())
                .telephone(dto.getTelephone())
                .deliveryAddress(dto.getDeliveryAddress())
                .deliveryType(dto.getDeliveryType())
                .contactByPhone(dto.isContactByPhone())
                .status(OrderStatus.PENDING)
                .build();

        orderRepository.save(order);

        Map<Long, ProductVariant> variantsById = dto.getOrderItems().stream()
                .map(OrderItemCreateDTO::getProductVariantId)
                .distinct()
                .collect(Collectors.toMap(id -> id, id -> productVariantRepository.findById(id)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Товар не знайдено"))));

        Map<Long, Integer> requestedQuantityByVariant = new HashMap<>();
        for (OrderItemCreateDTO item : dto.getOrderItems()) {
            requestedQuantityByVariant.merge(item.getProductVariantId(), item.getQuantity(), Integer::sum);
        }

        for (Map.Entry<Long, Integer> entry : requestedQuantityByVariant.entrySet()) {
            ProductVariant variant = variantsById.get(entry.getKey());
            int available = variant.getQuantity() != null ? variant.getQuantity() : 0;
            if (available < entry.getValue()) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Недостатньо товару на складі: " + variant.getProduct().getName()
                                + " (" + variant.getSize() + ", " + variant.getColor() + ")"
                );
            }
        }

        List<OrderItem> orderItems = dto.getOrderItems().stream().map(it -> {
            ProductVariant variant = variantsById.get(it.getProductVariantId());
            Product product = variant.getProduct();
            return OrderItem.builder()
                    .order(order)
                    .product(product)
                    .productVariant(variant)
                    .size(variant.getSize())
                    .color(variant.getColor())
                    .price(product.getPrice())
                    .quantity(it.getQuantity())
                    .build();
        }).toList();

        orderItemRepository.saveAll(orderItems);
        order.setOrderItems(orderItems);

        for (OrderItem orderItem : orderItems) {
            ProductVariant variant = orderItem.getProductVariant();
            variant.setQuantity(variant.getQuantity() - orderItem.getQuantity());
            variant.setInStock(variant.getQuantity() > 0);
        }

        productVariantRepository.saveAll(variantsById.values());

        eventPublisher.publishEvent(new OrderCreatedEvent(order.getId()));

        return order;
    }

    @Transactional
    public void resendConfirmationEmail(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Замовлення не знайдено"));

        emailService.resendOrderConfirmation(order);
    }

    @Transactional
    public Optional<Order> update(Long id, OrderUpdateDTO dto) {
        return orderRepository.findById(id).map(order -> {
            OrderStatus oldStatus = order.getStatus();

            adjustStockOnStatusChange(order, oldStatus, dto.getStatus());

            order.setUsername(dto.getUsername());
            order.setEmail(dto.getEmail());
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

            adjustStockOnStatusChange(order, oldStatus, status);

            order.setStatus(status);

            Order saved = orderRepository.save(order);

            eventPublisher.publishEvent(new OrderStatusChangedEvent(id, oldStatus, status));

            return saved;
        });
    }

    /**
     * Keeps variant stock in sync with order status transitions:
     * - moving INTO CANCELLED returns the stock the order took;
     * - moving OUT OF CANCELLED (order reinstated) deducts it again, failing the whole
     *   transition with a 400 if any variant no longer has enough stock available.
     * Other transitions (e.g. PENDING <-> DELIVERED) don't touch stock.
     */
    private void adjustStockOnStatusChange(Order order, OrderStatus oldStatus, OrderStatus newStatus) {
        if (oldStatus == newStatus) {
            return;
        }

        List<OrderItem> items = order.getOrderItems();
        if (items == null || items.isEmpty()) {
            return;
        }

        boolean cancelling = newStatus == OrderStatus.CANCELLED && oldStatus != OrderStatus.CANCELLED;
        boolean reinstating = oldStatus == OrderStatus.CANCELLED && newStatus != OrderStatus.CANCELLED;

        if (!cancelling && !reinstating) {
            return;
        }

        List<ProductVariant> touchedVariants = items.stream()
                .map(OrderItem::getProductVariant)
                .filter(Objects::nonNull)
                .toList();

        if (cancelling) {
            for (OrderItem item : items) {
                ProductVariant variant = item.getProductVariant();
                if (variant == null) {
                    continue;
                }
                int current = variant.getQuantity() != null ? variant.getQuantity() : 0;
                variant.setQuantity(current + item.getQuantity());
                variant.setInStock(variant.getQuantity() > 0);
            }
        } else {
            // Aggregate demand per variant first (rather than validating each OrderItem against the
            // pre-mutation snapshot independently), in case an order somehow has multiple items pointing
            // at the same variant — otherwise each check would pass against the same stale "available"
            // number and the combined deduction below could still push the variant negative.
            Map<Long, Integer> requiredByVariant = new HashMap<>();
            for (OrderItem item : items) {
                ProductVariant variant = item.getProductVariant();
                if (variant == null) {
                    continue;
                }
                requiredByVariant.merge(variant.getId(), item.getQuantity(), Integer::sum);
            }

            for (OrderItem item : items) {
                ProductVariant variant = item.getProductVariant();
                if (variant == null) {
                    continue;
                }
                int available = variant.getQuantity() != null ? variant.getQuantity() : 0;
                int required = requiredByVariant.getOrDefault(variant.getId(), item.getQuantity());
                if (available < required) {
                    throw new ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            "Недостатньо товару на складі, щоб повернути замовлення зі статусу 'Скасовано': "
                                    + item.getProduct().getName()
                                    + (item.getSize() != null ? " (" + item.getSize() + ", " + item.getColor() + ")" : "")
                    );
                }
            }

            for (OrderItem item : items) {
                ProductVariant variant = item.getProductVariant();
                if (variant == null) {
                    continue;
                }
                variant.setQuantity(variant.getQuantity() - item.getQuantity());
                variant.setInStock(variant.getQuantity() > 0);
            }
        }

        productVariantRepository.saveAll(touchedVariants);
    }
}
