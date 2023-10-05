package com.caringcoachtelegrambot.blocks.secondary.accounts.trainerinterfaces;

import com.caringcoachtelegrambot.exceptions.NotFoundInDataBaseException;
import com.caringcoachtelegrambot.exceptions.NotValidDataException;
import com.caringcoachtelegrambot.models.Report;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;

import java.util.List;

public class ReportCheckingInterface extends TrainerAccountInterface {

    private Report report;

    private boolean reportOnScreen;

    public ReportCheckingInterface(TrainerHelper helper) {
        super(helper);
    }

    @Override
    protected List<String> buttons() {
        return List.of("Взять отчёт");
    }

    private SendResponse uniqueStartMessage(Long chatId) {
        getHelper().setCheckingReports(true);
        return sender().sendResponse(
                new SendMessage(chatId, "Ты в блоке проверки отчетов")
                        .replyMarkup(markup()));
    }

    public SendResponse execute(Long chatId, Message message) {
        if (!getHelper().isCheckingReports()) return uniqueStartMessage(chatId);
        if (reportOnScreen) return taskExecution(chatId, message);
        return switching(chatId, message);
    }

    private SendResponse taskExecution(Long chatId, Message message) {
        String txt = message.text();
        switch (txt) {
            case "Принять отчет" -> {
                return acceptReport(chatId);
            }
            case "Назад" -> {
                return back(chatId);
            }
        }
        throw new NotValidDataException();
    }

    @Override
    protected SendResponse switching(Long chatId, Message message) {
        String txt = message.text();
        switch (txt) {
            case "Взять отчёт" -> {
                return sendReport(chatId);
            }
            case "Назад" -> {
                return stop(chatId);
            }
        }
        throw new NotValidDataException();
    }

    protected SendResponse stop(Long chatId) {
        getHelper().setCheckingReports(false);
        return getHelper().getTrainerAccountBlockable().uniqueStartBlockMessage(chatId);
    }

    private SendResponse sendReport(Long chatId) {
        try {
            report = reportService().findOneUncheckedReport();
            String reportInfo = String.format("""
                            *Отчет от атлета:* %s
                                            
                            *Общее состояние:* %s
                                            
                            *Эмоции от тренировок:* %s
                                            
                            *Вес:* %s
                                            
                            *Пожелания:* %s
                                           
                            """, report.getAthlete().getFirstName(),
                    report.getState(), report.getEmotion(),
                    report.getWeight(), report.getWishes());
            reportOnScreen = true;
            return sender().sendResponse(new SendMessage(chatId, reportInfo)
                    .replyMarkup(backMarkup().addRow("Принять отчет")));
        }catch (NotFoundInDataBaseException e){
            msg(chatId, "Отчетов нет");
            return uniqueStartMessage(chatId);
        }
    }

    private SendResponse acceptReport(Long chatId) {
        report.setChecked(true);
        reportService().post(report);
        return sendReport(chatId);
    }

    private SendResponse back(Long chatId) {
        report = null;
        reportOnScreen = false;
        return uniqueStartMessage(chatId);
    }

}
