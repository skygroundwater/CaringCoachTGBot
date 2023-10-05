package com.caringcoachtelegrambot.blocks.secondary;

import com.caringcoachtelegrambot.blocks.parents.PaddedBlockable;
import com.caringcoachtelegrambot.blocks.secondary.helpers.Helper;
import com.caringcoachtelegrambot.blocks.secondary.accounts.AthleteAccountBlockable;
import com.caringcoachtelegrambot.exceptions.NotFoundInDataBaseException;
import com.caringcoachtelegrambot.models.Athlete;
import com.caringcoachtelegrambot.models.Trainer;
import com.caringcoachtelegrambot.services.keeper.ServiceKeeper;
import com.caringcoachtelegrambot.utils.TelegramSender;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Getter
public class AuthorizationBlockable extends PaddedBlockable<AuthorizationBlockable.AuthorizationHelper> {

    private final PaddedBlockable<? extends Helper> athleteAccountBlockable;

    public AuthorizationBlockable(TelegramSender telegramSender,
                                  ServiceKeeper serviceKeeper,
                                  AthleteAccountBlockable athleteAccountBlockable) {
        super(telegramSender, serviceKeeper);
        this.athleteAccountBlockable = athleteAccountBlockable;
    }

    private PasswordEncoder encoder() {
        return trainerService().getEncoder();
    }

    @PostConstruct
    public void setUp() {
        node().setNextBlockable(athleteAccountBlockable);
        athleteAccountBlockable.setPrevBlockable(this);
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class AuthorizationHelper extends Helper {

        Trainer trainer;

        private Athlete athlete;

        private String login;

        private String password;

        public AuthorizationHelper(Trainer trainer) {
            this.trainer = trainer;
        }

        public void clear() {
            login = null;
            password = null;
        }
    }

    @Override
    public SendResponse process(Long chatId, Message message) {
        AuthorizationHelper helper = helpers().get(chatId);
        String txt = message.text();
        if (txt.equals("Прервать авторизацию")) return goToBack(chatId);
        return authorization(chatId, txt, helper);
    }

    private SendResponse authorization(Long chatId, String txt, AuthorizationHelper helper) {
        if (helper.getLogin() == null) {
            helper.setLogin(txt);
            return sender().sendResponse(new SendMessage(chatId, "Следующим сообщением введите пароль"));
        } else if (helper.getPassword() == null) {
            helper.setPassword(txt);
            if (chatId.equals(helper.getTrainer().getId())) {
                return trainerFunctional(chatId, helper);
            } else {
                return athleteFunctional(chatId, helper);
            }
        }
        return sender().sendResponse(new SendMessage(chatId, "Какой-то косяк"));
    }

    private SendResponse trainerFunctional(Long chatId, AuthorizationHelper helper) {
        if (helper.getTrainer().getLogin().equals(helper.getLogin())) {
            if (encoder().matches(helper.getPassword(), helper.getTrainer().getPassword())) {
                helper.setIn(false);
                return athleteAccountBlockable.jumpUnderHead(chatId);
            }
        }
        helper.clear();
        return sender().sendResponse(new SendMessage(chatId, "Логин или пароль не совпадают. Повторите операцию")
                .replyMarkup(markup()));
    }

    private SendResponse athleteFunctional(Long chatId, AuthorizationHelper helper) {
        Athlete athlete;
        try {
            athlete = athleteService().findById(chatId);
            helper.setAthlete(athlete);
            if (athlete.getLogin().equals(helper.getLogin()) && encoder().matches(helper.getPassword(), athlete.getPassword())) {
                helper.setIn(false);
                sender().sendResponse(new SendMessage(chatId, "Вы авторизованы"));
                return goToNext(chatId);
            } else {
                helper.clear();
                return sender().sendResponse(new SendMessage(chatId, "Логин или пароль не совпадают. Повторите операцию")
                        .replyMarkup(markup()));
            }
        } catch (NotFoundInDataBaseException e) {
            return sender().sendResponse(new SendMessage(chatId, "Вы еще не зарегестрированы. Пройдите этап регистрации"));
        }
    }

    @Override
    public SendResponse uniqueStartBlockMessage(Long chatId) {
        helpers().put(chatId, new AuthorizationHelper(trainerService().getTrainer()));
        AuthorizationHelper helper = helpers().get(chatId);
        if (chatId.equals(helper.getTrainer().getId())) {
            return sender().sendResponse(new SendMessage(chatId, "Привет, Пупсеячка)) Введи свой логин)")
                    .replyMarkup(markup()));
        }
        return sender().sendResponse(new SendMessage(chatId, "Введите ваш логин")
                .replyMarkup(markup()));
    }

    @Override
    public ReplyKeyboardMarkup markup() {
        return new ReplyKeyboardMarkup("Прервать авторизацию");
    }
}