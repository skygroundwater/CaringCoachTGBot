package com.caringcoachtelegrambot.blocks.secondary.accounts;

import com.caringcoachtelegrambot.blocks.parents.AccountBlockable;
import com.caringcoachtelegrambot.blocks.secondary.accounts.trainerhelper.TrainerHelper;
import com.caringcoachtelegrambot.exceptions.NotValidDataException;
import com.caringcoachtelegrambot.services.*;
import com.caringcoachtelegrambot.utils.TelegramSender;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.KeyboardButton;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Getter
public class TrainerAccountBlockable extends AccountBlockable<TrainerHelper> {
    @Autowired
    public TrainerAccountBlockable(TelegramSender telegramSender,
                                   ServiceKeeper serviceKeeper) {
        super(telegramSender, serviceKeeper);
    }

    @Override
    public SendResponse uniqueStartBlockMessage(Long chatId) {
        helpers().put(chatId, new TrainerHelper(
                sender(), getServiceKeeper(), this));
        ReplyKeyboardMarkup markup = markup();
        for (KeyboardButton button : buttonsForTrainer()) {
            markup.addRow(button);
        }
        return sender().sendResponse(new SendMessage(chatId, "Ты в личном кабинете")
                .replyMarkup(markup));
    }

    @Override
    public SendResponse process(Long chatId, Message message) {
        String txt = message.text();
        TrainerHelper helper = helpers().get(chatId);
        if (txt != null) {
            if (helper.isCheckingQuestionnaires()) {
                return helper.checkQuestionnaire(chatId, txt);
            } else if (helper.isAnsweringFAQs()) {
                return helper.answerFAQ(chatId, txt);
            } else if (helper.isAddingNewFaq()) {
                return helper.newFaq(chatId, txt);
            } else if (helper.isMakesTrainingPlan()) {
                return helper.makeTrainingPlan(chatId, message);
            } else if (helper.isAccountEditing()) {
                return helper.editAccount(chatId, message);
            }
            switch (txt) {
                case "Редактировать аккаунт" -> {
                    return helper.editAccount(chatId, message);
                }
                case "Выйти из личного кабинета" -> {
                    return goBack(chatId);
                }
                case "Проверить анкеты" -> {
                    return helper.checkQuestionnaire(chatId, txt);
                }
                case "Ответить на заданные вопросы" -> {
                    return helper.answerFAQ(chatId, txt);
                }
                case "Добавить новый FAQ" -> {
                    return helper.newFaq(chatId, txt);
                }
                case "Тренировки. Расписание" -> {
                    return helper.makeTrainingPlan(chatId, message);
                }
            }
        }
        throw new NotValidDataException();
    }

    private SendResponse goBack(Long chatId) {
        helpers().remove(chatId);
        assert node().getPrevBlockable() instanceof AthleteAccountBlockable;
        AthleteAccountBlockable athleteAccount = (AthleteAccountBlockable) node().getPrevBlockable();
        return athleteAccount.goToBack(chatId);
    }

    private List<KeyboardButton> buttonsForTrainer() {
        return List.of(
                new KeyboardButton("Проверить анкеты"),
                new KeyboardButton("Ответить на заданные вопросы"),
                new KeyboardButton("Добавить новый FAQ"),
                new KeyboardButton("Тренировки. Расписание"));
    }
}