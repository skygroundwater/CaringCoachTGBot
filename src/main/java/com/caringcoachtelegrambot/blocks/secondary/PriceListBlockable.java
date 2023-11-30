package com.caringcoachtelegrambot.blocks.secondary;

import com.caringcoachtelegrambot.blocks.parents.SimpleBlockable;
import com.caringcoachtelegrambot.blocks.secondary.helpers.Helper;
import com.caringcoachtelegrambot.exceptions.NotValidDataException;
import com.caringcoachtelegrambot.services.keeper.ServiceKeeper;
import com.caringcoachtelegrambot.utils.TelegramSender;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.response.SendResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.stereotype.Component;

import java.util.List;

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
        signIn(chatId, new PriceListHelper());
        return msg(chatId, "Блок прайс листов", markup());
    }

    @Override
    public List<String> buttons() {
        return List.of("Персональное ведение",
                "Разовая тренировка",
                "Абонементы");
    }

    private SendResponse personalManagement(Long chatId) {
        return msg(chatId, " персональное ведение ", markup());
    }

    private SendResponse oneTimeTraining(Long chatId) {
        return msg(chatId, " разовые тренировки ", markup());
    }

    private SendResponse subscriptions(Long chatId) {
        return msg(chatId, " абонементы ", markup());
    }
}