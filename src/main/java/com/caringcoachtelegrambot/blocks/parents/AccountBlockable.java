package com.caringcoachtelegrambot.blocks.parents;

import com.caringcoachtelegrambot.blocks.secondary.helpers.Helper;
import com.caringcoachtelegrambot.utils.TelegramSender;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;

public abstract class AccountBlockable<T extends Helper> extends PaddedBlockable<T> {

    public AccountBlockable(TelegramSender telegramSender) {
        super(telegramSender);
    }

    @Override
    public final ReplyKeyboardMarkup markup() {
        return new ReplyKeyboardMarkup("Выйти из личного кабинета")
                .addRow("Редактировать аккаунт");
    }
}