package org.underwearshop.underwearshop.email;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.underwearshop.underwearshop.entity.Order;
import org.underwearshop.underwearshop.entity.OrderItem;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final TemplateEngine templateEngine;

    @Value("${sendgrid.api.key}")
    private String apiKey;

    @Value("${mail.from}")
    private String from;

    public void sendOrderConfirmation(Order order) {

        if (order.getEmail() == null || order.getEmail().isBlank()) {
            return;
        }

        List<OrderItemView> items = buildItemViews(order);
        BigDecimal total = items.stream()
                .map(OrderItemView::lineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Context context = new Context();
        context.setVariable("order", order);
        context.setVariable("items", items);
        context.setVariable("total", total);

        String html = templateEngine.process("order-confirmation", context);

        Email fromEmail = new Email(from);
        Email toEmail = new Email(order.getEmail());

        Content content = new Content("text/html", html);
        Mail mail = new Mail(fromEmail, "Підтвердження замовлення #" + order.getId(), toEmail, content);

        SendGrid sg = new SendGrid(apiKey);
        Request request = new Request();

        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");

            request.setBody(mail.build());

            Response response = sg.api(request);

            if (response.getStatusCode() >= 400) {
                log.warn("SendGrid failed: status={}, body={}", response.getStatusCode(), response.getBody());
            }

        } catch (IOException e) {
            log.warn("Failed to send email via SendGrid", e);
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
            return new OrderItemView(
                    item.getProduct().getName(),
                    item.getQuantity(),
                    price,
                    lineTotal
            );
        }).toList();
    }

    public record OrderItemView(
            String productName,
            Integer quantity,
            BigDecimal price,
            BigDecimal lineTotal
    ) {
    }
}