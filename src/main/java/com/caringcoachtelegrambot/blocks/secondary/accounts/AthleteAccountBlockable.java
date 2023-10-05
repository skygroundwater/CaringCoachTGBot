package com.caringcoachtelegrambot.blocks.secondary.accounts;

import com.caringcoachtelegrambot.blocks.parents.AccountBlockable;
import com.caringcoachtelegrambot.blocks.parents.Blockable;
import com.caringcoachtelegrambot.blocks.secondary.accounts.athletehelper.*;
import com.caringcoachtelegrambot.exceptions.NotValidDataException;
import com.caringcoachtelegrambot.services.keeper.ServiceKeeper;
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
public class AthleteAccountBlockable extends AccountBlockable<AthleteHelper> {

    private final List<AthleteAccountInterface<? extends AthleteHelper>> interfaces = new ArrayList<>();

    private final Blockable trainerAccountBlockable;

    private final AthleteAccountInterface<EditingInterfaceAthlete.EditingHelper> editInterface;

    private final AthleteAccountInterface<ReportAthleteInterface.ReportHelper> reportInterface;

    private final AthleteAccountInterface<DietaryGuideInterface.DietaryGuideHelper> dietaryInterface;

    public AthleteAccountBlockable(TelegramSender telegramSender,
                                   ServiceKeeper serviceKeeper,
                                   TrainerAccountBlockable trainerAccountBlockable,
                                   @Qualifier("editingInterfaceAthlete") EditingInterfaceAthlete editInterface,
                                   @Qualifier("reportAthleteInterface") ReportAthleteInterface reportInterface,
                                   @Qualifier("dietaryGuideInterface") AthleteAccountInterface<DietaryGuideInterface.DietaryGuideHelper> dietaryInterface) {
        super(telegramSender, serviceKeeper);
        this.trainerAccountBlockable = trainerAccountBlockable;
        this.reportInterface = reportInterface;
        this.editInterface = editInterface;
        this.dietaryInterface = dietaryInterface;
        interfaces.add(this.reportInterface);
        interfaces.add(this.editInterface);
        interfaces.add(this.dietaryInterface);
    }

    @PostConstruct
    private void setUp() {
        reportInterface.setPrevBlockable(this);
        editInterface.setPrevBlockable(this);
        trainerAccountBlockable.setPrevBlockable(this);
        dietaryInterface.setPrevBlockable(this);
        this.node().setNextBlockable(trainerAccountBlockable);
    }

    @Override
    public SendResponse process(Long chatId, Message message) {
        String txt = message.text();
        for (AthleteAccountInterface<? extends AthleteHelper> i : interfaces) {
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
                return msg(chatId, trainerService().getTrainer().getLink());
            }
            case "Памятка по питанию" -> {
                return goTo(chatId, dietaryInterface);
            }
            case "Отправить отчёт" -> {
                return goTo(chatId, reportInterface);
            }
            case "Оставить отзыв" -> {
                return msg(chatId, """
                        Вы можете оставить отзыв на сервисе авито по ссылке:
                                                
                        *https://www.avito.ru/sankt-peterburg/predlozheniya_uslug/fitnes_trener_onlayn_2617594100*
                        """);
            }
        }
        throw new NotValidDataException();
    }

    private SendResponse goTo(Long chatId, Blockable blockable) {
        return blockable.uniqueStartBlockMessage(chatId);
    }

    @Override
    public SendResponse uniqueStartBlockMessage(Long chatId) {
        helpers().put(chatId, new AthleteHelper(athleteService().findById(chatId)));
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
