package com.caringcoachtelegrambot.blocks.parents;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import com.pengrad.telegrambot.response.SendResponse;

public interface Blockable {

    SendResponse block(Long chatId, Message message);

    boolean checkIn(Long chatId);

    void setPrevBlockable(Blockable blockable);

    SendResponse uniqueStartBlockMessage(Long chatId);

    ReplyKeyboardMarkup markup();
}