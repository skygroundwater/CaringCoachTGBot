package com.caringcoachtelegrambot.blocks.secondary.accounts;

import com.caringcoachtelegrambot.blocks.secondary.helpers.accounthelpers.AthleteHelper;
import com.caringcoachtelegrambot.blocks.secondary.accounts.athleteinterfaces.EditingInterfaceAthlete;
import com.caringcoachtelegrambot.blocks.secondary.accounts.athleteinterfaces.ReportAthleteInterface;
import com.caringcoachtelegrambot.blocks.secondary.accounts.athleteinterfaces.DietaryGuideInterface;
import com.caringcoachtelegrambot.exceptions.NotValidDataException;
import com.caringcoachtelegrambot.services.keeper.ServiceKeeper;
import com.caringcoachtelegrambot.utils.TelegramSender;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.response.SendResponse;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AthleteAccountBlockable extends AccountBlockable<AthleteHelper> {

    private final AccountInterface<EditingInterfaceAthlete.EditingHelper> editingInterface;

    private final AccountInterface<ReportAthleteInterface.ReportHelper> reportInterface;

    private final AccountInterface<DietaryGuideInterface.DietaryGuideHelper> dietaryInterface;

    public AthleteAccountBlockable(TelegramSender telegramSender,
                                   ServiceKeeper serviceKeeper,
                                   EditingInterfaceAthlete editingInterface,
                                   ReportAthleteInterface reportInterface,
                                   DietaryGuideInterface dietaryInterface) {
        super(telegramSender, serviceKeeper);
        this.reportInterface = reportInterface;
        this.editingInterface = editingInterface;
        this.dietaryInterface = dietaryInterface;
    }

    @PostConstruct
    private void setUp() {
        reportInterface.setPrevBlockable(this);
        editingInterface.setPrevBlockable(this);
        dietaryInterface.setPrevBlockable(this);
    }

    @Override
    public SendResponse process(Long chatId, Message message) {
        String txt = message.text();
        switch (txt) {
            case "Выйти из личного кабинета" -> {
                return goToBack(chatId);
            }
            case "Редактировать аккаунт" -> {
                return goTo(chatId, editingInterface);
            }
            case "Связаться с тренером" -> {
                return msg(chatId, trainerService().findTrainerById(chatId).getLink());
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

    @Override
    public SendResponse uniqueStartBlockMessage(Long chatId) {
        signIn(chatId, new AthleteHelper(athleteService().findById(chatId)));
        return msg(chatId, "Ты в личном кабинете", markup());
    }

    @Override
    public List<String> buttons() {
        return List.of("Связаться с тренером",
                "Памятка по питанию",
                "Отправить отчёт",
                "Оставить отзыв");
    }
}