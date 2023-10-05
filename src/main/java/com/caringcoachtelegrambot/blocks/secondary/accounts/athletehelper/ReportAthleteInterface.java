package com.caringcoachtelegrambot.blocks.secondary.accounts.athletehelper;

import com.caringcoachtelegrambot.exceptions.NotValidDataException;
import com.caringcoachtelegrambot.models.Athlete;
import com.caringcoachtelegrambot.models.Report;
import com.caringcoachtelegrambot.services.keeper.ServiceKeeper;
import com.caringcoachtelegrambot.utils.TelegramSender;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.caringcoachtelegrambot.utils.Constants.BACK;

@Component
public class ReportAthleteInterface extends AthleteAccountInterface<ReportAthleteInterface.ReportHelper> {

    public ReportAthleteInterface(TelegramSender sender,
                                  ServiceKeeper serviceKeeper) {
        super(sender, serviceKeeper);
    }

    @Setter
    @Getter
    public static class ReportHelper extends AthleteHelper {

        protected ReportHelper(Athlete athlete) {
            super(athlete);
        }

        private String state;

        private String emotion;

        private String weight;

        private String wishes;

    }

    @Override
    public SendResponse uniqueStartBlockMessage(Long chatId) {
        helpers().put(chatId, new ReportHelper(athleteService().findById(chatId)));
        return sender().sendResponse(new SendMessage(chatId, "Вы в блоке для отправки отчета")
                .replyMarkup(markup()));
    }

    @Override
    protected List<String> buttons() {
        return List.of("Начать отправку отчета");
    }

    @Override
    protected SendResponse switching(Long chatId, Message message, ReportHelper helper) {
        String txt = message.text();
        switch (txt) {
            case "Назад" -> {
                return goToBack(chatId);
            }
            case "Начать отправку отчета" -> {
                return execute(chatId, helper);
            }
        }
        throw new NotValidDataException();
    }

    private SendResponse execute(Long chatId, ReportHelper helper) {
        helper.setWorking(true);
        return intermediateMsg(chatId, "Расскажи о своем общем состоянии в течении прошедшей недели");
    }

    @Override
    protected SendResponse work(Long chatId, Message message, ReportHelper helper) {
        String value = message.text();
        if (value.equals(BACK)) return forcedStop(chatId);
        if (helper.state == null) {
            helper.setState(message.text());
            return msg(chatId, "Расскажи об эмоциях от тренировок");
        } else if (helper.emotion == null) {
            helper.setEmotion(message.text());
            return msg(chatId, "Расскажи о своих продвижениях на весах");
        } else if (helper.weight == null) {
            helper.setWeight(message.text());
            return msg(chatId, "Скажи чего бы ты хотела пожелать в будущем на наших тренировках");
        } else {
            return saveReport(chatId, message, helper);
        }
    }

    private SendResponse saveReport(Long chatId, Message message, ReportHelper helper) {
        helper.setWishes(message.text());
        reportService().post(Report.builder()
                .athlete(helper.getAthlete())
                .weight(helper.weight)
                .wishes(helper.wishes)
                .emotion(helper.emotion)
                .state(helper.state)
                .build());
        helpers().put(chatId, new ReportHelper(athleteService().findById(chatId)));
        return uniqueStartBlockMessage(chatId);
    }
}