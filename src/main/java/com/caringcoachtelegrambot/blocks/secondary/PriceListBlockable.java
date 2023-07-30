package com.caringcoachtelegrambot.blocks.secondary;

import com.caringcoachtelegrambot.blocks.parents.SimpleBlockable;
import com.caringcoachtelegrambot.blocks.secondary.helpers.Helper;
import com.caringcoachtelegrambot.exceptions.NotValidDataException;
import com.caringcoachtelegrambot.services.ServiceKeeper;
import com.caringcoachtelegrambot.utils.TelegramSender;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.stereotype.Component;

import static com.caringcoachtelegrambot.utils.Constants.BACK;

@Component
public class PriceListBlockable extends SimpleBlockable<PriceListBlockable.PriceListHelper> {

    public PriceListBlockable(TelegramSender telegramSender,
                              ServiceKeeper serviceKeeper) {
        super(telegramSender, serviceKeeper);
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class PriceListHelper extends Helper {
        public PriceListHelper() {
        }
    }

    @Override
    public SendResponse process(Long chatId, Message message) {
        String txt = message.text();
        switch (txt) {
            case "Персональное ведение" -> {
                return personalManagement(chatId);
            }
            case "Разовая тренировка" -> {
                return oneTimeTraining(chatId);
            }
            case "Абонементы" -> {
                return subscriptions(chatId);
            }
            case "Назад" -> {
                return goToBack(chatId);
            }
        }
        throw new NotValidDataException();
    }

    @Override
    public SendResponse uniqueStartBlockMessage(Long chatId) {
        helpers().put(chatId, new PriceListHelper());
        return sender().sendResponse(new SendMessage(chatId, "Блок прайс листов")
                .replyMarkup(markup()));
    }

    @Override
    public ReplyKeyboardMarkup markup() {
        return backMarkup().addRow("Персональное ведение")
                .addRow("Разовая тренировка")
                .addRow("Абонементы");
    }

    private SendResponse personalManagement(Long chatId) {
        return sender().sendResponse(new SendMessage(chatId, " персональное ведение ")
                .replyMarkup(markup()));
    }

    private SendResponse oneTimeTraining(Long chatId) {
        return sender().sendResponse(new SendMessage(chatId, " разовые тренировки ")
                .replyMarkup(markup()));
    }

    private SendResponse subscriptions(Long chatId) {
        return sender().sendResponse(new SendMessage(chatId, " абонементы ")
                .replyMarkup(markup()));
    }
}