package com.caringcoachtelegrambot.blocks.secondary;

import com.caringcoachtelegrambot.blocks.parents.PaddedBlockable;
import com.caringcoachtelegrambot.blocks.secondary.accounts.AccountBlockable;
import com.caringcoachtelegrambot.blocks.secondary.accounts.TrainerAccountBlockable;
import com.caringcoachtelegrambot.blocks.secondary.helpers.accounthelpers.AthleteHelper;
import com.caringcoachtelegrambot.blocks.secondary.helpers.accounthelpers.TrainerHelper;
import com.caringcoachtelegrambot.blocks.secondary.helpers.Helper;
import com.caringcoachtelegrambot.blocks.secondary.accounts.AthleteAccountBlockable;
import com.caringcoachtelegrambot.exceptions.NotFoundInDataBaseException;
import com.caringcoachtelegrambot.models.Athlete;
import com.caringcoachtelegrambot.models.Trainer;
import com.caringcoachtelegrambot.services.keeper.ServiceKeeper;
import com.caringcoachtelegrambot.utils.TelegramSender;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.response.SendResponse;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.caringcoachtelegrambot.utils.Constants.BACK;

@Component
@Getter
public class AuthorizationBlockable extends PaddedBlockable<AuthorizationBlockable.AuthorizationHelper> {

    private final AccountBlockable<AthleteHelper> athleteAccountBlockable;

    private final AccountBlockable<TrainerHelper> trainerAccountBlockable;

    public AuthorizationBlockable(TelegramSender telegramSender,
                                  ServiceKeeper serviceKeeper,
                                  AthleteAccountBlockable athleteAccountBlockable,
                                  TrainerAccountBlockable trainerAccountBlockable) {
        super(telegramSender, serviceKeeper);
        this.athleteAccountBlockable = athleteAccountBlockable;
        this.trainerAccountBlockable = trainerAccountBlockable;
    }

    private PasswordEncoder encoder() {
        return trainerService().getEncoder();
    }

    @PostConstruct
    public void setUp() {
        node().setNextBlockable(athleteAccountBlockable);
        athleteAccountBlockable.setPrevBlockable(this);
        trainerAccountBlockable.setPrevBlockable(this);
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class AuthorizationHelper extends Helper {

        private Trainer trainer;

        private Athlete athlete;

        private String login;

        private String password;

        public void clear() {
            login = null;
            password = null;
        }
    }

    @Override
    public SendResponse process(Long chatId, Message message) {
        AuthorizationHelper helper = helpers().get(chatId);
        String txt = message.text();
        if (txt.equals(BACK)) return goToBack(chatId);
        return authorization(chatId, txt, helper);
    }

    private SendResponse authorization(Long chatId, String txt, AuthorizationHelper helper) {
        if (helper.getLogin() == null) {
            helper.setLogin(txt);
            return msg(chatId, "Следующим сообщением введите пароль");
        } else if (helper.getPassword() == null) {
            helper.setPassword(txt);
            if (chatId.equals(helper.getTrainer().getId())) {
                return trainerFunctional(chatId, helper);
            } else {
                return athleteFunctional(chatId, helper);
            }
        }
        return msg(chatId, "Какой-то косяк");
    }

    private SendResponse trainerFunctional(Long chatId, AuthorizationHelper helper) {
        if (helper.getTrainer().getLogin().equals(helper.getLogin())) {
            if (encoder().matches(helper.getPassword(), helper.getTrainer().getPassword())) {
                helper.setIn(false);
                return goTo(chatId, trainerAccountBlockable);
            }
        }
        helper.clear();
        return msg(chatId, "Логин или пароль не совпадают. Повторите операцию", markup());
    }

    private SendResponse athleteFunctional(Long chatId, AuthorizationHelper helper) {
        Athlete athlete;
        try {
            athlete = athleteService().findById(chatId);
            helper.setAthlete(athlete);
        } catch (NotFoundInDataBaseException e) {
            return msg(chatId, "Вы еще не зарегестрированы. Пройдите этап регистрации");
        }
        if (athlete.getLogin().equals(helper.getLogin()) && encoder().matches(helper.getPassword(), athlete.getPassword())) {
            helper.setIn(false);
            msg(chatId, "Вы авторизованы");
            return goTo(chatId, athleteAccountBlockable);
        } else {
            helper.clear();
            return msg(chatId, "Логин или пароль не совпадают. Повторите операцию", markup());
        }
    }

    @Override
    public SendResponse uniqueStartBlockMessage(Long chatId) {
        Trainer trainer;
        AuthorizationHelper helper = new AuthorizationHelper();
        try {
            trainer = trainerService().findTrainerById(chatId);
            helper.setAthlete(athleteService().findById(chatId));
            signIn(chatId, helper);
            return msg(chatId, "Привет, Пупсеячка)) Введи свой логин)", markup());
        } catch (NotFoundInDataBaseException e) {
            helper.setTrainer(trainerService().findTrainerByAthleteId(chatId));
            signIn(chatId, helper);
            return msg(chatId, "Введите ваш логин", markup());
        }
    }

    @Override
    public List<String> buttons() {
        return List.of();
    }
}