package com.caringcoachtelegrambot.blocks.secondary.tertiary.accounts.trainerhelper.interfaces;

import com.caringcoachtelegrambot.blocks.secondary.tertiary.accounts.trainerhelper.TrainerHelper;
import com.caringcoachtelegrambot.exceptions.NotFoundInDataBaseException;
import com.caringcoachtelegrambot.models.Questionnaire;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import lombok.Getter;
import lombok.Setter;

import static com.caringcoachtelegrambot.utils.Constants.BACK;

@Getter
@Setter
public class QuestionnaireInterface extends TrainerAccountInterface{


    private Questionnaire questionnaire;

    public QuestionnaireInterface(TrainerHelper helper) {
        super(helper);
    }

    public SendResponse choseCheckQuestionnaireOrNot(Long chatId, String txt) {
        if (!getHelper().isCheckingQuestionnaires()) {
            getHelper().setCheckingQuestionnaires(true);
            return sendQuestionnaire(chatId);
        }
        switch (txt) {
            case "Принять анкету" -> {
                return acceptQuestionnaire(chatId);
            }
            case "Отклонить анкету" -> {
                return deleteQuestionnaire(chatId);
            }
            case "Назад" -> {
                return stopCheckingQuestionnaire(chatId);
            }
        }
        return sender().sendResponse(new SendMessage(chatId, "Вы в блоке проверки анкет. " +
                "Если желаете выйти - жмите кнопку назад"));
    }

    private SendResponse stopCheckingQuestionnaire(Long chatId) {
        getHelper().setCheckingQuestionnaires(false);
        sender().sendResponse(new SendMessage(chatId, "Вы прервали проверку анкет"));
        return getHelper().getTrainerAccountBlockable().uniqueStartBlockMessage(chatId);
    }

    public SendResponse sendQuestionnaire(Long chatId) {
        try {

            questionnaire = questionnaireService().findNotCheckedQuestionnaire();
            String info = String.format("""
                            Анкета от %s %s.
                                                            
                            Рост: %s
                                                            
                            Вес: %s
                                                            
                            Возраст: %s
                                                            
                            Цели: %s
                                                            
                            Ограничения: %s
                                                            
                            Опыт: %s
                                                            
                            Питание: %s
                                                            
                            Оборудование: %s
                                                      
                            Предпочтения: %s""",
                    questionnaire.getFirstName(),
                    questionnaire.getSecondName(),
                    questionnaire.getHeight(),
                    questionnaire.getWeight(),
                    questionnaire.getAge(),
                    questionnaire.getTargetOfTrainings(),
                    questionnaire.getRestrictions(),
                    questionnaire.getExperience(),
                    questionnaire.getNutrition(),
                    questionnaire.getEquipment(),
                    questionnaire.getPreferences());
            return sender().sendResponse(new SendMessage(chatId, info)
                    .replyMarkup(markup()));
        } catch (NotFoundInDataBaseException e) {
            getHelper().setCheckingQuestionnaires(false);
            sender().sendResponse(new SendMessage(chatId, "На данный момент анкет нет"));
            return getHelper().getTrainerAccountBlockable().uniqueStartBlockMessage(chatId);
        }
    }

    private ReplyKeyboardMarkup markup() {
        return new ReplyKeyboardMarkup("Принять анкету")
                .addRow("Отклонить анкету")
                .addRow(BACK);
    }

    private SendResponse deleteQuestionnaire(Long chatId) {
        getHelper().getQuestionnaireService().deleteQuestionnaire(this.questionnaire);
        sender().sendResponse(new SendMessage(this.questionnaire.getId(),
                "Вашу анкету отклонил тренер. Вам лучше заполнить анкету заново"));
        sender().sendResponse(new SendMessage(chatId, String.format("Анкета от %s отклонена",
                questionnaire.getFirstName() + this.questionnaire.getSecondName())));
        this.questionnaire = null;
        return sendQuestionnaire(chatId);
    }

    private SendResponse acceptQuestionnaire(Long chatId) {
        this.questionnaire.setChecked(true);
        getHelper().getQuestionnaireService().putQuestionnaire(this.questionnaire);
        sender().sendResponse(new SendMessage(this.questionnaire.getId(),
                "Вашу анкету принял тренер. Теперь вы можете зарегистрироваться"));
        this.questionnaire = new Questionnaire();
        sender().sendResponse(new SendMessage(chatId, "Анкета проверена"));
        return sendQuestionnaire(chatId);
    }
}
