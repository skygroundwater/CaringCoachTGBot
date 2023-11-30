package com.caringcoachtelegrambot.blocks.secondary;

import com.caringcoachtelegrambot.blocks.parents.SimpleBlockable;
import com.caringcoachtelegrambot.blocks.secondary.helpers.Helper;
import com.caringcoachtelegrambot.exceptions.NotValidDataException;
import com.caringcoachtelegrambot.models.Questionnaire;
import com.caringcoachtelegrambot.services.keeper.ServiceKeeper;
import com.caringcoachtelegrambot.utils.TelegramSender;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.caringcoachtelegrambot.utils.Constants.BACK;


@Component
public class QuestionnaireRecordingBlockable extends SimpleBlockable<QuestionnaireRecordingBlockable.QuestionnaireHelper> {

    public QuestionnaireRecordingBlockable(TelegramSender telegramSender,
                                           ServiceKeeper serviceKeeper) {
        super(telegramSender, serviceKeeper);
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class QuestionnaireHelper extends Helper {

        private Questionnaire questionnaire;

        private boolean started;

        public QuestionnaireHelper() {
            super();
            questionnaire = new Questionnaire();
            started = false;
        }
    }

    @Override
    public SendResponse process(Long chatId, Message message) {
        QuestionnaireHelper helper = helpers().get(chatId);
        String info = message.text();
        if (info != null && !info.isEmpty()) {
            if (!helper.isStarted()) {
                switch (info) {
                    case "Начать заполнение анкеты" -> {
                        return startFillingOutTheQuestionnaire(chatId, helper);
                    }
                    case "Назад" -> {
                        return goToBack(chatId);
                    }
                }
            } else return fillingOutTheQuestionnaire(chatId, info, helper);
        }
        throw new NotValidDataException();
    }

    @Override
    public SendResponse uniqueStartBlockMessage(Long chatId) {
        signIn(chatId, new QuestionnaireHelper());
        return msg(chatId, """
                Вы находитесь перед стартом заполнения своей анкеты.
                Пожалуйста, чем подробнее ответите, тем точнее и грамотнее
                мне удастся составить для Вас тренировочный план 🌸
                Заранее спасибо за развёрнутые ответы!
                """, markup());
    }

    @Override
    public List<String> buttons() {
        return List.of("Начать заполнение анкеты");
    }

    private SendResponse startFillingOutTheQuestionnaire(Long chatId, QuestionnaireHelper helper) {
        helper.getQuestionnaire().setId(chatId);
        helper.setStarted(true);
        return msg(chatId, "Введите ваше имя", new ReplyKeyboardMarkup(STOP_THE_FILL));
    }

    private SendResponse fillingOutTheQuestionnaire(Long chatId, String info, QuestionnaireHelper helper) {
        if (!forcedStopFillingOut(chatId, info)) {
            Questionnaire quest = helper.questionnaire;
            if (quest != null) {
                if (quest.getFirstName() == null) {
                    quest.setFirstName(info);
                    return msg(chatId, "Введите фамилию");
                } else if (quest.getSecondName() == null) {
                    quest.setSecondName(info);
                    return msg(chatId, "Введите возраст");
                } else if (quest.getAge() == null) {
                    quest.setAge(info);
                    return msg(chatId, "Введите ваш рост");
                } else if (quest.getHeight() == null) {
                    quest.setHeight(info);
                    return msg(chatId, "Укажите ваш вес");
                } else if (quest.getWeight() == null) {
                    quest.setWeight(info);
                    return msg(chatId, """
                            Отлично! Теперь расскажите про ваши цели, которых хотите достичь в тренировках
                            """);
                } else if (quest.getTargetOfTrainings() == null) {
                    quest.setTargetOfTrainings(info);
                    return msg(chatId, "Расскажите какие ограничения у вас есть, в том числе, по здоровью");
                } else if (quest.getRestrictions() == null) {
                    quest.setRestrictions(info);
                    return msg(chatId, "Укажите какой опыт у вас уже имеется");
                } else if (quest.getExperience() == null) {
                    quest.setExperience(info);
                    return msg(chatId, "Расскажите про свой актуальный рацион питания");
                } else if (quest.getNutrition() == null) {
                    quest.setNutrition(info);
                    return msg(chatId, "Расскажите каким оборудованием для тренировок вф располагаете");
                } else if (quest.getEquipment() == null) {
                    quest.setEquipment(info);
                    return msg(chatId, "Последнее! Какие предпочтения у вас");
                } else if (quest.getPreferences() == null) {
                    quest.setPreferences(info);
                    questionnaireService().post(quest);
                    stopFillingOut(chatId);
                    msg(chatId, "Ваша анкета принята!");
                    return goToBack(chatId);
                }
            }
            return msg(chatId, "Какой-то косяк");
        }
        return msg(chatId, "Заполнение анкеты было прервано. В следующий раз придется начать заново");
    }

    private static final String STOP_THE_FILL = "Остановить отправку анкеты";

    private boolean forcedStopFillingOut(Long chatId, String info) {
        if (info.equals(STOP_THE_FILL)) {
            stopFillingOut(chatId);
            return true;
        } else return false;
    }

    private void stopFillingOut(Long chatId) {
        helpers().get(chatId).setStarted(false);
        helpers().get(chatId).setQuestionnaire(new Questionnaire());
    }
}
