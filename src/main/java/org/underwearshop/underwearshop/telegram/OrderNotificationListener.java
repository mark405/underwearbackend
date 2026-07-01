package org.underwearshop.underwearshop.telegram;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.underwearshop.underwearshop.event.OrderCreatedEvent;
import org.underwearshop.underwearshop.event.OrderStatusChangedEvent;
import org.underwearshop.underwearshop.service.OrderService;

@Component
@RequiredArgsConstructor
public class OrderNotificationListener {

    private final OrderService orderService;
    private final ObjectProvider<OrderTelegramBot> botProvider;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onOrderCreated(OrderCreatedEvent event) {
        OrderTelegramBot bot = botProvider.getIfAvailable();
        if (bot == null) {
            return;
        }

        orderService.findOne(event.orderId()).ifPresent(order ->
                bot.broadcast(OrderMessageFormatter.formatOrder(order, "🆕 Нове замовлення"), OrderMessageFormatter.statusKeyboard(order.getId())));
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onOrderStatusChanged(OrderStatusChangedEvent event) {
        OrderTelegramBot bot = botProvider.getIfAvailable();
        if (bot == null) {
            return;
        }

        orderService.findOne(event.orderId()).ifPresent(order -> {
            String header = "🔄 Замовлення #" + event.orderId() + " змінило статус: "
                    + OrderMessageFormatter.statusLabel(event.oldStatus()) + " → " + OrderMessageFormatter.statusLabel(event.newStatus()) + "\n\n";
            bot.broadcast(header + OrderMessageFormatter.formatOrder(order, "Замовлення"), OrderMessageFormatter.statusKeyboard(order.getId()));
        });
    }
}
