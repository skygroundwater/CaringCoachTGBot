package com.caringcoachtelegrambot.blocks.secondary.accounts;

import com.caringcoachtelegrambot.blocks.secondary.helpers.accounthelpers.TrainerHelper;
import com.caringcoachtelegrambot.blocks.secondary.accounts.trainerinterfaces.*;
import com.caringcoachtelegrambot.exceptions.NotValidDataException;
import com.caringcoachtelegrambot.services.keeper.ServiceKeeper;
import com.caringcoachtelegrambot.utils.TelegramSender;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.response.SendResponse;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Getter
public class TrainerAccountBlockable extends AccountBlockable<TrainerHelper> {

    private final AccountInterface<EditingInterfaceTrainer.EditingHelper> editingInterface;

    private final AccountInterface<QuestionnaireInterface.CheckQuestionnaireHelper> questionnaireInterface;

    private final AccountInterface<FAQInterface.FAQHelper> faqInterface;

    private final AccountInterface<ReportCheckingInterface.ReportCheckingHelper> reportCheckingInterface;

    private final AccountInterface<TrainingPlanInterface.TrainingPlanHelper> trainingPlanInterface;

    public TrainerAccountBlockable(TelegramSender telegramSender, ServiceKeeper serviceKeeper,
                                   EditingInterfaceTrainer editingInterface,
                                   QuestionnaireInterface questionnaireInterface,
                                   FAQInterface faqInterface,
                                   ReportCheckingInterface reportCheckingInterface,
                                   TrainingPlanInterface trainingPlanInterface) {
        super(telegramSender, serviceKeeper);
        this.editingInterface = editingInterface;
        this.questionnaireInterface = questionnaireInterface;
        this.faqInterface = faqInterface;
        this.reportCheckingInterface = reportCheckingInterface;
        this.trainingPlanInterface = trainingPlanInterface;
    }

    @PostConstruct
    private void setUp() {
        this.editingInterface.setPrevBlockable(this);
        this.questionnaireInterface.setPrevBlockable(this);
        this.faqInterface.setPrevBlockable(this);
        this.reportCheckingInterface.setPrevBlockable(this);
        this.trainingPlanInterface.setPrevBlockable(this);
    }

    @Override
    public SendResponse uniqueStartBlockMessage(Long chatId) {
        signIn(chatId, new TrainerHelper(trainerService().findTrainerById(chatId)));
        return msg(chatId, "Ты в личном кабинете", markup());
    }

    @Override
    public SendResponse process(Long chatId, Message message) {
        String txt = message.text();
        if (txt != null) {
            switch (txt) {
                case "Редактировать аккаунт" -> {
                    return goTo(chatId, editingInterface);
                }
                case "Выйти из личного кабинета" -> {
                    return goToBack(chatId);
                }
                case "Проверить анкеты" -> {
                    return goTo(chatId, questionnaireInterface);
                }
                case "Раздел по популярным вопросам тренеру" -> {
                    return goTo(chatId, faqInterface);
                }
                case "Тренировки. Расписание" -> {
                    return goTo(chatId, trainingPlanInterface);
                }
                case "Проверить отчёты" -> {
                    return goTo(chatId, reportCheckingInterface);
                }
            }
        }
        throw new NotValidDataException();
    }

    @Override
    public List<String> buttons() {
        return List.of(
                "Проверить анкеты",
                "Раздел по популярным вопросам тренеру",
                "Тренировки. Расписание",
                "Проверить отчёты");
    }
}