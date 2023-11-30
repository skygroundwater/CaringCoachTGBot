package com.caringcoachtelegrambot.blocks.secondary.accounts;

import com.caringcoachtelegrambot.blocks.parents.SimpleBlockable;
import com.caringcoachtelegrambot.blocks.secondary.helpers.accounthelpers.AccountHelper;
import com.caringcoachtelegrambot.services.keeper.ServiceKeeper;
import com.caringcoachtelegrambot.utils.TelegramSender;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import com.pengrad.telegrambot.response.SendResponse;
import lombok.Getter;

import java.util.List;

@Getter
public abstract class AccountInterface<H extends AccountHelper> extends SimpleBlockable<H> {

    public AccountInterface(TelegramSender sender, ServiceKeeper serviceKeeper) {
        super(sender, serviceKeeper);
    }

    @Override
    public final SendResponse process(Long chatId, Message message) {
        H helper = helpers().get(chatId);
        if (helper.isWorking()) return work(chatId, message, helper);
        else return switching(chatId, message, helper);
    }

    @Override
    public final ReplyKeyboardMarkup markup() {
        ReplyKeyboardMarkup markup = backMarkup();
        buttons().forEach(markup::addRow);
        return markup;
    }

    public abstract List<String> buttons();

    protected abstract SendResponse switching(Long chatId, Message message, H helper);

    protected abstract SendResponse work(Long chatId, Message message, H helper);

    protected final SendResponse forcedStop(Long chatId) {
        helpers().get(chatId).setWorking(false);
        return uniqueStartBlockMessage(chatId);
    }
}