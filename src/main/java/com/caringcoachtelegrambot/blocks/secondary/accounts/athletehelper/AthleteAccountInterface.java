package com.caringcoachtelegrambot.blocks.secondary.accounts.athletehelper;

import com.caringcoachtelegrambot.blocks.parents.SimpleBlockable;
import com.caringcoachtelegrambot.services.keeper.ServiceKeeper;
import com.caringcoachtelegrambot.utils.TelegramSender;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;

import java.util.List;

public abstract class AthleteAccountInterface<H extends AthleteHelper> extends SimpleBlockable<H> {

    public AthleteAccountInterface(TelegramSender sender, ServiceKeeper serviceKeeper) {
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
        for (String button : buttons()) {
            markup.addRow(button);
        }
        return markup;
    }

    protected abstract List<String> buttons();

    protected abstract SendResponse switching(Long chatId, Message message, H helper);

    protected abstract SendResponse work(Long chatId, Message message, H helper);

    protected final SendResponse forcedStop(Long chatId) {
        helpers().remove(chatId);
        return uniqueStartBlockMessage(chatId);
    }

    protected final SendResponse intermediateMsg(Long chatId, String txt) {
        return sender().sendResponse(new SendMessage(chatId, txt)
                .replyMarkup(backMarkup()));
    }
}
