package com.caringcoachtelegrambot.blocks.parents;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import com.pengrad.telegrambot.response.SendResponse;

import static com.caringcoachtelegrambot.utils.Constants.BACK;

public interface Blockable {

    SendResponse block(Long chatId, Message message);

    boolean checkIn(Long chatId);

    void setPrevBlockable(Blockable blockable);

    SendResponse uniqueStartBlockMessage(Long chatId);

    ReplyKeyboardMarkup markup();

    default ReplyKeyboardMarkup backMarkup() {
        return new ReplyKeyboardMarkup(BACK);
    }
}