package com.caringcoachtelegrambot.blocks.parents;

import com.caringcoachtelegrambot.blocks.secondary.helpers.Helper;
import com.caringcoachtelegrambot.exceptions.NotReturnedResponseException;
import com.caringcoachtelegrambot.services.ServiceKeeper;
import com.caringcoachtelegrambot.utils.TelegramSender;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import com.pengrad.telegrambot.response.SendResponse;
import jakarta.annotation.PostConstruct;
import lombok.Getter;

import java.util.*;

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