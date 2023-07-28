package com.caringcoachtelegrambot.blocks.secondary.tertiary.accounts.trainerhelper;

import com.caringcoachtelegrambot.blocks.parents.Blockable;
import com.caringcoachtelegrambot.blocks.secondary.helpers.Helper;
import com.caringcoachtelegrambot.blocks.secondary.tertiary.accounts.TrainerAccountBlockable;
import com.caringcoachtelegrambot.blocks.secondary.tertiary.accounts.trainerhelper.interfaces.EditingInterface;
import com.caringcoachtelegrambot.blocks.secondary.tertiary.accounts.trainerhelper.interfaces.FAQInterface;
import com.caringcoachtelegrambot.blocks.secondary.tertiary.accounts.trainerhelper.interfaces.QuestionnaireInterface;
import com.caringcoachtelegrambot.blocks.secondary.tertiary.accounts.trainerhelper.interfaces.TrainingPlanInterface;
import com.caringcoachtelegrambot.services.*;
import com.caringcoachtelegrambot.utils.TelegramSender;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.response.SendResponse;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TrainerHelper extends Helper {

    private final Blockable trainerAccountBlockable;

    private final QuestionnaireService questionnaireService;

    private final FAQService faqService;

    private final TrainerService trainerService;

    private final OnlineTrainingService onlineTrainingService;

    private final AthleteService athleteService;

    private final TelegramSender sender;

    private final QuestionnaireInterface questionnaireInterface;

    private final FAQInterface faqInterface;

    private final TrainingPlanInterface trainingPlanInterface;

    private final EditingInterface editingInterface;

    private boolean checkingQuestionnaires;

    private boolean answeringFAQs;

    private boolean addingNewFaq;

    private boolean makesTrainingPlan;

    private boolean accountEditing;

    public TrainerHelper(QuestionnaireService questionnaireService,
                         FAQService faqService,
                         TelegramSender sender,
                         TrainerAccountBlockable trainerAccountBlockable,
                         OnlineTrainingService onlineTrainingService,
                         AthleteService athleteService,
                         TrainerService trainerService) {
        super();
        this.trainerAccountBlockable = trainerAccountBlockable;
        this.questionnaireService = questionnaireService;
        this.faqService = faqService;
        this.athleteService = athleteService;
        this.trainerService = trainerService;
        this.sender = sender;
        this.onlineTrainingService = onlineTrainingService;
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
