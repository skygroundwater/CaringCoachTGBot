package com.caringcoachtelegrambot.blocks.secondary.accounts.athleteinterfaces;

import com.caringcoachtelegrambot.blocks.secondary.accounts.AccountInterface;
import com.caringcoachtelegrambot.blocks.secondary.helpers.accounthelpers.AthleteHelper;
import com.caringcoachtelegrambot.exceptions.NotValidDataException;
import com.caringcoachtelegrambot.models.Athlete;
import com.caringcoachtelegrambot.models.FAQ;
import com.caringcoachtelegrambot.services.keeper.ServiceKeeper;
import com.caringcoachtelegrambot.utils.TelegramSender;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.caringcoachtelegrambot.utils.Constants.BACK;

@Component
public class DietaryGuideInterface extends AccountInterface<DietaryGuideInterface.DietaryGuideHelper> {

    public DietaryGuideInterface(TelegramSender sender,
                                 ServiceKeeper serviceKeeper) {
        super(sender, serviceKeeper);
    }

    public static class DietaryGuideHelper extends AthleteHelper {
        private boolean asking;

        public DietaryGuideHelper(Athlete athlete) {
            super(athlete);
            asking = false;
        }
    }

    @Override
    public SendResponse uniqueStartBlockMessage(Long chatId) {
        if (!helpers().containsKey(chatId)) {
            helpers().put(chatId, new DietaryGuideHelper(athleteService().findById(chatId)));
        }
        return sender().sendResponse(new SendMessage(chatId, "Вы в блоке памятки по питанию")
                .replyMarkup(markup()));
    }

    @Override
    public List<String> buttons() {
        return List.of("Общие наставления");
    }

    @Override
    protected SendResponse switching(Long chatId, Message message, DietaryGuideHelper helper) {
        String txt = message.text();
        switch (txt) {
            case "Назад" -> {
                return goToBack(chatId);
            }
            case "Общие наставления" -> {
                return dietaryGuide(chatId, helper);
            }
        }
        throw new NotValidDataException();
    }

    private SendResponse dietaryGuide(Long chatId, DietaryGuideHelper helper) {
        helper.setWorking(true);
        return msg(chatId,
                trainerService()
                        .findTrainerByAthleteId(chatId)
                        .getDietaryGuide(),
                backMarkup()
                        .addRow("Остался вопрос"));
    }

    @Override
    protected SendResponse work(Long chatId, Message message, DietaryGuideHelper helper) {
        if (helper.asking) return sendQuestion(chatId, message);
        switch (message.text()) {
            case "Остался вопрос" -> {
                return ask(chatId, helper);
            }
            case "Назад" -> {
                return forcedStop(chatId);
            }
        }
        throw new NotValidDataException();
    }

    private SendResponse sendQuestion(Long chatId, Message message) {
        String question = message.text();
        if (message.text().equals(BACK)) {
            return forcedStop(chatId);
        }
        msg(trainerService().findTrainerByAthleteId(chatId).getId(), "Атлет пожелал ответа на вопрос. *" + question + "*");
        msg(chatId, "Вы отправили вопрос тренеру");
        faqService().post(new FAQ(question, chatId));
        return forcedStop(chatId);
    }

    private SendResponse ask(Long chatId, DietaryGuideHelper helper) {
        helper.asking = true;
        return msg(chatId, "Следующим сообщением задайте вопрос", backMarkup());
    }
}
