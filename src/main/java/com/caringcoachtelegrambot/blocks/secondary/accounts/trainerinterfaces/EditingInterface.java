package com.caringcoachtelegrambot.blocks.secondary.accounts.trainerinterfaces;

import com.caringcoachtelegrambot.exceptions.NotValidDataException;
import com.caringcoachtelegrambot.models.Trainer;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

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

        private boolean guide;

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

    protected SendResponse stop(Long chatId) {
        changing = false;
        chHelper.setTrainer(null);
        return getHelper().getTrainerAccountBlockable().uniqueStartBlockMessage(chatId);
    }

    public SendResponse execute(Long chatId, Message message) {
        String function = message.text();
        if (!getHelper().isAccountEditing()) {
            chHelper.setTrainer(trainerService().getTrainer());
            getHelper().setAccountEditing(true);
            return startChanging(chatId);
        }
        if (changing) return changed(chatId, function);
        else return switching(chatId, message);
    }

    @Override
    protected SendResponse switching(Long chatId, Message message) {
        String function = message.text();
        switch (function) {
            case "Назад" -> {
                return stop(chatId);
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
            case "Изменить памятку по питанию" -> {
                return changeGuide(chatId);
            }
        }
        throw new NotValidDataException();
    }

    private SendResponse changeGuide(Long chatId) {
        changing = true;
        chHelper.setGuide(true);
        msg(chatId, chHelper.trainer.getDietaryGuide());
        return intermediateMsg(chatId, """
                Предыдущее сообщение указывает на то,
                как выглядит твоя памятка на данный момент.
                Можешь скопировать и использовать старую
                памятку для редактирования
                """);
    }

    private SendResponse changeLogin(Long chatId) {
        changing = true;
        chHelper.setLogin(true);
        msg(chatId, chHelper.trainer.getLogin());
        return intermediateMsg(chatId, """
                Предыдущее сообщение указывает на то, как
                выглядит твой логин на данный момент
                Введи новый логин
                """);
    }

    private SendResponse changePassword(Long chatId) {
        changing = true;
        chHelper.setOldPassword(true);
        return intermediateMsg(chatId, "Введи старый пароль");
    }

    private SendResponse changeAbout(Long chatId) {
        changing = true;
        chHelper.setAbout(true);
        msg(chatId, chHelper.trainer.getAbout());
        return intermediateMsg(chatId, "Введи новую информацию о себе");
    }

    private SendResponse changeCause(Long chatId) {
        changing = true;
        chHelper.setCause(true);
        msg(chatId, chHelper.trainer.getCause());
        return intermediateMsg(chatId, """
                Предыдущее сообщение указывает на то, как
                выглядит твои причины на данный момент
                """);
    }

    private SendResponse changed(Long chatId, String value) {
        Trainer trainer = chHelper.getTrainer();
        if (value.equals(BACK)) {
            chHelper.update();
            changing = false;
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
        } else if (chHelper.guide) {
            trainer.setDietaryGuide(value);
        }
        trainerService().put(trainer);
        changing = false;
        chHelper.update();
        return startChanging(chatId);
    }

    private SendResponse saveNewPassword(Long chatId, String value, Trainer trainer) {
        if (value.equals(chHelper.getNewPass())) {
            trainer.setPassword(encoder().encode(value));
            trainerService().put(trainer);
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
            return intermediateMsg(chatId, "Вы пароли не совпали, попробуйте снова");
        }
    }

    private SendResponse sendVariablesToChange(Long chatId) {
        return sender().sendResponse(new SendMessage(chatId, "Выбери что хотела бы изменить")
                .replyMarkup(markup()));
    }

    protected List<String> buttons() {
        return List.of(
                "Изменить логин",
                "Изменить пароль",
                "Изменить информацию о тренере",
                "Изменить причины");
    }
}
