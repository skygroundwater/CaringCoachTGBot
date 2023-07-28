package com.caringcoachtelegrambot.blocks.secondary;

import com.caringcoachtelegrambot.blocks.parents.SimpleBlockable;
import com.caringcoachtelegrambot.blocks.secondary.helpers.Helper;
import com.caringcoachtelegrambot.exceptions.NotValidDataException;
import com.caringcoachtelegrambot.models.Questionnaire;
import com.caringcoachtelegrambot.services.QuestionnaireService;
import com.caringcoachtelegrambot.utils.TelegramSender;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.stereotype.Component;

import static com.caringcoachtelegrambot.utils.Constants.BACK;


@Component
public class QuestionnaireRecordingBlockable extends SimpleBlockable<QuestionnaireRecordingBlockable.QuestionnaireHelper> {

    private final QuestionnaireService questionnaireService;

    public QuestionnaireRecordingBlockable(TelegramSender telegramSender, QuestionnaireService questionnaireService) {
        super(telegramSender);
        this.questionnaireService = questionnaireService;
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
                        return startFillingOutTheQuestionnaire(chatId);
                    }
                    case "Назад" -> {
                        return goToBack(chatId);
                    }
                }
            } else return fillingOutTheQuestionnaire(chatId, info);
        }
        throw new NotValidDataException();
    }

    @Override
    public SendResponse uniqueStartBlockMessage(Long chatId) {
        helpers().put(chatId, new QuestionnaireHelper());
        return sender().sendResponse(new SendMessage(chatId, """
                Вы находитесь перед стартом заполнения своей анкеты.
                Пожалуйста, чем подробнее ответите, тем точнее и грамотнее
                мне удастся составить для Вас тренировочный план 🌸
                Заранее спасибо за развёрнутые ответы!
                """).replyMarkup(markup()));
    }

    @Override
    public ReplyKeyboardMarkup markup() {
        return new ReplyKeyboardMarkup("Начать заполнение анкеты")
                .addRow(BACK).oneTimeKeyboard(true);
    }

    private SendResponse startFillingOutTheQuestionnaire(Long chatId) {
        helpers().forEach((aLong, questionnaireHelper) -> {
            if (aLong.equals(chatId)) {
                questionnaireHelper.getQuestionnaire().setId(chatId);
                questionnaireHelper.setStarted(true);
            }
        });
        return sender().sendResponse(new SendMessage(chatId, "Введите ваше имя")
                .replyMarkup(new ReplyKeyboardMarkup(STOP_THE_FILL)));
    }

    private SendResponse fillingOutTheQuestionnaire(Long chatId, String info) {
        if (!forcedStopFillingOut(chatId, info)) {
            Questionnaire quest = helpers().get(chatId).getQuestionnaire();
            if (quest != null) {
                if (quest.getFirstName() == null) {
                    quest.setFirstName(info);
                    return continueFilling(chatId, "Введите фамилию");
                } else if (quest.getSecondName() == null) {
                    quest.setSecondName(info);
                    return continueFilling(chatId, "Введите возраст");
                } else if (quest.getAge() == null) {
                    quest.setAge(info);
                    return continueFilling(chatId, "Введите ваш рост");
                } else if (quest.getHeight() == null) {
                    quest.setHeight(info);
                    return continueFilling(chatId, "Укажите ваш вес");
                } else if (quest.getWeight() == null) {
                    quest.setWeight(info);
                    return continueFilling(chatId, """
                            Отлично! Теперь расскажите про ваши цели, которых хотите достичь в тренировках
                            """);
                } else if (quest.getTargetOfTrainings() == null) {
                    quest.setTargetOfTrainings(info);
                    return continueFilling(chatId, "Расскажите какие ограничения у вас есть, в том числе, по здоровью");
                } else if (quest.getRestrictions() == null) {
                    quest.setRestrictions(info);
                    return continueFilling(chatId, "Укажите какой опыт у вас уже имеется");
                } else if (quest.getExperience() == null) {
                    quest.setExperience(info);
                    return continueFilling(chatId, "Расскажите про свой актуальный рацион питания");
                } else if (quest.getNutrition() == null) {
                    quest.setNutrition(info);
                    return continueFilling(chatId, "Расскажите каким оборудованием для тренировок вф располагаете");
                } else if (quest.getEquipment() == null) {
                    quest.setEquipment(info);
                    return continueFilling(chatId, "Последнее! Какие предпочтения у вас");
                } else if (quest.getPreferences() == null) {
                    quest.setPreferences(info);
                    questionnaireService.postQuestionnaire(quest);
                    stopFillingOut(chatId);
                    continueFilling(chatId, "Ваша анкета принята!");
                    return goToBack(chatId);
                }
            }
            return sender().sendResponse(new SendMessage(chatId, "Какой-то косяк"));
        }
        return sender().sendResponse(new SendMessage(chatId, "Заполнение анкеты было прервано. В следующий раз придется начать заново"));
    }

    private static final String STOP_THE_FILL = "Остановить отправку отчета";

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

    private SendResponse continueFilling(Long chatId, String messageText) {
        return sender().sendResponse(new SendMessage(chatId, messageText));
    }
}
