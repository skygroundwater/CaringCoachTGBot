package com.caringcoachtelegrambot.blocks.secondary.tertiary.accounts.trainerhelper.interfaces;

import com.caringcoachtelegrambot.blocks.secondary.tertiary.accounts.trainerhelper.TrainerHelper;
import com.caringcoachtelegrambot.exceptions.NotValidDataException;
import com.caringcoachtelegrambot.models.Trainer;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.crypto.password.PasswordEncoder;

import static com.caringcoachtelegrambot.utils.Constants.BACK;

public class EditingInterface extends TrainerAccountInterface {

    private boolean changing;

    public EditingInterface(TrainerHelper helper) {
        super(helper);
        changing = false;
    }

    @Setter
    @Getter
    private static class ChangingHelper {

        private Trainer trainer;

        private String newPass;

        private boolean login;

        private boolean oldPassword;

        private boolean newPassword;

        private boolean confirmingPassword;

        private boolean cause;

        private boolean about;

        public ChangingHelper() {
            login = false;
            oldPassword = false;
            newPassword = false;
            confirmingPassword = false;
            cause = false;
            about = false;
            trainer = null;
        }

        public void update() {
            login = false;
            oldPassword = false;
            newPassword = false;
            confirmingPassword = false;
            cause = false;
            about = false;
        }
    }

    private PasswordEncoder encoder() {
        return trainerService().getEncoder();
    }

    private static final ChangingHelper chHelper = new ChangingHelper();

    private SendResponse stopChanging(Long chatId) {
        changing = false;
        chHelper.setTrainer(null);
        return getHelper().getTrainerAccountBlockable().uniqueStartBlockMessage(chatId);
    }

    public SendResponse editAccount(Long chatId, Message message) {
        String function = message.text();
        if (!getHelper().isAccountEditing()) {
            chHelper.setTrainer(trainerService().getTrainer());
            getHelper().setAccountEditing(true);
            return startChanging(chatId);
        }
        if (changing) return changed(chatId, function);
        switch (function) {
            case "Назад" -> {
                return stopChanging(chatId);
            }
            case "Изменить логин" -> {
                return changeLogin(chatId);
            }
            case "Изменить пароль" -> {
                return changePassword(chatId);
            }
            case "Изменить информацию о тренере" -> {
                return changeAbout(chatId);
            }
            case "Изменить причины" -> {
                return changeCause(chatId);
            }
        }
        throw new NotValidDataException();
    }

    private SendResponse changeLogin(Long chatId) {
        changing = true;
        chHelper.setLogin(true);
        return sender().sendResponse(new SendMessage(chatId, "Введи новый логин")
                .replyMarkup(backMarkup()));
    }

    private SendResponse changePassword(Long chatId) {
        changing = true;
        chHelper.setOldPassword(true);
        return sender().sendResponse(new SendMessage(chatId, "Введи старый пароль")
                .replyMarkup(backMarkup()));
    }

    private SendResponse changeAbout(Long chatId) {
        changing = true;
        chHelper.setAbout(true);
        return sender().sendResponse(new SendMessage(chatId, "Введи новую информацию о себе")
                .replyMarkup(backMarkup()));
    }

    private SendResponse changeCause(Long chatId) {
        changing = true;
        chHelper.setCause(true);
        return sender().sendResponse(new SendMessage(chatId, "Введи новые причины")
                .replyMarkup(backMarkup()));
    }

    private SendResponse changed(Long chatId, String value) {
        Trainer trainer = chHelper.getTrainer();
        if (value.equals(BACK)) {
            chHelper.update();
            return startChanging(chatId);
        }
        if (chHelper.login) {
            trainer.setLogin(value);
        } else if (!chHelper.confirmingPassword && !chHelper.newPassword && chHelper.oldPassword) {
            return compareWithActualPassword(chatId, value, trainer);
        } else if (!chHelper.confirmingPassword && chHelper.newPassword && chHelper.oldPassword) {
            return keepNewPassword(chatId, value);
        } else if (chHelper.confirmingPassword && chHelper.newPassword && chHelper.oldPassword) {
            return saveNewPassword(chatId, value, trainer);
        } else if (chHelper.about) {
            trainer.setAbout(value);
        } else if (chHelper.cause) {
            trainer.setCause(value);
        }
        trainerService().setTrainer(trainer);
        changing = false;
        chHelper.update();
        return startChanging(chatId);
    }

    private SendResponse saveNewPassword(Long chatId, String value, Trainer trainer) {
        if (value.equals(chHelper.getNewPass())) {
            trainer.setPassword(encoder().encode(value));
            trainerService().setTrainer(trainer);
            chHelper.update();
            sender().sendResponse(new SendMessage(chatId, "Пароль успешно изменён"));
            changing = false;
            return sendVariablesToChange(chatId);
        } else {
            chHelper.update();
            return sender().sendResponse(new SendMessage(chatId, "Пароли не совпали - повторите весь процесс снова."));
        }
    }

    private SendResponse startChanging(Long chatId) {
        return sendVariablesToChange(chatId);
    }

    private SendResponse keepNewPassword(Long chatId, String newPassword) {
        chHelper.setConfirmingPassword(true);
        chHelper.setNewPass(newPassword);
        return sender().sendResponse(new SendMessage(chatId, """
                Отлично! А теперь подтвердите новый пароль
                """));
    }

    private SendResponse compareWithActualPassword(Long chatId, String comparingPassword, Trainer trainer) {
        if (encoder().matches(comparingPassword, trainer.getPassword())) {
            chHelper.setNewPassword(true);
            return sender().sendResponse(new SendMessage(chatId, """
                    Успешно.
                    Введи новый пароль
                    """));
        } else {
            return sender().sendResponse(new SendMessage(chatId, "Вы пароли не совпали, попробуйте снова")
                    .replyMarkup(backMarkup()));
        }
    }

    private SendResponse sendVariablesToChange(Long chatId) {
        return sender().sendResponse(new SendMessage(chatId, "Выбери что хотела бы изменить")
                .replyMarkup(variablesToChangeMarkup()));
    }

    private ReplyKeyboardMarkup variablesToChangeMarkup() {
        return backMarkup()
                .addRow("Изменить логин")
                .addRow("Изменить пароль")
                .addRow("Изменить информацию о тренере")
                .addRow("Изменить причины");
    }
}
