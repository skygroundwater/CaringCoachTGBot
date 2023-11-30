package com.caringcoachtelegrambot.blocks.parents;

import com.caringcoachtelegrambot.blocks.secondary.helpers.Helper;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import com.pengrad.telegrambot.response.SendResponse;

import java.util.List;

import static com.caringcoachtelegrambot.utils.Constants.BACK;

public interface Blockable {

    SendResponse block(Long chatId, Message message);

    boolean checkIn(Long chatId);

    void setPrevBlockable(Blockable blockable);

    SendResponse uniqueStartBlockMessage(Long chatId);

    default ReplyKeyboardMarkup backMarkup() {
        return new ReplyKeyboardMarkup(BACK);
    }

    List<String> buttons();

    <T extends Helper> void signIn(Long chatId, T helper);
}