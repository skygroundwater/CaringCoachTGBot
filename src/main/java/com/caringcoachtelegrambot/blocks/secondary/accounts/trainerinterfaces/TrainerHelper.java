package com.caringcoachtelegrambot.blocks.secondary.accounts.trainerinterfaces;

import com.caringcoachtelegrambot.blocks.parents.Blockable;
import com.caringcoachtelegrambot.blocks.secondary.accounts.AccountHelper;
import com.caringcoachtelegrambot.blocks.secondary.accounts.TrainerAccountBlockable;
import com.caringcoachtelegrambot.services.keeper.ServiceKeeper;
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

    private final ReportCheckingInterface reportCheckingInterface;

    private boolean checkingQuestionnaires;

    private boolean checkingReports;

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
        reportCheckingInterface = new ReportCheckingInterface(this);

    }

    public final SendResponse checkQuestionnaire(Long chatId, Message message) {
        return questionnaireInterface.execute(chatId, message);
    }

    public final SendResponse answerFAQ(Long chatId, Message message) {
        return faqInterface.execute(chatId, message);
    }

    public SendResponse newFaq(Long chatId, String string) {
        return faqInterface.newFaq(chatId, string);
    }

    public SendResponse makeTrainingPlan(Long chatId, Message message) {
        return trainingPlanInterface.execute(chatId, message);
    }

    public SendResponse editAccount(Long chatId, Message message) {
        return editingInterface.execute(chatId, message);
    }

    public SendResponse checkReports(Long chatId, Message message) {
        return reportCheckingInterface.execute(chatId, message);
    }
}
