package com.caringcoachtelegrambot.utils;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendAudio;
import com.pengrad.telegrambot.request.SendDocument;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.request.SendPhoto;
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

    public SendResponse sendResponse(SendPhoto sendPhoto) {
        return checkIsResponseOk(telegramBot.execute(sendPhoto));
    }

    public SendResponse sendResponse(SendDocument sendDocument) {
        return checkIsResponseOk(telegramBot.execute(sendDocument));
    }

    public SendResponse sendResponse(SendAudio sendAudio) {
        return checkIsResponseOk(telegramBot.execute(sendAudio));
    }

    private SendResponse checkIsResponseOk(SendResponse sendResponse) {
        if (!sendResponse.isOk()) {
            logger.error("Error during sending message: {}", sendResponse.message());
        }
        return sendResponse;
    }
}
