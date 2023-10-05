package com.caringcoachtelegrambot.blocks.parents;

import com.caringcoachtelegrambot.blocks.secondary.helpers.Helper;
import com.caringcoachtelegrambot.services.keeper.ServiceKeeper;
import com.caringcoachtelegrambot.utils.TelegramSender;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import lombok.Getter;

@Getter
public abstract class AccountBlockable<H extends Helper> extends PaddedBlockable<H> {

    public AccountBlockable(TelegramSender telegramSender,
                            ServiceKeeper serviceKeeper) {
        super(telegramSender, serviceKeeper);
    }

    @Override
    public final ReplyKeyboardMarkup markup() {
        return new ReplyKeyboardMarkup("Выйти из личного кабинета")
                .addRow("Редактировать аккаунт");
    }
}