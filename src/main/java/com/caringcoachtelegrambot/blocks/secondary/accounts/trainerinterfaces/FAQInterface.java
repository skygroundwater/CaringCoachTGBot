package com.caringcoachtelegrambot.blocks.secondary.accounts.trainerinterfaces;

import com.caringcoachtelegrambot.blocks.secondary.accounts.AccountInterface;
import com.caringcoachtelegrambot.blocks.secondary.helpers.accounthelpers.TrainerHelper;
import com.caringcoachtelegrambot.exceptions.NotValidDataException;
import com.caringcoachtelegrambot.models.FAQ;
import com.caringcoachtelegrambot.models.Trainer;
import com.caringcoachtelegrambot.services.keeper.ServiceKeeper;
import com.caringcoachtelegrambot.utils.TelegramSender;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.response.SendResponse;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.caringcoachtelegrambot.utils.Constants.BACK;

@Component
public class FAQInterface extends AccountInterface<FAQInterface.FAQHelper> {

    public FAQInterface(TelegramSender sender, ServiceKeeper serviceKeeper) {
        super(sender, serviceKeeper);
    }

    @Setter
    @Getter
    public static class FAQHelper extends TrainerHelper {

        private FAQ faq;

        private boolean answerProcess;

        private boolean newFAQProcess;

        public FAQHelper(Trainer trainer) {
            super(trainer);
        }
    }

    @Override
    public SendResponse uniqueStartBlockMessage(Long chatId) {
        signIn(chatId, new FAQHelper(trainerService().findTrainerById(chatId)));
        return msg(chatId, "Раздел работы с популярными вопросами", markup());
    }

    @Override
    protected SendResponse switching(Long chatId, Message message, FAQHelper helper) {
        String txt = message.text();
        switch (txt) {
            case "Назад" -> {
                return goToBack(chatId);
            }
            case "Добавить новый FAQ" -> {
                return startAdditionNewFAQ(chatId, helper);
            }
            case "Ответить на заданные вопросы" -> {
                return startAnswering(chatId, helper);
            }
        }
        throw new NotValidDataException();
    }

    @Override
    public List<String> buttons() {
        return List.of(
                "Ответить на заданные вопросы",
                "Добавить новый FAQ");
    }

    @Override
    protected SendResponse work(Long chatId, Message message, FAQHelper helper) {
        if (helper.answerProcess && !helper.newFAQProcess) {
            return answeringProcess(chatId, message, helper);
        } else if (helper.newFAQProcess && !helper.answerProcess) {
            return newFAQProcess(chatId, message, helper);
        }
        throw new NotValidDataException();
    }

    //БЛОК МЕТОДОВ ПО РАБОТЕ С ОТВЕТАМИ НА ВОПРОСЫ
    private SendResponse startAnswering(Long chatId, FAQHelper helper) {
        helper.setWorking(true);
        helper.setAnswerProcess(true);
        return sendFAQToTrainer(chatId, helper);
    }

    private SendResponse answeringProcess(Long chatId, Message message, FAQHelper helper) {
        String value = message.text();
        if (value.equals(BACK)) {
            return stopAnswering(chatId, helper);
        } else {
            return setAnswerForFAQ(chatId, value, helper);
        }
    }

    private SendResponse sendFAQToTrainer(Long chatId, FAQHelper helper) {
        helper.setFaq(faqService().findFAQWithoutAnswer());
        if (helper.getFaq() == null) {
            return stopAnswering(chatId, helper);
        } else {
            msg(chatId, helper.getFaq().getQuestion());
            return msg(chatId, "Ответ должен быть в следующем сообщении", backMarkup());
        }
    }

    private SendResponse stopAnswering(Long chatId, FAQHelper helper) {
        helper.setAnswerProcess(false);
        helper.setWorking(false);
        helper.setFaq(null);
        return uniqueStartBlockMessage(chatId);
    }

    private SendResponse setAnswerForFAQ(Long chatId, String answer, FAQHelper helper) {
        FAQ faq = helper.faq;
        faq.setAnswer(answer);
        faqService().put(faq);
        msg(faq.getAthleteId(), String.format("""
                Тренер ответил на ваш вопрос: %s
                                        
                Ответ: %s
                """, faq.getQuestion(), faq.getAnswer()));
        return sendFAQToTrainer(chatId, helper);
    }

    //БЛОК МЕТОДОВ ПО РАБОТЕ С ДОБАВЛЕНИЕМ НОВЫХ ВОПРОСОВ

    private SendResponse startAdditionNewFAQ(Long chatId, FAQHelper helper) {
        helper.setWorking(true);
        helper.setNewFAQProcess(true);
        helper.setFaq(new FAQ());
        return msg(chatId, "Следующим сообщением отправь вопрос, на который будешь отвечать");
    }

    private SendResponse newFAQProcess(Long chatId, Message message, FAQHelper helper) {
        String value = message.text();
        if (value.equals(BACK)) {
            return stopNewFAQProcess(chatId, helper);
        } else {
            return setValuesForFAQ(chatId, value, helper);
        }
    }

    private SendResponse setValuesForFAQ(Long chatId, String value, FAQHelper helper) {
        FAQ faq = helper.faq;
        if (faq.getQuestion().isBlank() && faq.getAnswer().isBlank()) {
            faq.setAnswer(value);
            return msg(chatId, "Следующим сообщением отправь ответ");
        } else if (!faq.getQuestion().isBlank() && faq.getAnswer().isBlank()) {
            faq.setAnswer(value);
            return msg(chatId, "Вы ответили на популярный вопрос");
        }
        throw new NotValidDataException();
    }

    private SendResponse stopNewFAQProcess(Long chatId, FAQHelper helper) {
        helper.setFaq(null);
        helper.setNewFAQProcess(false);
        helper.setWorking(false);
        return uniqueStartBlockMessage(chatId);
    }
}