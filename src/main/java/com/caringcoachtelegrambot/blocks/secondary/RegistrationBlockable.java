package com.caringcoachtelegrambot.blocks.secondary;

import com.caringcoachtelegrambot.blocks.parents.PaddedBlockable;
import com.caringcoachtelegrambot.blocks.secondary.helpers.Helper;
import com.caringcoachtelegrambot.enums.Role;
import com.caringcoachtelegrambot.exceptions.NotFoundInDataBaseException;
import com.caringcoachtelegrambot.exceptions.NotValidDataException;
import com.caringcoachtelegrambot.models.Athlete;
import com.caringcoachtelegrambot.models.Questionnaire;
import com.caringcoachtelegrambot.services.AthleteService;
import com.caringcoachtelegrambot.services.QuestionnaireService;
import com.caringcoachtelegrambot.services.ServiceKeeper;
import com.caringcoachtelegrambot.utils.TelegramSender;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class RegistrationBlockable extends PaddedBlockable<RegistrationBlockable.RegistrationHelper> {

    private final PasswordEncoder encoder;

    public RegistrationBlockable(TelegramSender telegramSender,
                                 ServiceKeeper serviceKeeper,
                                 PasswordEncoder encoder) {
        super(telegramSender, serviceKeeper);
        this.encoder = encoder;
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class RegistrationHelper extends Helper {
        private String login;
        private String password;
        private Questionnaire questionnaire;

        public RegistrationHelper() {
        }

        public RegistrationHelper build(Questionnaire questionnaire) {
            this.setQuestionnaire(questionnaire);
            return this;
        }
    }

    @Override
    public SendResponse process(Long chatId, Message message) {
        RegistrationHelper helper = helpers().get(chatId);
        String txt = message.text();
        if (helper != null) {
            if (!txt.equals("Прервать регистрацию")) {
                if (helper.getLogin() == null) {
                    helper.setLogin(txt);
                    return sender().sendResponse(new SendMessage(chatId, "Теперь следующим сообщением введите свой пароль")
                            .replyMarkup(markup()));
                } else if (helper.getPassword() == null) {
                    helper.setPassword(txt);
                    return sender().sendResponse(new SendMessage(chatId, "Следующим сообщением отправьте повторно ваш пароль для подтверждения")
                            .replyMarkup(markup()));
                } else return registerAthlete(chatId, helper, txt);
            } else return forcedStopRegistration(chatId);
        }
        throw new NotValidDataException();
    }

    private SendResponse forcedStopRegistration(Long chatId) {
        helpers().remove(chatId);
        sender().sendResponse(new SendMessage(chatId, "Вы прервали регистрацию"));
        return node().getPrevBlockable().uniqueStartBlockMessage(chatId);
    }


    private SendResponse registerAthlete(Long chatId, RegistrationHelper helper, String txt) {
        if (txt.equals(helper.getPassword())) {
            Questionnaire questionnaire = helper.getQuestionnaire();
            athleteService().post(
                    Athlete.builder()
                            .id(questionnaire.getId())
                            .role(Role.ROLE_ATHLETE)
                            .login(helper.getLogin())
                            .password(encoder.encode(helper.getPassword()))
                            .build().buildParameters(
                                    questionnaire.getFirstName(),
                                    questionnaire.getSecondName(),
                                    questionnaire.getAge(),
                                    questionnaire.getHeight(),
                                    questionnaire.getWeight()));
            questionnaire.setRegistered(true);
            questionnaireService().put(questionnaire);
            helpers().remove(chatId);
            sender().sendResponse(new SendMessage(chatId, "Bы зарегистрированы, теперь можете авторизоваться в личном кабинете."));
            return node().getPrevBlockable().uniqueStartBlockMessage(chatId);
        } else return sender().sendResponse(new SendMessage(chatId, "Пароли не совпадают. Повторите."));
    }

    @Override
    public SendResponse uniqueStartBlockMessage(Long chatId) {
        Questionnaire questionnaire;
        try {
            questionnaire = questionnaireService().findById(chatId);
        } catch (NotFoundInDataBaseException e) {
            sender().sendResponse(new SendMessage(chatId, """
                    На данный момент вы пока не отправляли заполненную анкету.
                    Сначала необходимо сделать это.
                    """));
            return node().getPrevBlockable().uniqueStartBlockMessage(chatId);
        }
        if (!questionnaire.isChecked()) {
            sender().sendResponse(new SendMessage(chatId, """
                    Вы отправили анкету, но она пока не проверена тренером.
                    Это нужно для того, чтобы не бежать впереди паровоза.
                    """));
            return node().getPrevBlockable().uniqueStartBlockMessage(chatId);
        }
        if (questionnaire.isRegistered()) {
            sender().sendResponse(new SendMessage(chatId, "Вы уже зарегистрированы и можете авторизоваться в личном кабинете."));
            return node().getPrevBlockable().uniqueStartBlockMessage(chatId);
        }
        helpers().put(chatId, new RegistrationHelper().build(questionnaire));
        return sender().sendResponse(new SendMessage(chatId, """
                Вы допущены к регистрации. Для начала вам необходимо ввести свой логин.
                Отправьте его следующим сообщением. Можно использовать как кириллицу,
                так и латинницу.
                """));
    }

    @Override
    public ReplyKeyboardMarkup markup() {
        return new ReplyKeyboardMarkup("Прервать регистрацию");
    }
}
