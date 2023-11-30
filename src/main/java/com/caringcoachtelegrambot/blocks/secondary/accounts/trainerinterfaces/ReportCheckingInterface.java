package com.caringcoachtelegrambot.blocks.secondary.accounts.trainerinterfaces;

import com.caringcoachtelegrambot.blocks.secondary.accounts.AccountInterface;
import com.caringcoachtelegrambot.blocks.secondary.helpers.accounthelpers.TrainerHelper;
import com.caringcoachtelegrambot.exceptions.NotFoundInDataBaseException;
import com.caringcoachtelegrambot.exceptions.NotValidDataException;
import com.caringcoachtelegrambot.models.Report;
import com.caringcoachtelegrambot.models.Trainer;
import com.caringcoachtelegrambot.services.keeper.ServiceKeeper;
import com.caringcoachtelegrambot.utils.TelegramSender;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.response.SendResponse;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ReportCheckingInterface extends AccountInterface<ReportCheckingInterface.ReportCheckingHelper> {

    public ReportCheckingInterface(TelegramSender sender, ServiceKeeper serviceKeeper) {
        super(sender, serviceKeeper);
    }

    @Setter
    @Getter
    public static class ReportCheckingHelper extends TrainerHelper {

        private Report report;

        public ReportCheckingHelper(Trainer trainer) {
            super(trainer);
        }
    }

    @Override
    public List<String> buttons() {
        return List.of("Взять отчёт");
    }

    @Override
    protected SendResponse switching(Long chatId, Message message, ReportCheckingHelper helper) {
        String txt = message.text();
        switch (txt) {
            case "Взять отчёт" -> {
                return sendReport(chatId, helper);
            }
            case "Назад" -> {
                return goToBack(chatId);
            }
        }
        throw new NotValidDataException();
    }

    private SendResponse sendReport(Long chatId, ReportCheckingHelper helper) {
        try {
            helper.setWorking(true);
            Report report = reportService().findOneUncheckedReport();
            String reportInfo = String.format("""
                            *Отчет от атлета:* %s
                                            
                            *Общее состояние:* %s
                                            
                            *Эмоции от тренировок:* %s
                                            
                            *Вес:* %s
                                            
                            *Пожелания:* %s
                                           
                            """, report.getAthlete().getFirstName(),
                    report.getState(), report.getEmotion(),
                    report.getWeight(), report.getWishes());
            helper.setReport(report);
            return msg(chatId, reportInfo, backMarkup().addRow("Принять отчет"));
        } catch (NotFoundInDataBaseException e) {
            msg(chatId, "Отчетов нет");
            return uniqueStartBlockMessage(chatId);
        }
    }

    @Override
    protected SendResponse work(Long chatId, Message message, ReportCheckingHelper helper) {
        String txt = message.text();
        switch (txt) {
            case "Принять отчет" -> {
                return acceptReport(chatId, helper);
            }
            case "Назад" -> {
                return back(chatId, helper);
            }
        }
        throw new NotValidDataException();
    }

    private SendResponse acceptReport(Long chatId, ReportCheckingHelper helper) {
        helper.report.setChecked(true);
        reportService().post(helper.report);
        return sendReport(chatId, helper);
    }

    private SendResponse back(Long chatId, ReportCheckingHelper helper) {
        helper.report = null;
        helper.setWorking(false);
        return uniqueStartBlockMessage(chatId);
    }

    @Override
    public SendResponse uniqueStartBlockMessage(Long chatId) {
        signIn(chatId, new ReportCheckingHelper(trainerService().findTrainerById(chatId)));
        return msg(chatId, "Ты в блоке проверки отчетов", markup());
    }
}