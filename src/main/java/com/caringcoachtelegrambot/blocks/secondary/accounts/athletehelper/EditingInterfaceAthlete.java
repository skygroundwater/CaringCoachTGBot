package com.caringcoachtelegrambot.blocks.secondary.accounts.athletehelper;

import com.caringcoachtelegrambot.exceptions.NotValidDataException;
import com.caringcoachtelegrambot.models.Athlete;
import com.caringcoachtelegrambot.services.ServiceKeeper;
import com.caringcoachtelegrambot.utils.TelegramSender;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import lombok.Builder;
import org.springframework.stereotype.Component;

import static com.caringcoachtelegrambot.utils.Constants.BACK;

@Component
public class EditingInterfaceAthlete extends AthleteAccountInterface<EditingInterfaceAthlete.EditingHelper> {

    public EditingInterfaceAthlete(TelegramSender sender,
                                   ServiceKeeper serviceKeeper) {
        super(sender, serviceKeeper);
    }

    @Builder
    public static class EditingHelper extends AthleteHelper {
        private String newPass;
        private Athlete athlete;
        private boolean changing;
        private boolean login;
        private boolean waitingOldPassword;
        private boolean confirmedPassword;
        private boolean newPassword;
        private boolean name;
        private boolean secondName;
        private boolean age;
        private boolean height;
        private boolean weight;
    }

    @Override
    public SendResponse uniqueStartBlockMessage(Long chatId) {
        helpers().put(chatId, EditingHelper.builder()
                .athlete(athleteService().findById(chatId)).build());
        return sender().sendResponse(new SendMessage(chatId, "Раздел редактирования")
                .replyMarkup(markup()));
    }

    @Override
    public ReplyKeyboardMarkup markup() {
        return backMarkup()
                .addRow("Изменить логин")
                .addRow("Изменить пароль")
                .addRow("Изменить имя")
                .addRow("Изменить фамилию")
                .addRow("Изменить рост")
                .addRow("Изменить вес")
                .addRow("Изменить возраст");
    }

    @Override
    public SendResponse process(Long chatId, Message message) {
        String txt = message.text();
        EditingHelper helper = helpers().get(chatId);
        if (helper.changing) return change(chatId, message, helper);
        switch (txt) {
            case "Назад" -> {
                return goToBack(chatId);
            }
            case "Изменить логин" -> {
                return setLogin(chatId, helper);
            }
            case "Изменить пароль" -> {
                return setPassword(chatId, helper);
            }
            case "Изменить имя" -> {
                return setName(chatId, helper);
            }
            case "Изменить фамилию" -> {
                return setSecondName(chatId, helper);
            }
            case "Изменить рост" -> {
                return setHeight(chatId, helper);
            }
            case "Изменить вес" -> {
                return setWeight(chatId, helper);
            }
            case "Изменить возраст" -> {
                return setAge(chatId, helper);
            }
            case "Изменить фото профиля" -> {
                return sender().sendResponse(new SendMessage(chatId, "Функционал пока не реализовал"));
            }
        }
        throw new NotValidDataException();
    }

    private SendResponse message(Long chatId, String txt) {
        return sender().sendResponse(new SendMessage(chatId, txt)
                .replyMarkup(backMarkup()));
    }

    private SendResponse setName(Long chatId, EditingHelper helper) {
        helper.name = true;
        helper.changing = true;
        return message(chatId, "Введите новое имя");
    }

    private SendResponse setSecondName(Long chatId, EditingHelper helper) {
        helper.secondName = true;
        helper.changing = true;
        return message(chatId, "Введите новую фамилию");
    }

    private SendResponse setHeight(Long chatId, EditingHelper helper) {
        helper.height = true;
        helper.changing = true;
        return message(chatId, "Введите ваш рост");
    }

    private SendResponse setWeight(Long chatId, EditingHelper helper) {
        helper.weight = true;
        helper.changing = true;
        return message(chatId, "Введите ваш вес");
    }

    private SendResponse setAge(Long chatId, EditingHelper helper) {
        helper.age = true;
        helper.changing = true;
        return message(chatId, "Введите ваш возраст");
    }

    private SendResponse setPassword(Long chatId, EditingHelper helper) {
        helper.waitingOldPassword = true;
        helper.changing = true;
        return message(chatId, "Введите ваш старый пароль");
    }

    private SendResponse confirmOldPass(Long chatId, String oldPass, EditingHelper helper) {
        if (trainerService().getEncoder().matches(oldPass, helper.athlete.getPassword())) {
            helper.confirmedPassword = true;
            return message(chatId, "Введите ваш новый пароль");
        } else return message(chatId, "Вы неверно ввели старый пароль. Попробуйте снова");
    }

    private SendResponse setLogin(Long chatId, EditingHelper helper) {
        helper.login = true;
        helper.changing = true;
        return message(chatId, "Введите новый логин");
    }

    private SendResponse change(Long chatId, Message message, EditingHelper helper) {
        Athlete athlete = helper.athlete;
        String field = message.text();
        if (field.equals(BACK)) {
            return forceChanging(chatId, athlete);
        } else if (helper.login) {
            athlete.setLogin(field);
        } else if (helper.waitingOldPassword && !helper.confirmedPassword && !helper.newPassword) {
            return confirmOldPass(chatId, field, helper);
        } else if (helper.waitingOldPassword && helper.confirmedPassword && !helper.newPassword) {
            return keepNewPass(chatId, field, helper);
        } else if (helper.waitingOldPassword && helper.confirmedPassword && helper.newPassword) {
            if (helper.newPass.equals(field)) {
                athlete.setPassword(trainerService().getEncoder().encode(field));
            } else return message(chatId, "Вы не смогли подтвердить пароль. Попробуйте снова");
        } else if (helper.name) {
            athlete.setFirstName(field);
        } else if (helper.secondName) {
            athlete.setSecondName(field);
        } else if (helper.height) {
            athlete.setHeight(field);
        } else if (helper.weight) {
            athlete.setWeight(field);
        }
        helpers()
                .put(chatId,
                        EditingHelper
                                .builder()
                                .athlete(athleteService().put(athlete))
                                .build());
        message(chatId, "Вы успешно изменили данные");
        return uniqueStartBlockMessage(chatId);
    }

    private SendResponse keepNewPass(Long chatId, String field, EditingHelper helper) {
        helper.newPass = field;
        helper.newPassword = true;
        return message(chatId, "Повторно введите новый пароль");
    }

    private SendResponse forceChanging(Long chatId, Athlete athlete) {
        helpers()
                .put(chatId,
                        EditingHelper
                                .builder()
                                .athlete(athleteService().put(athlete))
                                .build());
        return uniqueStartBlockMessage(chatId);
    }
}
