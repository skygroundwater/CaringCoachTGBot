package com.caringcoachtelegrambot.blocks.secondary.accounts.trainerinterfaces;

import com.caringcoachtelegrambot.blocks.secondary.accounts.AccountInterface;
import com.caringcoachtelegrambot.blocks.secondary.helpers.accounthelpers.TrainerHelper;
import com.caringcoachtelegrambot.exceptions.NotFoundInDataBaseException;
import com.caringcoachtelegrambot.exceptions.NotValidDataException;
import com.caringcoachtelegrambot.models.Questionnaire;
import com.caringcoachtelegrambot.models.Trainer;
import com.caringcoachtelegrambot.services.keeper.ServiceKeeper;
import com.caringcoachtelegrambot.utils.TelegramSender;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import com.pengrad.telegrambot.response.SendResponse;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class QuestionnaireInterface extends AccountInterface<QuestionnaireInterface.CheckQuestionnaireHelper> {

    public QuestionnaireInterface(TelegramSender sender,
                                  ServiceKeeper serviceKeeper) {
        super(sender, serviceKeeper);
    }

    @Getter
    @Setter
    public static class CheckQuestionnaireHelper extends TrainerHelper {

        private Questionnaire questionnaire;

        public CheckQuestionnaireHelper(Trainer trainer) {
            super(trainer);
        }
    }

    @Override
    public SendResponse uniqueStartBlockMessage(Long chatId) {
        signIn(chatId, new CheckQuestionnaireHelper(trainerService().findTrainerById(chatId)));
        return msg(chatId, "Ты в разделе работы с анкетами твоих будущих атлетов", markup());
    }

    @Override
    public List<String> buttons() {
        return List.of("Просмотреть входящие анкеты");
    }

    @Override
    protected SendResponse switching(Long chatId, Message message, CheckQuestionnaireHelper helper) {
        String txt = message.text();
        switch (txt) {
            case "Назад" -> {
                return goToBack(chatId);
            }
            case "Просмотреть входящие анкеты" -> {
                return sendQuestionnaire(chatId, helper);
            }
        }
        throw new NotValidDataException();
    }

    private ReplyKeyboardMarkup workMarkup() {
        return backMarkup()
                .addRow("Принять анкету")
                .addRow("Отклонить анкету");
    }

    @Override
    protected SendResponse work(Long chatId, Message message, CheckQuestionnaireHelper helper) {
        String txt = message.text();
        switch (txt) {
            case "Принять анкету" -> {
                return acceptQuestionnaire(chatId, helper.getQuestionnaire(), helper);
            }
            case "Отклонить анкету" -> {
                return deleteQuestionnaire(chatId, helper.getQuestionnaire(), helper);
            }
            case "Назад" -> {
                return forcedStop(chatId);
            }
        }
        return msg(chatId, "Вы в блоке проверки анкет. " +
                "Если желаете выйти - жмите кнопку назад");
    }

    public SendResponse sendQuestionnaire(Long chatId, CheckQuestionnaireHelper helper) {
        try {
            helper.setWorking(true);
            Questionnaire questionnaire = questionnaireService().findNotCheckedQuestionnaire();
            helper.setQuestionnaire(questionnaire);
            return msg(chatId, String.format("""
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
                    questionnaire.getPreferences()), workMarkup());
        } catch (NotFoundInDataBaseException e) {
            helper.setWorking(false);
            msg(chatId, "На данный момент анкет нет");
            return uniqueStartBlockMessage(chatId);
        }
    }

    private SendResponse deleteQuestionnaire(Long chatId, Questionnaire questionnaire, CheckQuestionnaireHelper helper) {
        questionnaireService().delete(questionnaire);
        msg(questionnaire.getId(),
                "Вашу анкету отклонил тренер. Вам лучше заполнить анкету заново");
        msg(chatId, String.format("Анкета от %s отклонена",
                questionnaire.getFirstName() + questionnaire.getSecondName()));
        helper.questionnaire = null;
        return sendQuestionnaire(chatId, helper);
    }

    private SendResponse acceptQuestionnaire(Long chatId, Questionnaire questionnaire, CheckQuestionnaireHelper helper) {
        questionnaire.setChecked(true);
        questionnaireService().put(questionnaire);
        msg(questionnaire.getId(),
                "Вашу анкету принял тренер. Теперь вы можете зарегистрироваться");
        helper.questionnaire = null;
        msg(chatId, "Анкета проверена");
        return sendQuestionnaire(chatId, helper);
    }
}