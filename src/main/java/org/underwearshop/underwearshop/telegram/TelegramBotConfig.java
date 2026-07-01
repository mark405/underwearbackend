package org.underwearshop.underwearshop.telegram;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import org.underwearshop.underwearshop.repository.TelegramSubscriberRepository;
import org.underwearshop.underwearshop.service.OrderService;

@Slf4j
@Configuration
public class TelegramBotConfig {

    @Value("${telegram.bot.token}")
    private String botToken;

    @Value("${telegram.bot.username}")
    private String botUsername;

    @Bean
    public OrderTelegramBot orderTelegramBot(OrderService orderService, TelegramSubscriberRepository subscriberRepository) {
        if (botToken == null || botToken.isBlank()) {
            log.warn("telegram.bot.token is not set - Telegram bot is disabled");
            return null;
        }

        OrderTelegramBot bot = new OrderTelegramBot(botToken, botUsername, orderService, subscriberRepository);

        try {
            new TelegramBotsApi(DefaultBotSession.class).registerBot(bot);
        } catch (TelegramApiException e) {
            throw new IllegalStateException("Failed to register Telegram bot", e);
        }

        return bot;
    }
}
