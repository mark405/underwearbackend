package org.underwearshop.underwearshop.telegram;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.underwearshop.underwearshop.entity.Order;
import org.underwearshop.underwearshop.service.OrderService;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PendingOrdersDigestScheduler {

    private final OrderService orderService;
    private final ObjectProvider<OrderTelegramBot> botProvider;

    @Scheduled(cron = "0 0 9 * * *")
    public void sendDailyPendingDigest() {
        OrderTelegramBot bot = botProvider.getIfAvailable();
        if (bot == null) {
            return;
        }

        List<Order> pending = orderService.findAllPending();

        if (pending.isEmpty()) {
            bot.broadcast("✅ Щоденний дайджест: немає замовлень в очікуванні.", null);
            return;
        }

        bot.broadcast("📋 Щоденний дайджест: " + pending.size() + " замовлення(-нь) в очікуванні:", null);
        for (Order order : pending) {
            bot.broadcast(OrderMessageFormatter.formatOrder(order, "Замовлення"), OrderMessageFormatter.statusKeyboard(order.getId()));
        }
    }
}
