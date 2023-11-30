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
public class MeetingBlockable extends SimpleBlockable<MeetingBlockable.MeetingHelper> {

    public MeetingBlockable(ServiceKeeper serviceKeeper,
                            TelegramSender telegramSender) {
        super(telegramSender, serviceKeeper);
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class MeetingHelper extends Helper {
    }

    @Override
    public SendResponse process(Long chatId, Message message) {
        String txt = message.text();
        switch (txt) {
            case "Обо мне" -> {
                return about(chatId);
            }
            case "Мои регалии" -> {
                return regalia(chatId);
            }
            case "Тебе нужна работа со мной, если..." -> {
                return cause(chatId);
            }
            case "Назад" -> {
                return goToBack(chatId);
            }
        }
        throw new NotValidDataException();
    }

    @Override
    public SendResponse uniqueStartBlockMessage(Long chatId) {
        signIn(chatId, new MeetingHelper());
        return msg(chatId, "Знакомство", markup());
    }

    @Override
    public List<String> buttons() {
        return List.of("Обо мне",
                "Мои регалии",
                "тебе нужна работа со мной если...");
    }


    private SendResponse about(Long chatId) {
        return msg(chatId, "Функционал дорабатывается");
    }

    private SendResponse cause(Long chatId) {
        return msg(chatId, "Функционал дорабатывается");
    }

    private SendResponse regalia(Long chatId) {
        return msg(chatId, "Функционал дорабатывается");
    }
}