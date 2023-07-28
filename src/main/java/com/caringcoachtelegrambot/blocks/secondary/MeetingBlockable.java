package com.caringcoachtelegrambot.blocks.secondary;

import com.caringcoachtelegrambot.blocks.parents.SimpleBlockable;
import com.caringcoachtelegrambot.blocks.secondary.helpers.Helper;
import com.caringcoachtelegrambot.exceptions.NotValidDataException;
import com.caringcoachtelegrambot.services.TrainerService;
import com.caringcoachtelegrambot.utils.TelegramSender;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.KeyboardButton;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.stereotype.Component;

@Component
public class MeetingBlockable extends SimpleBlockable<MeetingBlockable.MeetingHelper> {

    private final TrainerService trainerService;

    public MeetingBlockable(TrainerService trainerService,
                            TelegramSender telegramSender) {
        super(telegramSender);
        this.trainerService = trainerService;
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class MeetingHelper extends Helper {}

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
        helpers().put(chatId, new MeetingHelper());
        return sender().sendResponse(new SendMessage(chatId, "Знакомство")
                .replyMarkup(markup()));
    }

    @Override
    public ReplyKeyboardMarkup markup() {
        return new ReplyKeyboardMarkup(
                new KeyboardButton("Обо мне"),
                new KeyboardButton("Мои регалии"))
                .addRow(new KeyboardButton("Тебе нужна работа со мной, если..."))
                .addRow("Назад");
    }

    private SendResponse about(Long chatId) {
        return sender().sendResponse(new SendMessage(chatId, trainerService.getTrainer().getAbout()));
    }

    private SendResponse cause(Long chatId) {
        return sender().sendResponse(new SendMessage(chatId, trainerService.getTrainer().getCause()));
    }

    private SendResponse regalia(Long chatId) {
        return sender().sendResponse(new SendMessage(chatId, "Мои регалии"));
    }
}