package com.caringcoachtelegrambot.blocks.secondary;

import com.caringcoachtelegrambot.blocks.parents.SimpleBlockable;
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
            return msg(chatId, faqService().findById(question).getAnswer());
        }
    }

    @Override
    public SendResponse uniqueStartBlockMessage(Long chatId) {
        signIn(chatId, new PopFAQHelper());
        return msg(chatId, "Выберите вопрос из списка", markup());
    }

    @Override
    public List<String> buttons() {
        return faqService().findAll().stream().map(FAQ::getQuestion).toList();
    }
}
