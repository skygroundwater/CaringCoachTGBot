package com.caringcoachtelegrambot.blocks.secondary.tertiary.accounts;

import com.caringcoachtelegrambot.blocks.parents.AccountBlockable;
import com.caringcoachtelegrambot.blocks.parents.Blockable;
import com.caringcoachtelegrambot.blocks.secondary.helpers.AthleteHelper;
import com.caringcoachtelegrambot.exceptions.NotValidDataException;
import com.caringcoachtelegrambot.utils.TelegramSender;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.KeyboardButton;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AthleteAccountBlockable extends AccountBlockable<AthleteHelper> {

    private final Blockable trainerAccountBlockable;

    public AthleteAccountBlockable(TelegramSender telegramSender,
                                   TrainerAccountBlockable trainerAccountBlockable) {
        super(telegramSender);
        this.trainerAccountBlockable = trainerAccountBlockable;
    }

    @PostConstruct
    private void setUp() {
        trainerAccountBlockable.setPrevBlockable(this);
        this.node().setNextBlockable(trainerAccountBlockable);
    }


    @Override
    public SendResponse process(Long chatId, Message message) {
        String txt = message.text();

        switch (txt) {
            case "Выйти из личного кабинета" -> {
                return goToBack(chatId);
            }
            case "Редактировать аккаунт" -> {

            }
            case "Связаться с тренером" -> {
                return sender().sendResponse(new SendMessage(chatId, "@tvoytrainer"));
            }
            case "Памятка по питанию" -> {

            }
            case "Отправить отчёт" -> {

            }
            case "Оставить отзыв" -> {

            }


        }
        throw new NotValidDataException();
    }

    @Override
    public SendResponse uniqueStartBlockMessage(Long chatId) {
        helpers().put(chatId, new AthleteHelper());
        ReplyKeyboardMarkup markup = markup();
        for (KeyboardButton button : buttonsForAthlete()) {
            markup.addRow(button);
        }
        return sender().sendResponse(new SendMessage(chatId, "Ты в личном кабинете")
                .replyMarkup(markup));
    }

    private List<KeyboardButton> buttonsForAthlete() {
        return List.of(
                new KeyboardButton("Связаться с тренером"),
                new KeyboardButton("Памятка по питанию"),
                new KeyboardButton("Отправить отчёт"),
                new KeyboardButton("Оставить отзыв"));
    }


}
