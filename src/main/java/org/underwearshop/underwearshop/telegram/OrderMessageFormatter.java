package org.underwearshop.underwearshop.telegram;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.underwearshop.underwearshop.entity.Order;
import org.underwearshop.underwearshop.entity.OrderItem;
import org.underwearshop.underwearshop.entity.OrderStatus;

import java.math.BigDecimal;
import java.util.List;

final class OrderMessageFormatter {

    private OrderMessageFormatter() {
    }

    static String formatOrder(Order order, String title) {
        StringBuilder sb = new StringBuilder();
        sb.append(title).append(" #").append(order.getId()).append("\n");
        sb.append("Клієнт: ").append(order.getUsername()).append("\n");
        sb.append("Телефон: ").append(order.getTelephone()).append("\n");
        sb.append("Доставка: ").append(order.getDeliveryType()).append(", ").append(order.getDeliveryAddress()).append("\n");
        sb.append("Статус: ").append(statusLabel(order.getStatus())).append("\n");
        sb.append("Товари:\n");

        BigDecimal total = BigDecimal.ZERO;
        List<OrderItem> orderItems = order.getOrderItems();
        if (orderItems != null) {
            for (OrderItem item : orderItems) {
                BigDecimal price = item.getPrice() != null ? item.getPrice() : item.getProduct().getPrice();
                BigDecimal lineTotal = price.multiply(BigDecimal.valueOf(item.getQuantity()));
                total = total.add(lineTotal);
                sb.append(" - ").append(item.getProduct().getName())
                        .append(" x").append(item.getQuantity())
                        .append(" = ").append(lineTotal).append("\n");
            }
        }

        sb.append("Разом: ").append(total);

        return sb.toString();
    }

    static String statusLabel(OrderStatus status) {
        return switch (status) {
            case PENDING -> "В очікуванні";
            case DELIVERED -> "Доставлено";
            case CANCELLED -> "Скасовано";
        };
    }

    static InlineKeyboardMarkup statusKeyboard(Long orderId) {
        List<InlineKeyboardButton> row = List.of(
                button("⏳ В очікуванні", orderId, OrderStatus.PENDING),
                button("✅ Доставлено", orderId, OrderStatus.DELIVERED),
                button("❌ Скасовано", orderId, OrderStatus.CANCELLED)
        );

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        markup.setKeyboard(List.of(row));

        return markup;
    }

    private static InlineKeyboardButton button(String label, Long orderId, OrderStatus status) {
        InlineKeyboardButton button = new InlineKeyboardButton(label);
        button.setCallbackData("ord:" + orderId + ":" + status);

        return button;
    }
}
