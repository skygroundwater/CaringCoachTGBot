package com.caringcoachtelegrambot.blocks.secondary.accounts.trainerinterfaces;

import com.caringcoachtelegrambot.exceptions.NotFoundInDataBaseException;
import com.caringcoachtelegrambot.exceptions.NotValidDataException;
import com.caringcoachtelegrambot.models.FAQ;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

import static com.caringcoachtelegrambot.utils.Constants.BACK;

@Getter
@Setter
public class FAQInterface extends TrainerAccountInterface {

    private FAQ faq;

    private boolean answering;

    private boolean questionOnScreen;

    public FAQInterface(TrainerHelper helper) {
        super(helper);
        answering = false;
    }


    public SendResponse execute(Long chatId, Message message) {
        String txt = message.text();
        getHelper().setAnsweringFAQs(true);
        if (this.answering) {
            return setAnswer(chatId, txt);
        } else if (!this.questionOnScreen) {
            return sendQuestionWithoutAnswer(chatId);
        } else return switching(chatId, message);
    }

    @Override
    protected SendResponse switching(Long chatId, Message message) {
        String txt = message.text();
        switch (txt) {
            case "Отправить ответ" -> {
                return startAnswering(chatId);
            }
            case "Отклонить вопрос" -> {
                return rejectFAQ(chatId);
            }
            case "Назад" -> {
                return stop(chatId);
            }
        }
        throw new NotValidDataException();
    }

    private SendResponse startAnswering(Long chatId) {
        this.answering = true;
        return sender().sendResponse(new SendMessage(chatId, "Ваш ответ должен быть в следующем сообщении")
                .replyMarkup(new ReplyKeyboardMarkup("Не отвечать на вопрос").oneTimeKeyboard(true)));
    }

    private SendResponse setAnswer(Long chatId, String answer) {
        if (answer.equals("Не отвечать на вопрос")) {
            this.answering = false;
            return sendQuestionWithoutAnswer(chatId);
        }
        faq.setAnswer(answer);
        String answerForAthlete =
                String.format("""
                        Тренер ответил на ваш вопрос: %s
                                                
                        Ответ: %s
                        """, faq.getQuestion(), faq.getAnswer());
        faqService().post(faq);
        sender().sendResponse(new SendMessage(faq.getAthleteId(), answerForAthlete));
        answering = false;
        questionOnScreen = false;
        sender().sendResponse(new SendMessage(chatId, "Вы ответили на вопрос и ответ отправлен тому, кто его задал"));
        return sendQuestionWithoutAnswer(chatId);
    }

    protected SendResponse stop(Long chatId) {
        this.answering = false;
        questionOnScreen = false;
        getHelper().setAnsweringFAQs(false);
        return getHelper().getTrainerAccountBlockable().uniqueStartBlockMessage(chatId);
    }

    private SendResponse rejectFAQ(Long chatId) {
        faqService().delete(faq);
        questionOnScreen = false;
        sender().sendResponse(new SendMessage(faq.getAthleteId(), "Ваш вопрос был отклонен тренером"));
        sender().sendResponse(new SendMessage(chatId, "Вы отклонили вопрос"));
        faq = null;
        return sendQuestionWithoutAnswer(chatId);
    }

    private SendResponse sendQuestionWithoutAnswer(Long chatId) {
        try {
            faq = faqService().findFAQWithoutAnswer();
            if (faq != null) {
                questionOnScreen = true;
                return sender().sendResponse(
                        new SendMessage(chatId, faq.getQuestion())
                                .replyMarkup(markup()));
            }
        } catch (NotFoundInDataBaseException e) {
            getHelper().setAnsweringFAQs(false);
            return sender()
                    .sendResponse(new SendMessage(chatId, "Актуальных вопросов более нет"));
        }
        getHelper().setAnsweringFAQs(false);
        return sender()
                .sendResponse(new SendMessage(chatId, "Актуальных вопросов более нет"));
    }


    protected List<String> buttons() {
        return List.of("Отправить ответ", "Отклонить вопрос");
    }

    public SendResponse newFaq(Long chatId, String txt) {
        if (txt.equals("Не отпралять FAQ")) {
            return stopAdding(chatId);
        } else if (faq == null) {
            return startAdding(chatId);
        } else if (faq.getQuestion() == null) {
            faq.setQuestion(txt);
            return sender().sendResponse(new SendMessage(chatId, "Cледующим сообщением отправь ответ"));
        } else {
            faq.setAnswer(txt);
            getHelper().setAddingNewFaq(false);
            faqService().post(faq);
            faq = null;
            sender().sendResponse(new SendMessage(chatId, "Вы добавили новый FAQ"));
            return getHelper().getTrainerAccountBlockable().uniqueStartBlockMessage(chatId);
        }
    }

    private SendResponse stopAdding(Long chatId) {
        getHelper().setAddingNewFaq(false);
        faq = null;
        sender().sendResponse(new SendMessage(chatId, "Вы не стали добавлять новый FAQ"));
        return getHelper().getTrainerAccountBlockable().uniqueStartBlockMessage(chatId);
    }

    private SendResponse startAdding(Long chatId) {
        getHelper().setAddingNewFaq(true);
        faq = new FAQ();
        faq.setAthleteId(chatId);
        return sender().sendResponse(new SendMessage(chatId, "Следующим сообщением отправь вопрос")
                .replyMarkup(markupForAdding()));
    }

    private ReplyKeyboardMarkup markupForAdding() {
        return new ReplyKeyboardMarkup("Не отпралять FAQ");
    }
}