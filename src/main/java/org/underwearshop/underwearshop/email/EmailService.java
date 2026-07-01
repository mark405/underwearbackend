package org.underwearshop.underwearshop.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.underwearshop.underwearshop.entity.Order;
import org.underwearshop.underwearshop.entity.OrderItem;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${mail.from}")
    private String from;

    public void sendOrderConfirmation(Order order) {
        if (order.getEmail() == null || order.getEmail().isBlank()) {
            return;
        }

        List<OrderItemView> items = buildItemViews(order);
        BigDecimal total = items.stream().map(OrderItemView::lineTotal).reduce(BigDecimal.ZERO, BigDecimal::add);

        Context context = new Context();
        context.setVariable("order", order);
        context.setVariable("items", items);
        context.setVariable("total", total);

        String html = templateEngine.process("order-confirmation", context);

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");
            helper.setTo(order.getEmail());
            helper.setFrom(from);
            helper.setSubject("Підтвердження замовлення #" + order.getId());
            helper.setText(html, true);
            mailSender.send(message);
        } catch (MessagingException e) {
            log.warn("Failed to send order confirmation email to {}", order.getEmail(), e);
        }
    }

    private List<OrderItemView> buildItemViews(Order order) {
        List<OrderItem> orderItems = order.getOrderItems();
        if (orderItems == null) {
            return List.of();
        }

        return orderItems.stream().map(item -> {
            BigDecimal price = item.getPrice() != null ? item.getPrice() : item.getProduct().getPrice();
            BigDecimal lineTotal = price.multiply(BigDecimal.valueOf(item.getQuantity()));
            return new OrderItemView(item.getProduct().getName(), item.getQuantity(), price, lineTotal);
        }).toList();
    }

    public record OrderItemView(String productName, Integer quantity, BigDecimal price, BigDecimal lineTotal) {
    }
}
