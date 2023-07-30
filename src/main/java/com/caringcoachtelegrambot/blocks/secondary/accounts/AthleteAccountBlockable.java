package com.caringcoachtelegrambot.blocks.secondary.accounts;

import com.caringcoachtelegrambot.blocks.parents.AccountBlockable;
import com.caringcoachtelegrambot.blocks.parents.Blockable;
import com.caringcoachtelegrambot.blocks.parents.SimpleBlockable;
import com.caringcoachtelegrambot.blocks.secondary.accounts.athletehelper.AthleteAccountInterface;
import com.caringcoachtelegrambot.blocks.secondary.accounts.athletehelper.AthleteHelper;
import com.caringcoachtelegrambot.blocks.secondary.accounts.athletehelper.EditingInterfaceAthlete;
import com.caringcoachtelegrambot.blocks.secondary.accounts.athletehelper.ReportAthleteInterface;
import com.caringcoachtelegrambot.exceptions.NotValidDataException;
import com.caringcoachtelegrambot.services.ServiceKeeper;
import com.caringcoachtelegrambot.utils.TelegramSender;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.KeyboardButton;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class AthleteAccountBlockable<H extends AthleteHelper> extends AccountBlockable<AthleteHelper> {

    private final List<AthleteAccountInterface> interfaces = new ArrayList<>();

    private final Blockable trainerAccountBlockable;

    private final AthleteAccountInterface<EditingInterfaceAthlete.EditingHelper> editInterface;

    private final AthleteAccountInterface<ReportAthleteInterface.ReportHelper> reportAthleteInterface;

    public AthleteAccountBlockable(TelegramSender telegramSender,
                                   ServiceKeeper serviceKeeper,
                                   TrainerAccountBlockable trainerAccountBlockable,
                                   @Qualifier("editingInterfaceAthlete") EditingInterfaceAthlete editInterface,
                                   @Qualifier("reportAthleteInterface") ReportAthleteInterface reportAthleteInterface) {
        super(telegramSender, serviceKeeper);
        this.trainerAccountBlockable = trainerAccountBlockable;
        this.reportAthleteInterface = reportAthleteInterface;
        this.editInterface = editInterface;
        interfaces.add(this.reportAthleteInterface);
        interfaces.add(this.editInterface);
    }

    @PostConstruct
    private void setUp() {
        reportAthleteInterface.setPrevBlockable(this);
        editInterface.setPrevBlockable(this);
        trainerAccountBlockable.setPrevBlockable(this);
        this.node().setNextBlockable(trainerAccountBlockable);
    }

    @Override
    public SendResponse process(Long chatId, Message message) {
        String txt = message.text();
        for (AthleteAccountInterface i : interfaces) {
            if (i.checkIn(chatId)) {
                return i.block(chatId, message);
            }
        }
        switch (txt) {
            case "Выйти из личного кабинета" -> {
                return goToBack(chatId);
            }
            case "Редактировать аккаунт" -> {
                return goTo(chatId, editInterface);
            }
            case "Связаться с тренером" -> {
                return goTo(chatId, null);
            }
            case "Памятка по питанию" -> {
                return goTo(chatId, null);
            }
            case "Отправить отчёт" -> {
                return goTo(chatId, reportAthleteInterface);
            }
            case "Оставить отзыв" -> {
                return goTo(chatId, null);
            }
        }
        throw new NotValidDataException();
    }

    private SendResponse goTo(Long chatId, Blockable blockable) {
        return blockable.uniqueStartBlockMessage(chatId);
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
