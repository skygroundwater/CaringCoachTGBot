package com.caringcoachtelegrambot.blocks.secondary.accounts.trainerinterfaces;

import com.caringcoachtelegrambot.blocks.secondary.accounts.AccountInterface;
import com.caringcoachtelegrambot.blocks.secondary.accounts.TrainerAccountBlockable;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import lombok.Getter;
import org.apache.kafka.common.network.Send;

import java.util.List;

@Getter
public abstract class TrainerAccountInterface extends AccountInterface<TrainerHelper> {

    public TrainerAccountInterface(TrainerHelper helper) {
        super(helper);
    }

    public final TrainerAccountBlockable trainerAccountBlockable() {
        return (TrainerAccountBlockable) getHelper().getTrainerAccountBlockable();
    }

    protected final SendResponse intermediateMsg(Long chatId, String txt) {
        return sender().sendResponse(new SendMessage(chatId, txt)
                .replyMarkup(backMarkup()));
    }

    protected final ReplyKeyboardMarkup markup(){
        ReplyKeyboardMarkup markup = backMarkup();
        buttons().forEach(markup::addRow);
        return markup;
    }

    protected abstract List<String> buttons();

    protected abstract SendResponse execute(Long chatId, Message message);

    protected final SendResponse msg(Long chatId, String txt) {
        return sender().sendResponse(new SendMessage(chatId, txt));
    }

    protected abstract SendResponse switching(Long chatId, Message message);

    protected  abstract SendResponse stop(Long chatId);
}
