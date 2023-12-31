package com.caringcoachtelegrambot.blocks.parents;

import com.caringcoachtelegrambot.blocks.secondary.helpers.Helper;
import com.caringcoachtelegrambot.utils.TelegramSender;
import com.pengrad.telegrambot.response.SendResponse;
import lombok.Setter;

@Setter
public abstract class PaddedBlockable<T extends Helper> extends SimpleBlockable<T> implements BlockableForNextStep {

    public PaddedBlockable(TelegramSender telegramSender) {
        super(telegramSender);
    }

    @Override
    public final SendResponse goToNext(Long chatId) {
        try {
            this.helpers().get(chatId).setIn(false);
        } catch (NullPointerException e) {
            return node().getNextBlockable().uniqueStartBlockMessage(chatId);
        }
        return node().getNextBlockable().uniqueStartBlockMessage(chatId);
    }

    @Override
    public final SendResponse jumpUnderHead(Long chatId) {
        return goToNext(chatId);
    }

    @Override
    public final SendResponse goDownTwoSteps(Long chatId){
        return goToBack(chatId);
    }
}
