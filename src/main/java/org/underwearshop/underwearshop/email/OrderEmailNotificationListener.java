package org.underwearshop.underwearshop.email;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.underwearshop.underwearshop.event.OrderCreatedEvent;
import org.underwearshop.underwearshop.service.OrderService;

@Component
@RequiredArgsConstructor
public class OrderEmailNotificationListener {

    private final OrderService orderService;
    private final EmailService emailService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onOrderCreated(OrderCreatedEvent event) {
        orderService.findOne(event.orderId()).ifPresent(emailService::sendOrderConfirmation);
    }
}
