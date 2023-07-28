package com.caringcoachtelegrambot.blocks.secondary;

import com.caringcoachtelegrambot.blocks.parents.PaddedBlockable;
import com.caringcoachtelegrambot.blocks.secondary.helpers.Helper;
import com.caringcoachtelegrambot.blocks.secondary.tertiary.PopularFAQBlockable;
import com.caringcoachtelegrambot.models.FAQ;
import com.caringcoachtelegrambot.services.FAQService;
import com.caringcoachtelegrambot.utils.TelegramSender;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.stereotype.Component;

@Component
public class FAQBlockable extends PaddedBlockable<FAQBlockable.FAQHelper> {

    private final FAQService faqService;

    public FAQBlockable(FAQService faqService, TelegramSender telegramSender,
                        PopularFAQBlockable popularFAQBlockable) {
        super(telegramSender);
        this.faqService = faqService;
        node().setNextBlockable(popularFAQBlockable);
        popularFAQBlockable.setPrevBlockable(this);
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class FAQHelper extends Helper {

        private boolean asking;

        public FAQHelper() {
            super();
            this.asking = false;
        }
    }

    @Override
    public SendResponse process(Long chatId, Message message) {
        String txt = message.text();
        if (!node().getNextBlockable().checkIn(chatId)) {
            FAQHelper helper = helpers().get(chatId);
            if (helper != null) {
                if (!helper.isAsking()) {
                    switch (txt) {
                        case "Вы можете задать свой вопрос" -> {
                            return ask(chatId);
                        }
                        case "Популярные вопросы" -> {
                            return goToNext(chatId);
                        }
                        case "Назад" -> {
                            return goToBack(chatId);
                        }
                    }
                }
                return stopAsking(chatId, txt);
            }
        }
        return node().getNextBlockable().block(chatId, message);
    }

    @Override
    public SendResponse uniqueStartBlockMessage(Long chatId) {
        helpers().put(chatId, new FAQHelper());
        return sender().sendResponse(new SendMessage(chatId, "Блок для вопросов")
                .replyMarkup(markup()));
    }

    @Override
    public ReplyKeyboardMarkup markup() {
        return new ReplyKeyboardMarkup("Вы можете задать свой вопрос")
                .addRow("Популярные вопросы")
                .addRow("Назад").oneTimeKeyboard(true);
    }

    private SendResponse ask(Long chatId) {
        helpers().get(chatId).setAsking(true);
        return sender().sendResponse(new SendMessage(chatId,
                "Следующим сообщением задайте вопрос"));
    }

    private SendResponse stopAsking(Long chatId, String question) {
        helpers().get(chatId).setAsking(false);
        faqService.postFAQ(new FAQ(question, chatId));
        return uniqueStartBlockMessage(chatId);
    }
}
