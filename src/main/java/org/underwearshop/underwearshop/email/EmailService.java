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
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.underwearshop.underwearshop.entity.EmailLog;
import org.underwearshop.underwearshop.entity.Order;
import org.underwearshop.underwearshop.entity.OrderItem;
import org.underwearshop.underwearshop.repository.EmailLogRepository;
import org.underwearshop.underwearshop.service.EmailPaymentDetailsService;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final TemplateEngine templateEngine;
    private final EmailLogRepository emailLogRepository;
    private final EmailPaymentDetailsService emailPaymentDetailsService;

    @Value("${sendgrid.api.key}")
    private String apiKey;

    @Value("${mail.from}")
    private String from;

    public void sendOrderConfirmation(Order order) {
        if (order.isContactByPhone()) {
            return;
        }

        if (order.getEmail() == null || order.getEmail().isBlank()) {
            return;
        }

        sendOrderConfirmationEmail(order);
    }

    public void resendOrderConfirmation(Order order) {
        if (order.getEmail() == null || order.getEmail().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "У замовлення не вказано email");
        }

        sendOrderConfirmationEmail(order);
    }

    private void sendOrderConfirmationEmail(Order order) {
        List<OrderItemView> items = buildItemViews(order);
        BigDecimal total = items.stream()
                .map(OrderItemView::lineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Context context = new Context();
        context.setVariable("order", order);
        context.setVariable("items", items);
        context.setVariable("total", total);
        context.setVariable("paymentDetails", emailPaymentDetailsService.get().getContent());

        String html = templateEngine.process("order-confirmation", context);
        String subject = "Підтвердження замовлення #" + order.getId();

        boolean success = send(order.getEmail(), subject, html);

        emailLogRepository.save(EmailLog.builder()
                .order(order)
                .recipient(order.getEmail())
                .subject(subject)
                .body(html)
                .success(success)
                .createdAt(LocalDateTime.now())
                .build());
    }

    private boolean send(String recipient, String subject, String html) {
        Email fromEmail = new Email(from);
        Email toEmail = new Email(recipient);

        Content content = new Content("text/html", html);
        Mail mail = new Mail(fromEmail, subject, toEmail, content);

        SendGrid sg = new SendGrid(apiKey);
        Request request = new Request();

        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");

            request.setBody(mail.build());

            Response response = sg.api(request);
            log.info("Email sent: {}", response.getStatusCode());

            if (response.getStatusCode() >= 400) {
                log.warn("SendGrid failed: status={}, body={}", response.getStatusCode(), response.getBody());
                return false;
            }

            return true;
        } catch (IOException e) {
            log.warn("Failed to send email via SendGrid", e);
            return false;
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