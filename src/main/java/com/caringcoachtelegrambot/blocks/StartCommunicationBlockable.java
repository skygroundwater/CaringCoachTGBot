package com.caringcoachtelegrambot.blocks;

import com.caringcoachtelegrambot.blocks.parents.Blockable;
import com.caringcoachtelegrambot.blocks.parents.BlockableForBackStep;
import com.caringcoachtelegrambot.blocks.parents.PaddedBlockable;
import com.caringcoachtelegrambot.blocks.secondary.FAQBlockable;
import com.caringcoachtelegrambot.blocks.secondary.MeetingBlockable;
import com.caringcoachtelegrambot.blocks.secondary.PriceListBlockable;
import com.caringcoachtelegrambot.blocks.secondary.QuestionnaireRecordingBlockable;
import com.caringcoachtelegrambot.blocks.secondary.AuthorizationBlockable;
import com.caringcoachtelegrambot.blocks.secondary.RegistrationBlockable;
import com.caringcoachtelegrambot.blocks.secondary.helpers.Helper;
import com.caringcoachtelegrambot.exceptions.NotValidDataException;
import com.caringcoachtelegrambot.services.ServiceKeeper;
import com.caringcoachtelegrambot.utils.TelegramSender;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
public class StartCommunicationBlockable extends PaddedBlockable<StartCommunicationBlockable.StartCommunicationHelper> {

    private final Blockable meetingBlockable;

    private final Blockable questionnaireBlockable;

    private final BlockableForBackStep faqBlockable;

    private final Blockable priceListBlockable;

    private final Blockable registrationBlockable;

    private final Blockable authorizationBlockable;

    public StartCommunicationBlockable(MeetingBlockable meetingBlockable,
                                       QuestionnaireRecordingBlockable questionnaireBlockable,
                                       FAQBlockable faqBlockable,
                                       PriceListBlockable priceListBlockable,
                                       RegistrationBlockable registrationBlockable,
                                       AuthorizationBlockable authorizationBlockable,
                                       TelegramSender telegramSender,
                                       ServiceKeeper serviceKeeper) {
        super(telegramSender, serviceKeeper);
        this.meetingBlockable = meetingBlockable;
        this.questionnaireBlockable = questionnaireBlockable;
        this.faqBlockable = faqBlockable;
        this.priceListBlockable = priceListBlockable;
        this.registrationBlockable = registrationBlockable;
        this.authorizationBlockable = authorizationBlockable;
    }

    @PostConstruct
    private void setUp() {
        meetingBlockable.setPrevBlockable(this);
        questionnaireBlockable.setPrevBlockable(this);
        faqBlockable.setPrevBlockable(this);
        priceListBlockable.setPrevBlockable(this);
        registrationBlockable.setPrevBlockable(this);
        authorizationBlockable.setPrevBlockable(this);
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class StartCommunicationHelper extends Helper {
        public StartCommunicationHelper() {
        }
    }

    @Override
    public SendResponse process(Long chatId, Message message) {
        String info = message.text();
        switch (info) {
            case "Блок информации о тренере" -> {
                return goTo(chatId, meetingBlockable);
            }
            case "Ответы на часто задаваемые вопросы" -> {
                return goTo(chatId, faqBlockable);
            }
            case "Отправить анкету" -> {
                return goTo(chatId, questionnaireBlockable);
            }
            case "Прайс-лист" -> {
                return goTo(chatId, priceListBlockable);
            }
            case "Регистрация" -> {
                return goTo(chatId, registrationBlockable);
            }
            case "Личный кабинет" -> {
                return goTo(chatId, authorizationBlockable);
            }
        }
        throw new NotValidDataException();
    }

    @Override
    public SendResponse uniqueStartBlockMessage(Long chatId) {
        helpers().put(chatId, new StartCommunicationHelper());
        return sender().sendResponse(new SendMessage(chatId, "Привет!")
                .replyMarkup(markup()));
    }

    private SendResponse goTo(Long chatId, Blockable blockable) {
        helpers().get(chatId).setIn(false);
        return blockable.uniqueStartBlockMessage(chatId);
    }

    @Override
    public ReplyKeyboardMarkup markup() {
        return new ReplyKeyboardMarkup("Блок информации о тренере")
                .addRow("Ответы на часто задаваемые вопросы")
                .addRow("Отправить анкету")
                .addRow("Прайс-лист")
                .addRow("Регистрация")
                .addRow("Личный кабинет").oneTimeKeyboard(true);
    }
}