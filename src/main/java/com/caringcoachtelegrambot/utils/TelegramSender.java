package com.caringcoachtelegrambot.utils;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class TelegramSender {

    private final TelegramBot telegramBot;

    private final Logger logger;

    public TelegramSender(TelegramBot telegramBot, Logger logger) {
        this.telegramBot = telegramBot;
        this.logger = logger;
    }

    public SendResponse sendResponse(SendMessage sendMessage) {
        sendMessage.parseMode(ParseMode.Markdown);
        return checkIsResponseOk(telegramBot.execute(sendMessage));
    }

    private SendResponse checkIsResponseOk(SendResponse sendResponse) {
        if (!sendResponse.isOk()) {
            logger.error("Error during sending message: {}", sendResponse.message());
        }
        return sendResponse;
    }
}