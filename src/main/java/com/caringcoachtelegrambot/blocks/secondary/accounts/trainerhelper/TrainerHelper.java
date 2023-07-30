package com.caringcoachtelegrambot.blocks.secondary.accounts.trainerhelper;

import com.caringcoachtelegrambot.blocks.parents.Blockable;
import com.caringcoachtelegrambot.blocks.secondary.accounts.AccountHelper;
import com.caringcoachtelegrambot.blocks.secondary.accounts.TrainerAccountBlockable;
import com.caringcoachtelegrambot.blocks.secondary.accounts.trainerhelper.interfaces.EditingInterface;
import com.caringcoachtelegrambot.blocks.secondary.accounts.trainerhelper.interfaces.FAQInterface;
import com.caringcoachtelegrambot.blocks.secondary.accounts.trainerhelper.interfaces.QuestionnaireInterface;
import com.caringcoachtelegrambot.blocks.secondary.accounts.trainerhelper.interfaces.TrainingPlanInterface;
import com.caringcoachtelegrambot.services.*;
import com.caringcoachtelegrambot.utils.TelegramSender;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.response.SendResponse;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TrainerHelper extends AccountHelper {

    private final Blockable trainerAccountBlockable;

    private final QuestionnaireInterface questionnaireInterface;

    private final FAQInterface faqInterface;

    private final TrainingPlanInterface trainingPlanInterface;

    private final EditingInterface editingInterface;

    private boolean checkingQuestionnaires;

    private boolean answeringFAQs;

    private boolean addingNewFaq;

    private boolean makesTrainingPlan;

    private boolean accountEditing;

    public TrainerHelper(TelegramSender sender,
                         ServiceKeeper serviceKeeper,
                         TrainerAccountBlockable trainerAccountBlockable) {
        super(serviceKeeper, sender);
        this.trainerAccountBlockable = trainerAccountBlockable;
        questionnaireInterface = new QuestionnaireInterface(this);
        trainingPlanInterface = new TrainingPlanInterface(this);
        faqInterface = new FAQInterface(this);
        editingInterface = new EditingInterface(this);
    }

    public final SendResponse checkQuestionnaire(Long chatId, String txt) {
        return questionnaireInterface.choseCheckQuestionnaireOrNot(chatId, txt);
    }

    public final SendResponse answerFAQ(Long chatId, String txt) {
        return faqInterface.answer(chatId, txt);
    }

    public SendResponse newFaq(Long chatId, String txt) {
        return faqInterface.newFaq(chatId, txt);
    }

    public SendResponse makeTrainingPlan(Long chatId, Message message) {
        return trainingPlanInterface.makeTrainingPlan(chatId, message);
    }

    public SendResponse editAccount(Long chatId, Message message) {
        return editingInterface.editAccount(chatId, message);
    }
}
