package com.caringcoachtelegrambot.blocks.secondary;

import com.caringcoachtelegrambot.blocks.parents.SimpleBlockable;
import com.caringcoachtelegrambot.blocks.secondary.helpers.Helper;
import com.caringcoachtelegrambot.services.FAQService;
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
public class PopularFAQBlockable extends SimpleBlockable<PopularFAQBlockable.PopFAQHelper> {

    private PopularFAQBlockable(ServiceKeeper serviceKeeper,
                                TelegramSender telegramSender) {
        super(telegramSender, serviceKeeper);
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class PopFAQHelper extends Helper {
    }

    @Override
    public SendResponse process(Long chatId, Message message) {
        String question = message.text();
        if (question.equals(BACK)) {
            return goToBack(chatId);
        } else {
            return sender().sendResponse(new SendMessage(chatId, faqService().findById(question).getAnswer()));
        }
    }

    @Override
    public SendResponse uniqueStartBlockMessage(Long chatId) {
        helpers().put(chatId, new PopFAQHelper());
        return sender().sendResponse(new SendMessage(chatId, "Выберите вопрос из списка")
                .replyMarkup(markup()));
    }

    @Override
    public ReplyKeyboardMarkup markup() {
        ReplyKeyboardMarkup markupWithQuestions = backMarkup();
        faqService().findAll().forEach(faq -> {
            if (faq.getAnswer() != null) {
                markupWithQuestions.addRow(faq.getQuestion());
            }
        });
        return markupWithQuestions;
    }
}
