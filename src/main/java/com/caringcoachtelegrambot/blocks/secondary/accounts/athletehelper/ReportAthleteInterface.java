package com.caringcoachtelegrambot.blocks.secondary.accounts.athletehelper;

import com.caringcoachtelegrambot.services.ServiceKeeper;
import com.caringcoachtelegrambot.utils.TelegramSender;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import com.pengrad.telegrambot.response.SendResponse;
import lombok.Builder;
import org.springframework.stereotype.Component;

@Component
public class ReportAthleteInterface extends AthleteAccountInterface<ReportAthleteInterface.ReportHelper> {

    public ReportAthleteInterface(TelegramSender sender,
                                  ServiceKeeper serviceKeeper) {
        super(sender, serviceKeeper);
    }

    @Builder
    public static class ReportHelper extends AthleteHelper{
        private boolean reporting;

    }

    @Override
    public SendResponse uniqueStartBlockMessage(Long chatId) {
        return null;
    }

    @Override
    public ReplyKeyboardMarkup markup() {
        return backMarkup()
                .addRow("Начать отправку отчета")
                .addRow("");
    }

    @Override
    public SendResponse process(Long chatId, Message message) {
        String txt = message.text();

        return ;
    }
}
