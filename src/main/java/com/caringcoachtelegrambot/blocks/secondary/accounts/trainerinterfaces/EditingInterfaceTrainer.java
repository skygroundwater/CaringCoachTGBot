package com.caringcoachtelegrambot.blocks.secondary.accounts.trainerinterfaces;

import com.caringcoachtelegrambot.blocks.secondary.accounts.AccountInterface;
import com.caringcoachtelegrambot.blocks.secondary.helpers.accounthelpers.TrainerHelper;
import com.caringcoachtelegrambot.exceptions.NotValidDataException;
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
public class EditingInterfaceTrainer extends AccountInterface<EditingInterfaceTrainer.EditingHelper> {

    public EditingInterfaceTrainer(TelegramSender sender,
                                   ServiceKeeper serviceKeeper) {
        super(sender, serviceKeeper);
    }

    @Setter
    @Getter
    public static class EditingHelper extends TrainerHelper {

        private String newPass;

        private boolean login;

        private boolean oldPassword;

        private boolean newPassword;

        private boolean confirmingPassword;

        private boolean cause;

        private boolean about;

        private boolean guide;

        public EditingHelper(Trainer trainer) {
            super(trainer);
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

    @Override
    public SendResponse uniqueStartBlockMessage(Long chatId) {
        signIn(chatId, new EditingHelper(trainerService().findTrainerById(chatId)));
        return msg(chatId, "Ты можешь изменить данные о себе в этом разделе", markup());
    }

    @Override
    protected SendResponse work(Long chatId, Message message, EditingHelper helper) {
        Trainer trainer = helpers().get(chatId).getTrainer();
        String value = message.text();
        if (value.equals(BACK)) {
            helper.update();
            helper.setWorking(false);
            return uniqueStartBlockMessage(chatId);
        }
        if (helper.login) {
            trainer.setLogin(value);
        } else if (!helper.confirmingPassword && !helper.newPassword && helper.oldPassword) {
            return compareWithActualPassword(chatId, value, trainer, helper);
        } else if (!helper.confirmingPassword && helper.newPassword && helper.oldPassword) {
            return keepNewPassword(chatId, value, helper);
        } else if (helper.confirmingPassword && helper.newPassword && helper.oldPassword) {
            return saveNewPassword(chatId, value, trainer, helper);
        } else if (helper.about) {
            trainer.setAbout(value);
        } else if (helper.cause) {
            trainer.setCause(value);
        } else if (helper.guide) {
            trainer.setDietaryGuide(value);
        }
        trainerService().put(trainer);
        helper.setWorking(false);
        helper.update();
        return uniqueStartBlockMessage(chatId);
    }

    @Override
    public List<String> buttons() {
        return List.of(
                "Изменить логин",
                "Изменить пароль",
                "Изменить информацию о тренере",
                "Изменить причины");
    }

    @Override
    protected SendResponse switching(Long chatId, Message message, EditingHelper helper) {
        String function = message.text();
        switch (function) {
            case "Назад" -> {
                return goToBack(chatId);
            }
            case "Изменить логин" -> {
                return changeLogin(chatId, helper);
            }
            case "Изменить пароль" -> {
                return changePassword(chatId, helper);
            }
            case "Изменить информацию о тренере" -> {
                return changeAbout(chatId, helper);
            }
            case "Изменить причины" -> {
                return changeCause(chatId, helper);
            }
            case "Изменить памятку по питанию" -> {
                return changeGuide(chatId, helper);
            }
        }
        throw new NotValidDataException();
    }

    private SendResponse changeGuide(Long chatId, EditingHelper helper) {
        helper.setWorking(true);
        helper.setGuide(true);
        msg(chatId, helper.getTrainer().getDietaryGuide());
        return msg(chatId, """
                Предыдущее сообщение указывает на то,
                как выглядит твоя памятка на данный момент.
                Можешь скопировать и использовать старую
                памятку для редактирования
                """, backMarkup());
    }

    private SendResponse changeLogin(Long chatId, EditingHelper helper) {
        helper.setWorking(true);
        helper.setLogin(true);
        msg(chatId, helper.getTrainer().getLogin());
        return msg(chatId, """
                Предыдущее сообщение указывает на то, как
                выглядит твой логин на данный момент
                Введи новый логин
                """, backMarkup());
    }

    private SendResponse changePassword(Long chatId, EditingHelper helper) {
        helper.setWorking(true);
        helper.setOldPassword(true);
        return msg(chatId, "Введи старый пароль", backMarkup());
    }

    private SendResponse changeAbout(Long chatId, EditingHelper helper) {
        helper.setWorking(true);
        helper.setAbout(true);
        msg(chatId, helper.getTrainer().getAbout());
        return msg(chatId, "Введи новую информацию о себе", backMarkup());
    }

    private SendResponse changeCause(Long chatId, EditingHelper helper) {
        helper.setWorking(true);
        helper.setCause(true);
        msg(chatId, helper.getTrainer().getCause());
        return msg(chatId, """
                Предыдущее сообщение указывает на то, как
                выглядит твои причины на данный момент
                """, backMarkup());
    }

    private SendResponse keepNewPassword(Long chatId, String newPassword, EditingHelper helper) {
        helper.setConfirmingPassword(true);
        helper.setNewPass(newPassword);
        return msg(chatId, """
                Отлично! А теперь подтвердите новый пароль
                """);
    }

    private SendResponse compareWithActualPassword(Long chatId, String comparingPassword, Trainer trainer, EditingHelper helper) {
        if (trainerService().getEncoder().matches(comparingPassword, trainer.getPassword())) {
            helper.setNewPassword(true);
            return msg(chatId, """
                    Успешно.
                    Введи новый пароль
                    """);
        } else {
            return msg(chatId, "Вы пароли не совпали, попробуйте снова", backMarkup());
        }
    }

    private SendResponse saveNewPassword(Long chatId, String value, Trainer trainer, EditingHelper helper) {
        if (value.equals(helper.getNewPass())) {
            trainer.setPassword(trainerService().getEncoder().encode(value));
            trainerService().put(trainer);
            helper.update();
            msg(chatId, "Пароль успешно изменён");
            helper.setWorking(false);
            return uniqueStartBlockMessage(chatId);
        } else {
            helper.update();
            return msg(chatId, "Пароли не совпали - повторите весь процесс снова.");
        }
    }
}
