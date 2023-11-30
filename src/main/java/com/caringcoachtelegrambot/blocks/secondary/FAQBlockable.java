package com.caringcoachtelegrambot.blocks.secondary;

import com.caringcoachtelegrambot.blocks.parents.PaddedBlockable;
import com.caringcoachtelegrambot.blocks.secondary.helpers.Helper;
import com.caringcoachtelegrambot.models.FAQ;
import com.caringcoachtelegrambot.services.keeper.ServiceKeeper;
import com.caringcoachtelegrambot.utils.TelegramSender;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.response.SendResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FAQBlockable extends PaddedBlockable<FAQBlockable.FAQHelper> {

    public FAQBlockable(ServiceKeeper serviceKeeper,
                        TelegramSender telegramSender,
                        PopularFAQBlockable popularFAQBlockable) {
        super(telegramSender, serviceKeeper);
        node().setNextBlockable(popularFAQBlockable);
        popularFAQBlockable.setPrevBlockable(this);
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    protected static class FAQHelper extends Helper {

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
        signIn(chatId, new FAQHelper());
        return msg(chatId, "Блок для вопросов", markup());
    }

    @Override
    public List<String> buttons() {
        return List.of(
                "Вы можете задать свой вопрос",
                "Популярные вопросы");
    }

    private SendResponse ask(Long chatId) {
        helpers().get(chatId).setAsking(true);
        return msg(chatId, "Следующим сообщением задайте вопрос");
    }

    private SendResponse stopAsking(Long chatId, String question) {
        helpers().get(chatId).setAsking(false);
        faqService().post(new FAQ(question, chatId));
        return uniqueStartBlockMessage(chatId);
    }
}
