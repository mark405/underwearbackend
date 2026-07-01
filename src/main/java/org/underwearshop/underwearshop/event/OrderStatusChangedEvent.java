package org.underwearshop.underwearshop.event;

import org.underwearshop.underwearshop.entity.OrderStatus;

public record OrderStatusChangedEvent(Long orderId, OrderStatus oldStatus, OrderStatus newStatus) {
}
