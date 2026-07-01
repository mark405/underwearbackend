package org.underwearshop.underwearshop.telegram;

import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.underwearshop.underwearshop.entity.Order;
import org.underwearshop.underwearshop.entity.OrderStatus;
import org.underwearshop.underwearshop.entity.TelegramSubscriber;
import org.underwearshop.underwearshop.repository.TelegramSubscriberRepository;
import org.underwearshop.underwearshop.service.OrderService;

import java.util.List;

@Slf4j
public class OrderTelegramBot extends TelegramLongPollingBot {

    private final String botUsername;
    private final OrderService orderService;
    private final TelegramSubscriberRepository subscriberRepository;

    public OrderTelegramBot(String botToken, String botUsername, OrderService orderService,
                             TelegramSubscriberRepository subscriberRepository) {
        super(botToken);
        this.botUsername = botUsername;
        this.orderService = orderService;
        this.subscriberRepository = subscriberRepository;
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            handleMessage(update.getMessage());
        } else if (update.hasCallbackQuery()) {
            handleCallback(update.getCallbackQuery());
        }
    }

    void broadcast(String text, InlineKeyboardMarkup keyboard) {
        for (TelegramSubscriber subscriber : subscriberRepository.findAll()) {
            send(subscriber.getChatId(), text, keyboard);
        }
    }

    private void handleMessage(Message message) {
        Long chatId = message.getChatId();
        String command = message.getText().trim().split("\\s+")[0].split("@")[0].toLowerCase();

        switch (command) {
            case "/start" -> {
                if (!subscriberRepository.existsById(chatId)) {
                    subscriberRepository.save(new TelegramSubscriber(chatId));
                }
                send(chatId, "Привіт! 👋", null);
            }
            case "/pending" -> sendPendingOrders(chatId);
            default -> send(chatId, "Доступні команди: /start, /pending", null);
        }
    }

    private void sendPendingOrders(Long chatId) {
        List<Order> pending = orderService.findAllPending();

        if (pending.isEmpty()) {
            send(chatId, "Немає замовлень в очікуванні.", null);
            return;
        }

        for (Order order : pending) {
            send(chatId, OrderMessageFormatter.formatOrder(order, "Замовлення"), OrderMessageFormatter.statusKeyboard(order.getId()));
        }
    }

    private void handleCallback(CallbackQuery query) {
        String[] parts = query.getData().split(":");
        Long orderId = Long.parseLong(parts[1]);
        OrderStatus status = OrderStatus.valueOf(parts[2]);

        orderService.updateStatus(orderId, status).ifPresentOrElse(
                order -> {
                    editMessage(query, OrderMessageFormatter.formatOrder(order, "Замовлення"), OrderMessageFormatter.statusKeyboard(orderId));
                    answerCallback(query.getId(), "Статус оновлено: " + OrderMessageFormatter.statusLabel(status));
                },
                () -> answerCallback(query.getId(), "Замовлення не знайдено")
        );
    }

    private void editMessage(CallbackQuery query, String text, InlineKeyboardMarkup keyboard) {
        EditMessageText edit = new EditMessageText();
        edit.setChatId(query.getMessage().getChatId());
        edit.setMessageId(query.getMessage().getMessageId());
        edit.setText(text);
        edit.setReplyMarkup(keyboard);

        try {
            execute(edit);
        } catch (TelegramApiException e) {
            log.warn("Failed to edit Telegram message", e);
        }
    }

    private void answerCallback(String callbackQueryId, String text) {
        AnswerCallbackQuery answer = new AnswerCallbackQuery();
        answer.setCallbackQueryId(callbackQueryId);
        answer.setText(text);

        try {
            execute(answer);
        } catch (TelegramApiException e) {
            log.warn("Failed to answer Telegram callback query", e);
        }
    }

    private void send(Long chatId, String text, InlineKeyboardMarkup keyboard) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);
        if (keyboard != null) {
            message.setReplyMarkup(keyboard);
        }

        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.warn("Failed to send Telegram message to {}", chatId, e);
        }
    }
}
